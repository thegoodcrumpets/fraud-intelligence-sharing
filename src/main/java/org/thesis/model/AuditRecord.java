package org.thesis.model;

import org.thesis.validation.CiParameter;
import org.thesis.validation.DecisionStatus;
import org.thesis.validation.RejectionCode;

public class AuditRecord {
    public String auditId;
    public String timestamp;
    public String nodeId;
    public String eventId;
    public String schemaVersion;
    public String ciProfileId;
    public String senderInstitutionId;
    public String receiverInstitutionId;
    public DecisionStatus decision;
    public RejectionCode rejectionCode;
    public CiParameter ciParameter;
    public String controlName;
    public String message;
    public String payloadDigest;
    public String doraImpactCategory;

    public AuditRecord() {
    }
}
