package org.thesis.simulation;

import org.thesis.validation.DecisionStatus;
import org.thesis.validation.RejectionCode;

public class Scenario {
    public String scenarioId;
    public String basePayload;
    public DeviationType deviationType;
    public String targetParticipantId;
    public DecisionStatus expectedDecision;
    public RejectionCode expectedRejectionCode;

    public Scenario() {
    }
}