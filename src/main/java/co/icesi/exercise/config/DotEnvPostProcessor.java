package co.icesi.exercise.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads variables from a .env file in the project root into the Spring
 * Environment before any other property sources are resolved.
 *
 * This is the correct approach for Spring Boot 4.x, which does not
 * auto-load .env files. The processor is registered via
 * META-INF/spring.factories and runs at HIGHEST_PRECEDENCE so that
 * placeholder resolution (${DB_URL}, etc.) works in application.properties.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenvFile";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        File envFile = new File(".env");
        if (!envFile.exists()) return;

        Map<String, Object> properties = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int idx = line.indexOf('=');
                if (idx < 0) continue;

                String key   = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();

                // Strip optional surrounding quotes  "value" or 'value'
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                         || (value.startsWith("'")  && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }

                properties.put(key, value);
            }
        } catch (Exception e) {
            // If .env cannot be read, continue without it (CI/CD uses real env vars)
            return;
        }

        if (!properties.isEmpty()) {
            // Add with lower priority than OS environment variables so that real
            // env vars always win over the .env file.
            environment.getPropertySources().addLast(
                    new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }
}
