package utils;

import api.models.LoginRequest;
import api.models.SignupRequest;
import api.services.AuthService;
import io.restassured.response.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.serenitybdd.core.Serenity;
import ui.models.UserCredentials;

/**
 * Resolves authentication properties used by UI/API login flows.
 * Fails fast when required credentials are missing.
 */
public final class AuthProperties {

    private static final String AUTH_EMAIL_KEY = "auth.email";
    private static final String AUTH_PASSWORD_KEY = "auth.password";
    private static final String DEFAULT_FIRST_NAME = "Daniel";
    private static final String DEFAULT_LAST_NAME = "Automation";

    private AuthProperties() {
    }

    /**
     * @return configured authentication email.
     */
    public static String email() {
        return requiredProperty(AUTH_EMAIL_KEY);
    }

    /**
     * @return configured authentication password.
     */
    public static String password() {
        return requiredProperty(AUTH_PASSWORD_KEY);
    }

    /**
     * @return reusable credentials object for valid login actions.
     */
    public static UserCredentials configuredUserCredentials() {
        return new UserCredentials(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, email(), password());
    }

    /**
     * @return deterministic invalid password based on configured secret.
     */
    public static String invalidPassword() {
        return password() + "_invalid";
    }

    /**
     * Ensures that configured credentials can log in.
     * Tries login first, then signup + login when the user does not exist.
     *
     * @param authService auth service used to perform API requests.
     */
    public static void ensureConfiguredUserCanLogin(AuthService authService) {
        UserCredentials credentials = configuredUserCredentials();

        Response firstLoginAttempt = authService.login(new LoginRequest(credentials.getEmail(), credentials.getPassword()));
        if (firstLoginAttempt.statusCode() == 200) {
            return;
        }

        Response signupResponse = authService.signUp(new SignupRequest(
                credentials.getFirstName(),
                credentials.getLastName(),
                credentials.getEmail(),
                credentials.getPassword()
        ));

        if (signupResponse.statusCode() == 201) {
            return;
        }

        Response secondLoginAttempt = authService.login(new LoginRequest(credentials.getEmail(), credentials.getPassword()));
        if (secondLoginAttempt.statusCode() == 200) {
            return;
        }

        throw new IllegalStateException(
                "No fue posible autenticar con auth.email/auth.password. "
                        + "Configura estas propiedades en serenity.conf o sobreescribe con "
                        + "-Dauth.email=\"...\" -Dauth.password=\"...\"."
        );
    }

    /**
     * Reads a required Serenity property and validates it is not blank.
     *
     * @param key property name.
     * @return trimmed property value.
     */
    private static String requiredProperty(String key) {
        String value = readFromSerenity(key)
                .or(() -> readFromSystemProperty(key))
                .or(() -> readFromClasspathFile("serenity.conf", key))
                .or(() -> readFromClasspathFile("serenity.properties", key))
                .orElse(null);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required property '" + key + "'. "
                            + "Configura serenity.conf o usa -D" + key + "=\"...\"."
            );
        }
        return value.trim();
    }

    /**
     * Reads property from Serenity environment variables.
     */
    private static Optional<String> readFromSerenity(String key) {
        String value = Serenity.environmentVariables().getValue(key);
        if (value == null || value.isBlank()) {
            value = Serenity.environmentVariables().getProperty(key);
        }
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    /**
     * Reads property from JVM system properties.
     */
    private static Optional<String> readFromSystemProperty(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    /**
     * Reads property from serenity config files located on classpath.
     */
    private static Optional<String> readFromClasspathFile(String resourceName, String key) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(line -> line.startsWith(key))
                        .map(line -> line.replaceFirst("^" + java.util.regex.Pattern.quote(key) + "\\s*[=:]\\s*", ""))
                        .map(value -> value.replace("\"", "").trim())
                        .filter(value -> !value.isBlank())
                        .findFirst();
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }
}
