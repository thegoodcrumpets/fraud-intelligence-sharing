package org.thesis.simulation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thesis.directory.Participant;
import org.thesis.directory.ParticipantDirectory;
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
    ParticipantDirectory participantDirectory;

    @Inject
    ReceiverNodeClient receiverNodeClient;

    public List<ScenarioRunResult> runAll() {
        List<ScenarioRunResult> results = new ArrayList<>();
        for (Scenario scenario : scenarioRepository.all()) {
            results.add(run(scenario));
        }
        return results;
    }

    public ScenarioRunResult run(Scenario scenario) {
        ScenarioRunResult result = new ScenarioRunResult();
        result.scenarioId = scenario.scenarioId;
        result.deviationType = scenario.deviationType;
        result.targetParticipantId = scenario.targetParticipantId;
        result.expectedDecision = scenario.expectedDecision;
        result.expectedRejectionCode = scenario.expectedRejectionCode;

        try {
            FraudIntelligenceEvent base = basePayloadFactory.create(scenario.basePayload);
            String payload = deviationInjector.toJsonWithDeviation(base, scenario.deviationType);

            Participant target = participantDirectory.find(scenario.targetParticipantId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Unknown target participant: " + scenario.targetParticipantId
                    ));

            if (target.nodeUrl == null || target.nodeUrl.isBlank()) {
                throw new IllegalStateException("Participant has no nodeUrl: " + target.participantId);
            }

            result.targetNodeUrl = target.nodeUrl;

            ValidationResponse response = receiverNodeClient.post(target.nodeUrl, payload);

            result.actualDecision = response.decision;
            result.actualRejectionCode = response.rejectionCode;
            result.response = response;
            result.passed = scenario.expectedDecision == response.decision
                    && Objects.equals(scenario.expectedRejectionCode, response.rejectionCode);

            return result;
        } catch (RuntimeException e) {
            result.transportError = e.getMessage();
            result.passed = false;
            return result;
        }
    }
}