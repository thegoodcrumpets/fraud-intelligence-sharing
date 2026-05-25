package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.directory.Participant;
import org.thesis.directory.ParticipantDirectory;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;

@ApplicationScoped
public class ReceiverProfileControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.receiver-profile.enabled", defaultValue = "true")
    boolean enabled;

    @Inject
    ParticipantDirectory participantDirectory;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        Optional<Participant> receiver = participantDirectory.find(context.event.receiverInstitutionId);
        if (receiver.isEmpty() || !Boolean.TRUE.equals(receiver.get().enabled)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.RECEIVER_PROFILE_MISMATCH,
                    CiParameter.RECEIVER,
                    getClass().getSimpleName(),
                    "Receiver is unknown or disabled: " + context.event.receiverInstitutionId
            ));
        }
        if (!receiver.get().allowedProfileIds.contains(context.event.ciProfileId)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.RECEIVER_PROFILE_MISMATCH,
                    CiParameter.RECEIVER,
                    getClass().getSimpleName(),
                    "Receiver " + context.event.receiverInstitutionId + " does not accept profile " + context.event.ciProfileId
            ));
        }
        return Optional.empty();
    }
}
