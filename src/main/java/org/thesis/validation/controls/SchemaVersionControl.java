package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class SchemaVersionControl implements ValidationControl {

    @ConfigProperty(name = "org.thesis.controls.schema-version.enabled", defaultValue = "true")
    boolean enabled;

    @ConfigProperty(name = "org.thesis.schema.supported-versions")
    String supportedVersions;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        Set<String> supported = Arrays.stream(supportedVersions.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        if (!supported.contains(context.event.schemaVersion)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.UNSUPPORTED_SCHEMA_VERSION,
                    CiParameter.SCHEMA,
                    getClass().getSimpleName(),
                    "Unsupported schemaVersion: " + context.event.schemaVersion
            ));
        }
        return Optional.empty();
    }
}
