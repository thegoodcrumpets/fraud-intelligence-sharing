package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.ci.CiProfile;
import org.thesis.ci.CiProfileRegistry;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;

@ApplicationScoped
public class CiProfileControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.ci-profile.enabled", defaultValue = "true")
    boolean enabled;

    @Inject
    CiProfileRegistry ciProfileRegistry;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        Optional<CiProfile> maybeProfile = ciProfileRegistry.find(context.event.ciProfileId);
        if (maybeProfile.isEmpty()) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.CI_PROFILE_NOT_FOUND,
                    CiParameter.SCHEMA,
                    getClass().getSimpleName(),
                    "CI profile not found: " + context.event.ciProfileId
            ));
        }
        CiProfile profile = maybeProfile.get();
        if (!profile.permittedSenders.contains(context.event.senderInstitutionId)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.CI_SENDER_MISMATCH,
                    CiParameter.SENDER,
                    getClass().getSimpleName(),
                    "Sender is not permitted by CI profile"
            ));
        }
        if (!profile.permittedReceivers.contains(context.event.receiverInstitutionId)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.CI_RECEIVER_MISMATCH,
                    CiParameter.RECEIVER,
                    getClass().getSimpleName(),
                    "Receiver is not permitted by CI profile"
            ));
        }
        if (context.event.subject == null || !profile.permittedSubjectCategories.contains(context.event.subject.subjectCategory)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.CI_SUBJECT_MISMATCH,
                    CiParameter.SUBJECT,
                    getClass().getSimpleName(),
                    "Subject category is not permitted by CI profile"
            ));
        }
        for (String informationType : context.event.informationTypes) {
            if (!profile.permittedInformationTypes.contains(informationType)) {
                return Optional.of(ValidationDecision.rejected(
                        RejectionCode.CI_INFORMATION_TYPE_DISALLOWED,
                        CiParameter.INFORMATION_TYPE,
                        getClass().getSimpleName(),
                        "Information type is not permitted by CI profile: " + informationType
                ));
            }
        }
        if (!profile.permittedTransmissionPrinciples.contains(context.event.transmissionPrinciple)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.CI_TRANSMISSION_PRINCIPLE_DISALLOWED,
                    CiParameter.TRANSMISSION_PRINCIPLE,
                    getClass().getSimpleName(),
                    "Transmission principle is not permitted by CI profile"
            ));
        }
        return Optional.empty();
    }
}
