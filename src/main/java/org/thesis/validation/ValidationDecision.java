package org.thesis.validation;

public class ValidationDecision {
    public final DecisionStatus status;
    public final RejectionCode rejectionCode;
    public final CiParameter ciParameter;
    public final String controlName;
    public final String message;

    private ValidationDecision(DecisionStatus status,
                               RejectionCode rejectionCode,
                               CiParameter ciParameter,
                               String controlName,
                               String message) {
        this.status = status;
        this.rejectionCode = rejectionCode;
        this.ciParameter = ciParameter;
        this.controlName = controlName;
        this.message = message;
    }

    public static ValidationDecision accepted() {
        return new ValidationDecision(DecisionStatus.ACCEPTED, null, CiParameter.NONE, null, "Accepted");
    }

    public static ValidationDecision rejected(RejectionCode rejectionCode,
                                              CiParameter ciParameter,
                                              String controlName,
                                              String message) {
        return new ValidationDecision(DecisionStatus.REJECTED, rejectionCode, ciParameter, controlName, message);
    }

    public boolean isRejected() {
        return status == DecisionStatus.REJECTED;
    }
}
