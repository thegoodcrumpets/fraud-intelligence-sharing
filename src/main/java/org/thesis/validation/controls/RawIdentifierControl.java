package org.thesis.validation.controls;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.model.SubjectRef;
import org.thesis.validation.CiParameter;
import org.thesis.validation.RejectionCode;
import org.thesis.validation.ValidationContext;
import org.thesis.validation.ValidationControl;
import org.thesis.validation.ValidationDecision;

import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class RawIdentifierControl implements ValidationControl {

    private static final Pattern RAW_IDENTIFIER_PATTERN = Pattern.compile("^(raw:.*|\\d{8}-?\\d{4}|SE\\d{6,}|\\d{10,})$", Pattern.CASE_INSENSITIVE);

    @ConfigProperty(name = "org.thesis.controls.raw-identifier.enabled", defaultValue = "true")
    boolean enabled;

    @Override
    public Optional<ValidationDecision> validate(ValidationContext context) {
        if (!enabled) {
            return Optional.empty();
        }
        SubjectRef subject = context.event.subject;
        if (subject == null) {
            return Optional.empty();
        }
        if (notBlank(subject.rawNationalIdentifier)
                || notBlank(subject.rawAccountNumber)
                || looksRaw(subject.accountToken)
                || looksRaw(subject.customerToken)) {
            return Optional.of(ValidationDecision.rejected(
                    RejectionCode.RAW_IDENTIFIER_LEAKAGE,
                    CiParameter.INFORMATION_TYPE,
                    getClass().getSimpleName(),
                    "Payload contains raw or raw-looking identifier material"
            ));
        }
        return Optional.empty();
    }

    private boolean looksRaw(String value) {
        return notBlank(value) && RAW_IDENTIFIER_PATTERN.matcher(value.trim()).matches();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
