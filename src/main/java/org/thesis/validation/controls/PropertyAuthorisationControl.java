package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.auth.PropertyGrant;
import org.thesis.auth.PropertyGrantRegistry;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PropertyAuthorisationControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.property-authorisation.enabled", defaultValue = "true")
    boolean enabled;

    @Inject
    PropertyGrantRegistry propertyGrantRegistry;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        Optional<PropertyGrant> maybeGrant = propertyGrantRegistry.find(
                context.event.ciProfileId,
                context.event.senderInstitutionId,
                context.event.receiverInstitutionId
        );
        if (maybeGrant.isEmpty()) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.PROPERTY_GRANT_NOT_FOUND,
                    CiParameter.AUTHORISATION,
                    getClass().getSimpleName(),
                    "No property grant exists for sender/receiver/profile combination"
            ));
        }
        List<String> presentProperties = presentProperties(context.event);
        for (String property : presentProperties) {
            if (!maybeGrant.get().allowedProperties.contains(property)) {
                return Optional.of(ValidationDecision.rejected(
                        RejectionCode.PROPERTY_AUTHORISATION_FAILURE,
                        CiParameter.AUTHORISATION,
                        getClass().getSimpleName(),
                        "Property is not authorised for receiver: " + property
                ));
            }
        }
        return Optional.empty();
    }

    private List<String> presentProperties(FraudIntelligenceEvent event) {
        List<String> paths = new ArrayList<>();
        add(paths, "schemaVersion", event.schemaVersion);
        add(paths, "eventId", event.eventId);
        add(paths, "ciProfileId", event.ciProfileId);
        add(paths, "senderInstitutionId", event.senderInstitutionId);
        add(paths, "receiverInstitutionId", event.receiverInstitutionId);
        if (event.subject != null) {
            add(paths, "subject.subjectCategory", event.subject.subjectCategory);
            add(paths, "subject.accountToken", event.subject.accountToken);
            add(paths, "subject.customerToken", event.subject.customerToken);
            add(paths, "subject.rawNationalIdentifier", event.subject.rawNationalIdentifier);
            add(paths, "subject.rawAccountNumber", event.subject.rawAccountNumber);
        }
        if (event.informationTypes != null && !event.informationTypes.isEmpty()) {
            paths.add("informationTypes");
        }
        add(paths, "transmissionPrinciple", event.transmissionPrinciple);
        if (event.policy != null) {
            add(paths, "policy.purpose", event.policy.purpose);
            add(paths, "policy.legalBasis", event.policy.legalBasis);
            add(paths, "policy.retentionDays", event.policy.retentionDays);
            add(paths, "policy.onwardSharingAllowed", event.policy.onwardSharingAllowed);
        }
        if (event.fraudSignal != null) {
            add(paths, "fraudSignal.signalType", event.fraudSignal.signalType);
            add(paths, "fraudSignal.riskScore", event.fraudSignal.riskScore);
            add(paths, "fraudSignal.confidence", event.fraudSignal.confidence);
            add(paths, "fraudSignal.deviceFingerprintToken", event.fraudSignal.deviceFingerprintToken);
            add(paths, "fraudSignal.reportedStolenDevice", event.fraudSignal.reportedStolenDevice);
            add(paths, "fraudSignal.passwordResetRecent", event.fraudSignal.passwordResetRecent);
            add(paths, "fraudSignal.velocityLevel", event.fraudSignal.velocityLevel);
            add(paths, "fraudSignal.counterpartyAccountToken", event.fraudSignal.counterpartyAccountToken);
        }
        if (event.evidence != null) {
            add(paths, "evidence.firstSeenAt", event.evidence.firstSeenAt);
            add(paths, "evidence.lastSeenAt", event.evidence.lastSeenAt);
            add(paths, "evidence.sourceEventCount", event.evidence.sourceEventCount);
            add(paths, "evidence.doraImpactCategory", event.evidence.doraImpactCategory);
        }
        return paths;
    }

    private void add(List<String> paths, String path, Object value) {
        if (value != null) {
            if (value instanceof String stringValue && stringValue.isBlank()) {
                return;
            }
            paths.add(path);
        }
    }
}
