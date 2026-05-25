package org.thesis.validation;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thesis.validation.controls.CiProfileControl;
import org.thesis.validation.controls.DigestControl;
import org.thesis.validation.controls.PolicyMetadataControl;
import org.thesis.validation.controls.PropertyAuthorisationControl;
import org.thesis.validation.controls.RawIdentifierControl;
import org.thesis.validation.controls.ReceiverProfileControl;
import org.thesis.validation.controls.RequiredFieldControl;
import org.thesis.validation.controls.SchemaVersionControl;

import java.util.List;

@ApplicationScoped
public class ValidationGateway {

    @Inject
    RequiredFieldControl requiredFieldControl;

    @Inject
    SchemaVersionControl schemaVersionControl;

    @Inject
    DigestControl digestControl;

    @Inject
    RawIdentifierControl rawIdentifierControl;

    @Inject
    ReceiverProfileControl receiverProfileControl;

    @Inject
    CiProfileControl ciProfileControl;

    @Inject
    PolicyMetadataControl policyMetadataControl;

    @Inject
    PropertyAuthorisationControl propertyAuthorisationControl;

    private List<ValidationControl> orderedControls;

    @PostConstruct
    void orderControls() {
        orderedControls = List.of(
                requiredFieldControl,
                schemaVersionControl,
                digestControl,
                rawIdentifierControl,
                receiverProfileControl,
                ciProfileControl,
                policyMetadataControl,
                propertyAuthorisationControl
        );
    }

    public ValidationDecision validate(ValidationContext context) {
        for (ValidationControl control : orderedControls) {
            var decision = control.validate(context);
            if (decision.isPresent() && decision.get().isRejected()) {
                return decision.get();
            }
        }
        return ValidationDecision.accepted();
    }
}
