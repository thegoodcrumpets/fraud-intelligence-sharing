package org.thesis.validation;

import org.thesis.model.FraudIntelligenceEvent;

public class ValidationContext {
    public final FraudIntelligenceEvent event;
    public final String rawJson;
    public final String computedDigest;
    public final String declaredDigest;

    public ValidationContext(FraudIntelligenceEvent event, String rawJson, String computedDigest, String declaredDigest) {
        this.event = event;
        this.rawJson = rawJson;
        this.computedDigest = computedDigest;
        this.declaredDigest = declaredDigest;
    }
}
