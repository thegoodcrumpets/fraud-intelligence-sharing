package org.thesis.validation;

import java.util.Optional;

public interface ValidationControl {
    Optional<ValidationDecision> validate(ValidationContext context);
}
