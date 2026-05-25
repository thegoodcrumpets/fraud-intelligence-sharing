package org.thesis.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.thesis.simulation.Scenario;
import org.thesis.simulation.ScenarioRepository;
import org.thesis.simulation.ScenarioRunResult;
import org.thesis.simulation.ScenarioRunner;

import java.util.List;

@Path("/simulation")
@Produces(MediaType.APPLICATION_JSON)
public class SimulationResource {

    @Inject
    ScenarioRepository scenarioRepository;

    @Inject
    ScenarioRunner scenarioRunner;

    @GET
    @Path("/scenarios")
    public List<Scenario> scenarios() {
        return scenarioRepository.all();
    }

    @POST
    @Path("/run")
    public List<ScenarioRunResult> run() {
        return scenarioRunner.runAll();
    }
}
