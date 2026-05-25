package org.thesis.model;

public class FraudSignal {
    public String signalType;
    public Integer riskScore;

    // Schema-valid, but deliberately not permitted by the CI/profile config.
    public Double confidence;

    public String deviceFingerprintToken;
    public Boolean reportedStolenDevice;
    public Boolean passwordResetRecent;
    public String velocityLevel;
    public String counterpartyAccountToken;

    public FraudSignal() {
    }
}
