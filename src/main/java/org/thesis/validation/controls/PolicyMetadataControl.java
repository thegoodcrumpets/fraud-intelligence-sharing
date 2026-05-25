package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.ci.CiProfile;
import org.thesis.ci.CiProfileRegistry;
import org.thesis.model.PolicyMetadata;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;

@ApplicationScoped
public class PolicyMetadataControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.policy.enabled", defaultValue = "true")
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
            return Optional.empty();
        }
        CiProfile profile = maybeProfile.get();
        PolicyMetadata policy = context.event.policy;
        if (policy == null || policy.purpose == null || !profile.permittedPurposes.contains(policy.purpose)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.POLICY_PURPOSE_DISALLOWED,
                    CiParameter.TRANSMISSION_PRINCIPLE,
                    getClass().getSimpleName(),
                    "Policy purpose is not permitted by CI profile"
            ));
        }
        if (policy.retentionDays == null || profile.maxRetentionDays == null || policy.retentionDays > profile.maxRetentionDays) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.POLICY_RETENTION_EXCEEDED,
                    CiParameter.TRANSMISSION_PRINCIPLE,
                    getClass().getSimpleName(),
                    "Retention exceeds profile maximum"
            ));
        }
        boolean requestedOnwardSharing = Boolean.TRUE.equals(policy.onwardSharingAllowed);
        boolean profileAllowsOnwardSharing = Boolean.TRUE.equals(profile.onwardSharingAllowed);
        if (requestedOnwardSharing && !profileAllowsOnwardSharing) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.POLICY_ONWARD_SHARING_DISALLOWED,
                    CiParameter.TRANSMISSION_PRINCIPLE,
                    getClass().getSimpleName(),
                    "Onward sharing is not permitted by CI profile"
            ));
        }
        return Optional.empty();
    }
}
