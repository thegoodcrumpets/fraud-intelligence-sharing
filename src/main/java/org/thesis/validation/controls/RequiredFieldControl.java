package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;

@ApplicationScoped
public class RequiredFieldControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.required-fields.enabled", defaultValue = "true")
    boolean enabled;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        FraudIntelligenceEvent event = context.event;
        if (isBlank(event.schemaVersion) || isBlank(event.eventId) || isBlank(event.ciProfileId)
                || isBlank(event.senderInstitutionId) || isBlank(event.receiverInstitutionId)
                || event.subject == null || event.policy == null || event.fraudSignal == null
                || event.informationTypes == null || event.informationTypes.isEmpty()
                || isBlank(event.transmissionPrinciple)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.MISSING_REQUIRED_FIELD,
                    CiParameter.SCHEMA,
                    getClass().getSimpleName(),
                    "One or more required payload fields are missing"
            ));
        }
        return Optional.empty();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
