package org.thesis.ci;

import java.util.ArrayList;
import java.util.List;

public class CiProfile {
    public String profileId;
    public String context;
    public List<String> permittedSenders = new ArrayList<>();
    public List<String> permittedReceivers = new ArrayList<>();
    public List<String> permittedSubjectCategories = new ArrayList<>();
    public List<String> permittedInformationTypes = new ArrayList<>();
    public List<String> permittedTransmissionPrinciples = new ArrayList<>();
    public List<String> permittedPurposes = new ArrayList<>();
    public Integer maxRetentionDays;
    public Boolean onwardSharingAllowed;

    public CiProfile() {
    }
}
