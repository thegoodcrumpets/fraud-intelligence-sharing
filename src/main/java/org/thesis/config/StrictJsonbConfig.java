package org.thesis.config;

import io.quarkus.jsonb.JsonbConfigCustomizer;
import jakarta.inject.Singleton;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;

@Singleton
public class StrictJsonbConfig implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig config) {
        config.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }
}
