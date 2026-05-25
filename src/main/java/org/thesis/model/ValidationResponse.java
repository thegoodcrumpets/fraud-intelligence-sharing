package org.thesis.model;

import org.thesis.validation.CiParameter;
import org.thesis.validation.DecisionStatus;
import org.thesis.validation.RejectionCode;

public class ValidationResponse {
    public String auditId;
    public String eventId;
    public String timestamp;
    public DecisionStatus decision;
    public RejectionCode rejectionCode;
    public CiParameter ciParameter;
    public String rejectedByControl;
    public String message;
    public String payloadDigest;

    public ValidationResponse() {
    }

    public static ValidationResponse accepted(String auditId, String eventId, String timestamp, String payloadDigest) {
        ValidationResponse response = new ValidationResponse();
        response.auditId = auditId;
        response.eventId = eventId;
        response.timestamp = timestamp;
        response.decision = DecisionStatus.ACCEPTED;
        response.message = "Accepted by validation gateway";
        response.payloadDigest = payloadDigest;
        return response;
    }

    public static ValidationResponse rejected(String auditId,
                                              String eventId,
                                              String timestamp,
                                              String payloadDigest,
                                              RejectionCode rejectionCode,
                                              CiParameter ciParameter,
                                              String rejectedByControl,
                                              String message) {
        ValidationResponse response = new ValidationResponse();
        response.auditId = auditId;
        response.eventId = eventId;
        response.timestamp = timestamp;
        response.decision = DecisionStatus.REJECTED;
        response.rejectionCode = rejectionCode;
        response.ciParameter = ciParameter;
        response.rejectedByControl = rejectedByControl;
        response.message = message;
        response.payloadDigest = payloadDigest;
        return response;
    }
}
