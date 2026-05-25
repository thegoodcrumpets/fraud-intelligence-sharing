package org.thesis.auth;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.util.ConfigFileLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class PropertyGrantRegistry {

    @ConfigProperty(name = "org.thesis.config.property-grants")
    String grantsPath;

    @Inject
    ConfigFileLoader fileLoader;

    @Inject
    Jsonb jsonb;

    private final List<PropertyGrant> grants = new CopyOnWriteArrayList<>();

    @PostConstruct
    void load() {
        String json = fileLoader.readResource(grantsPath);
        PropertyGrant[] loaded = jsonb.fromJson(json, PropertyGrant[].class);
        grants.addAll(Arrays.asList(loaded));
    }

    public Optional<PropertyGrant> find(String profileId, String senderInstitutionId, String receiverInstitutionId) {
        return grants.stream()
                .filter(grant -> grant.profileId.equals(profileId))
                .filter(grant -> grant.senderInstitutionId.equals(senderInstitutionId))
                .filter(grant -> grant.receiverInstitutionId.equals(receiverInstitutionId))
                .findFirst();
    }
}
