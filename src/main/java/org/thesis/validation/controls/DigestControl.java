package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;

@ApplicationScoped
public class DigestControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.digest.enabled", defaultValue = "true")
    boolean enabled;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled || context.declaredDigest == null || context.declaredDigest.isBlank()) {
            return Optional.empty();
        }
        if (!context.computedDigest.equalsIgnoreCase(context.declaredDigest)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.DIGEST_MISMATCH,
                    CiParameter.AUDIT_INTEGRITY,
                    getClass().getSimpleName(),
                    "X-Payload-Digest did not match the received JSON body"
            ));
        }
        return Optional.empty();
    }
}
