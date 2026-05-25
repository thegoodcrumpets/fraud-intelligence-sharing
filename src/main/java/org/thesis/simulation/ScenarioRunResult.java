package org.thesis.simulation;

import org.thesis.model.ValidationResponse;
import org.thesis.validation.DecisionStatus;
import org.thesis.validation.RejectionCode;

public class ScenarioRunResult {
    public String scenarioId;
    public DeviationType deviationType;
    public DecisionStatus expectedDecision;
    public RejectionCode expectedRejectionCode;
    public DecisionStatus actualDecision;
    public RejectionCode actualRejectionCode;
    public boolean passed;
    public ValidationResponse response;

    public ScenarioRunResult() {
    }
}
