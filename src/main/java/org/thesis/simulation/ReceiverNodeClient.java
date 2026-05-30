package org.thesis.simulation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.thesis.model.ValidationResponse;
import org.thesis.util.DigestUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ApplicationScoped
public class ReceiverNodeClient {

    @Inject
    Jsonb jsonb;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public ValidationResponse post(String nodeUrl, String rawJson) {
        String digest = DigestUtil.sha256(rawJson == null ? "" : rawJson);

        HttpRequest request = HttpRequest.newBuilder(URI.create(nodeUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-Payload-Digest", digest)
                .POST(HttpRequest.BodyPublishers.ofString(rawJson, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            return jsonb.fromJson(response.body(), ValidationResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not reach receiver node " + nodeUrl, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling receiver node " + nodeUrl, e);
        }
    }
}