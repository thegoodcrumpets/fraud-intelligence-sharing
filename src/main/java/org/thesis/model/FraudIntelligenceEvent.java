package org.thesis.model;

import java.util.ArrayList;
import java.util.List;

public class FraudIntelligenceEvent {
    public String schemaVersion;
    public String eventId;
    public String ciProfileId;
    public String senderInstitutionId;
    public String receiverInstitutionId;
    public SubjectRef subject;
    public List<String> informationTypes = new ArrayList<>();
    public String transmissionPrinciple;
    public PolicyMetadata policy;
    public FraudSignal fraudSignal;
    public Evidence evidence;

    public FraudIntelligenceEvent() {
    }
}
