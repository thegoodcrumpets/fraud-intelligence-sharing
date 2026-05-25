package org.thesis.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import org.thesis.audit.AuditLogger;
import org.thesis.model.AuditRecord;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.model.ValidationResponse;
import org.thesis.util.DigestUtil;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationDecision;
import org.thesis.validation.ValidationGateway;

@ApplicationScoped
public class InboundEventService {

    @Inject
    Jsonb jsonb;

    @Inject
    ValidationGateway validationGateway;

    @Inject
    AuditLogger auditLogger;

    public ValidationResponse receiveRaw(String rawJson, String declaredDigest) {
        String computedDigest = DigestUtil.sha256(rawJson == null ? "" : rawJson);
        FraudIntelligenceEvent event;
        try {
            event = jsonb.fromJson(rawJson, FraudIntelligenceEvent.class);
        } catch (JsonbException | IllegalArgumentException e) {
            AuditRecord auditRecord = auditLogger.logMalformed("Payload could not be deserialised strictly: " + e.getMessage(), computedDigest);
            return ValidationResponse.rejected(
                    auditRecord.auditId,
                    null,
                    auditRecord.timestamp,
                    computedDigest,
                    auditRecord.rejectionCode,
                    auditRecord.ciParameter,
                    auditRecord.controlName,
                    auditRecord.message
            );
        }

        ValidationContext context = new ValidationContext(event, rawJson, computedDigest, declaredDigest);
        ValidationDecision decision = validationGateway.validate(context);
        AuditRecord auditRecord = auditLogger.log(event, decision, computedDigest);

        if (decision.isRejected()) {
            return ValidationResponse.rejected(
                    auditRecord.auditId,
                    event.eventId,
                    auditRecord.timestamp,
                    computedDigest,
                    decision.rejectionCode,
                    decision.ciParameter,
                    decision.controlName,
                    decision.message
            );
        }

        return ValidationResponse.accepted(auditRecord.auditId, event.eventId, auditRecord.timestamp, computedDigest);
    }
}
