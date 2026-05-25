package org.thesis.ci;

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
public class CiProfileRegistry {

    @ConfigProperty(name = "org.thesis.config.ci-profiles")
    String ciProfilesPath;

    @Inject
    ConfigFileLoader fileLoader;

    @Inject
    Jsonb jsonb;

    private final Map<String, CiProfile> profilesById = new ConcurrentHashMap<>();

    @PostConstruct
    void load() {
        String json = fileLoader.readResource(ciProfilesPath);
        CiProfile[] profiles = jsonb.fromJson(json, CiProfile[].class);
        Arrays.stream(profiles).forEach(profile -> profilesById.put(profile.profileId, profile));
    }

    public Optional<CiProfile> find(String profileId) {
        return Optional.ofNullable(profilesById.get(profileId));
    }

    public Map<String, CiProfile> all() {
        return Map.copyOf(profilesById);
    }
}
