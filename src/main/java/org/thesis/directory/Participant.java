package org.thesis.directory;

import java.util.ArrayList;
import java.util.List;

public class Participant {
    public String participantId;
    public String displayName;
    public String participantType;
    public Boolean enabled;
    public List<String> allowedProfileIds = new ArrayList<>();

    public Participant() {
    }
}
