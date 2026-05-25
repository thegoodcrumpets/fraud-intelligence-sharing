package org.thesis.simulation;

import jakarta.enterprise.context.ApplicationScoped;
import org.thesis.model.Evidence;
import org.thesis.model.FraudIntelligenceEvent;
import org.thesis.model.FraudSignal;
import org.thesis.model.PolicyMetadata;
import org.thesis.model.SubjectRef;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BasePayloadFactory {

    public FraudIntelligenceEvent create(String basePayload) {
        return switch (basePayload) {
            case "BANK_BASE" -> bankBase("bank-a", "bank-b");
            case "BANK_C_TO_BANK_B_BASE" -> bankBase("bank-c", "bank-b");
            case "CROSS_BASE" -> crossBase("bank-a", "utbetalningsmyndigheten");
            default -> throw new IllegalArgumentException("Unknown base payload: " + basePayload);
        };
    }

    private FraudIntelligenceEvent bankBase(String sender, String receiver) {
        FraudIntelligenceEvent event = common(sender, receiver);
        event.eventId = "evt-bank-base-001";
        event.ciProfileId = "BANKING_FRAUD_INTELLIGENCE_V1";
        event.subject.subjectCategory = "BANK_CUSTOMER";
        event.informationTypes = new ArrayList<>(List.of(
                "ACCOUNT_TOKEN",
                "CUSTOMER_TOKEN",
                "DEVICE_FINGERPRINT_TOKEN",
                "FRAUD_RISK_SCORE",
                "STOLEN_DEVICE_FLAG",
                "PASSWORD_RESET_RECENCY",
                "VELOCITY_LEVEL",
                "COUNTERPARTY_ACCOUNT_TOKEN"
        ));
        event.transmissionPrinciple = "PSD3_FRAUD_PREVENTION";
        event.policy.purpose = "fraud_prevention";
        event.policy.legalBasis = "GDPR_ART_6_1_F_LEGITIMATE_INTEREST";
        event.policy.retentionDays = 90;
        event.fraudSignal.deviceFingerprintToken = "devtok_77bfb0e6a4";
        event.fraudSignal.counterpartyAccountToken = "acctok_counterparty_27f1";
        return event;
    }

    private FraudIntelligenceEvent crossBase(String sender, String receiver) {
        FraudIntelligenceEvent event = common(sender, receiver);
        event.eventId = "evt-cross-base-001";
        event.ciProfileId = "PUBLIC_SECTOR_FRAUD_INTELLIGENCE_V1";
        event.subject.subjectCategory = "BENEFIT_APPLICANT";
        event.informationTypes = new ArrayList<>(List.of(
                "ACCOUNT_TOKEN",
                "CUSTOMER_TOKEN",
                "FRAUD_RISK_SCORE",
                "STOLEN_DEVICE_FLAG",
                "VELOCITY_LEVEL"
        ));
        event.transmissionPrinciple = "PUBLIC_PAYOUT_FRAUD_PREVENTION";
        event.policy.purpose = "public_payout_fraud_prevention";
        event.policy.legalBasis = "GDPR_ART_6_1_E_PUBLIC_TASK";
        event.policy.retentionDays = 30;
        event.fraudSignal.deviceFingerprintToken = null;
        event.fraudSignal.passwordResetRecent = null;
        event.fraudSignal.counterpartyAccountToken = null;
        return event;
    }

    private FraudIntelligenceEvent common(String sender, String receiver) {
        FraudIntelligenceEvent event = new FraudIntelligenceEvent();
        event.schemaVersion = "fraud-intelligence-event-v1";
        event.senderInstitutionId = sender;
        event.receiverInstitutionId = receiver;

        event.subject = new SubjectRef();
        event.subject.accountToken = "acctok_4f9b3a77";
        event.subject.customerToken = "custok_9ec2ab11";

        event.policy = new PolicyMetadata();
        event.policy.onwardSharingAllowed = false;

        event.fraudSignal = new FraudSignal();
        event.fraudSignal.signalType = "SYNTHETIC_SOCIAL_ENGINEERING_RISK";
        event.fraudSignal.riskScore = 87;
        event.fraudSignal.reportedStolenDevice = true;
        event.fraudSignal.passwordResetRecent = true;
        event.fraudSignal.velocityLevel = "HIGH";

        event.evidence = new Evidence();
        event.evidence.firstSeenAt = "2026-05-20T08:15:00Z";
        event.evidence.lastSeenAt = "2026-05-20T09:30:00Z";
        event.evidence.sourceEventCount = 3;
        event.evidence.doraImpactCategory = "CONFIDENTIALITY_INTEGRITY_RELEVANT";
        return event;
    }
}
