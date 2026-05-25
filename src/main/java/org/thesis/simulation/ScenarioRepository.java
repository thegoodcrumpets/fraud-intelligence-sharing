package org.thesis.simulation;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.thesis.util.ConfigFileLoader;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ScenarioRepository {

    @ConfigProperty(name = "org.thesis.config.scenarios")
    String scenariosPath;

    @Inject
    ConfigFileLoader fileLoader;

    @Inject
    Jsonb jsonb;

    private List<Scenario> scenarios;

    @PostConstruct
    void load() {
        String json = fileLoader.readResource(scenariosPath);
        scenarios = List.copyOf(Arrays.asList(jsonb.fromJson(json, Scenario[].class)));
    }

    public List<Scenario> all() {
        return scenarios;
    }
}
