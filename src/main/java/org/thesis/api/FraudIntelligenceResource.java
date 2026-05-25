package org.thesis.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.thesis.model.ValidationResponse;
import org.thesis.validation.DecisionStatus;

@Path("/fraud-intelligence")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FraudIntelligenceResource {

    @Inject
    InboundEventService inboundEventService;

    @POST
    @Path("/events")
    public Response receive(String rawJson, @HeaderParam("X-Payload-Digest") String declaredDigest) {
        ValidationResponse validationResponse = inboundEventService.receiveRaw(rawJson, declaredDigest);
        if (validationResponse.decision == DecisionStatus.ACCEPTED) {
            return Response.accepted(validationResponse).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(validationResponse).build();
    }
}
