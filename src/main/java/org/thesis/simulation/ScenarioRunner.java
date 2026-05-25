package org.thesis.simulation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thesis.api.InboundEventService;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.model.ValidationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ScenarioRunner {

    @Inject
    ScenarioRepository scenarioRepository;

    @Inject
    BasePayloadFactory basePayloadFactory;

    @Inject
    DeviationInjector deviationInjector;

    @Inject
    InboundEventService inboundEventService;

    public List<ScenarioRunResult> runAll() {
        List<ScenarioRunResult> results = new ArrayList<>();
        for (Scenario scenario : scenarioRepository.all()) {
            results.add(run(scenario));
        }
        return results;
    }

    public ScenarioRunResult run(Scenario scenario) {
        FraudIntelligenceEvent base = basePayloadFactory.create(scenario.basePayload);
        String payload = deviationInjector.toJsonWithDeviation(base, scenario.deviationType);
        ValidationResponse response = inboundEventService.receiveRaw(payload, null);

        ScenarioRunResult result = new ScenarioRunResult();
        result.scenarioId = scenario.scenarioId;
        result.deviationType = scenario.deviationType;
        result.expectedDecision = scenario.expectedDecision;
        result.expectedRejectionCode = scenario.expectedRejectionCode;
        result.actualDecision = response.decision;
        result.actualRejectionCode = response.rejectionCode;
        result.response = response;
        result.passed = scenario.expectedDecision == response.decision
                && Objects.equals(scenario.expectedRejectionCode, response.rejectionCode);
        return result;
    }
}
