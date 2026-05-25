package org.thesis.simulation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.thesis.model.FraudIntelligenceEvent;

@ApplicationScoped
public class DeviationInjector {

    @Inject
    Jsonb jsonb;

    public String toJsonWithDeviation(FraudIntelligenceEvent event, DeviationType deviationType) {
        switch (deviationType) {
            case NONE -> {
                return jsonb.toJson(event);
            }
            case RAW_IDENTIFIER_LEAKAGE -> {
                event.subject.accountToken = "19800101-1234";
                event.subject.rawNationalIdentifier = "19800101-1234";
                return jsonb.toJson(event);
            }
            case PROFILE_DISALLOWED_FIELD -> {
                event.informationTypes.add("MODEL_CONFIDENCE");
                event.fraudSignal.confidence = 0.99;
                return jsonb.toJson(event);
            }
            case UNSUPPORTED_SCHEMA_VERSION -> {
                event.schemaVersion = "fraud-intelligence-event-v9";
                return jsonb.toJson(event);
            }
            case UNDECLARED_FIELD_ADDITION -> {
                String json = jsonb.toJson(event);
                return json.substring(0, json.length() - 1) + ",\"freeTextNarrative\":\"This field is intentionally outside the contract\"}";
            }
            case RECEIVER_PROFILE_MISMATCH -> {
                event.receiverInstitutionId = "utbetalningsmyndigheten";
                return jsonb.toJson(event);
            }
            case INCOMPATIBLE_POLICY_METADATA -> {
                event.policy.purpose = "marketing_enrichment";
                event.policy.retentionDays = 365;
                return jsonb.toJson(event);
            }
            case PROPERTY_AUTHORISATION_FAILURE -> {
                event.eventId = "evt-dev-6-property-auth";
                event.fraudSignal.deviceFingerprintToken = "devtok_not_granted_to_receiver";
                return jsonb.toJson(event);
            }
            case CROSS_SECTOR_OVER_SHARING -> {
                event.informationTypes.add("COUNTERPARTY_ACCOUNT_TOKEN");
                event.fraudSignal.counterpartyAccountToken = "acctok_cross_sector_overshare";
                return jsonb.toJson(event);
            }
            default -> throw new IllegalArgumentException("Unsupported deviation type: " + deviationType);
        }
    }
}
