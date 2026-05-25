package org.thesis.directory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.util.ConfigFileLoader;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ParticipantDirectory {

    @ConfigProperty(name = "org.thesis.config.participants")
    String participantsPath;

    @Inject
    ConfigFileLoader fileLoader;

    @Inject
    Jsonb jsonb;

    private final Map<String, Participant> participantsById = new ConcurrentHashMap<>();

    @PostConstruct
    void load() {
        String json = fileLoader.readResource(participantsPath);
        Participant[] participants = jsonb.fromJson(json, Participant[].class);
        Arrays.stream(participants).forEach(participant -> participantsById.put(participant.participantId, participant));
    }

    public Optional<Participant> find(String participantId) {
        return Optional.ofNullable(participantsById.get(participantId));
    }

    public Map<String, Participant> all() {
        return Map.copyOf(participantsById);
    }
}
