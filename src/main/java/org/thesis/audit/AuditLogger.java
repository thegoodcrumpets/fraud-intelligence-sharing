package org.thesis.audit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.model.AuditRecord;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.validation.ValidationDecision;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class AuditLogger {

    @ConfigProperty(name = "org.thesis.node.id", defaultValue = "unknown-node")
    String nodeId;

    @ConfigProperty(name = "org.thesis.audit.file", defaultValue = "target/audit/audit.jsonl")
    String auditFile;

    @Inject
    Jsonb jsonb;

    public AuditRecord log(FraudIntelligenceEvent event, ValidationDecision decision, String payloadDigest) {
        AuditRecord record = baseRecord(event, decision, payloadDigest);
        append(record);
        return record;
    }

    public AuditRecord logMalformed(String message, String payloadDigest) {
        AuditRecord record = new AuditRecord();
        record.auditId = UUID.randomUUID().toString();
        record.timestamp = Instant.now().toString();
        record.nodeId = nodeId;
        record.decision = org.thesis.validation.DecisionStatus.REJECTED;
        record.rejectionCode = org.thesis.validation.RejectionCode.UNDECLARED_FIELD;
        record.ciParameter = org.thesis.validation.CiParameter.SCHEMA;
        record.controlName = "StrictJsonbDeserialisation";
        record.message = message;
        record.payloadDigest = payloadDigest;
        append(record);
        return record;
    }

    private AuditRecord baseRecord(FraudIntelligenceEvent event, ValidationDecision decision, String payloadDigest) {
        AuditRecord record = new AuditRecord();
        record.auditId = UUID.randomUUID().toString();
        record.timestamp = Instant.now().toString();
        record.nodeId = nodeId;
        record.eventId = event.eventId;
        record.schemaVersion = event.schemaVersion;
        record.ciProfileId = event.ciProfileId;
        record.senderInstitutionId = event.senderInstitutionId;
        record.receiverInstitutionId = event.receiverInstitutionId;
        record.decision = decision.status;
        record.rejectionCode = decision.rejectionCode;
        record.ciParameter = decision.ciParameter;
        record.controlName = decision.controlName;
        record.message = decision.message;
        record.payloadDigest = payloadDigest;
        if (event.evidence != null) {
            record.doraImpactCategory = event.evidence.doraImpactCategory;
        }
        return record;
    }

    private void append(AuditRecord record) {
        try {
            Path path = Path.of(auditFile);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    path,
                    jsonb.toJson(record) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("Could not write audit record", e);
        }
    }
}
