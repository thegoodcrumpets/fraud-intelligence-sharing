package org.thesis.auth;

import java.util.ArrayList;
import java.util.List;

public class PropertyGrant {
    public String profileId;
    public String senderInstitutionId;
    public String receiverInstitutionId;
    public List<String> allowedProperties = new ArrayList<>();

    public PropertyGrant() {
    }
}
