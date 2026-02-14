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
 * Acá centralizamos las credenciales para login UI y API.
 * Si falta algo, fallamos de una con un mensaje claro.
 */
public final class AuthProperties {

    private static final String AUTH_EMAIL_KEY = "auth.email";
    private static final String AUTH_PASSWORD_KEY = "auth.password";
    private static final String DEFAULT_FIRST_NAME = "Daniel";
    private static final String DEFAULT_LAST_NAME = "Automation";

    private AuthProperties() {
    }

    /**
     * @return correo configurado para autenticación.
     */
    public static String email() {
        return requiredProperty(AUTH_EMAIL_KEY);
    }

    /**
     * @return clave configurada para autenticación.
     */
    public static String password() {
        return requiredProperty(AUTH_PASSWORD_KEY);
    }

    /**
     * @return objeto de credenciales listo para login válido.
     */
    public static UserCredentials configuredUserCredentials() {
        return new UserCredentials(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, email(), password());
    }

    /**
     * @return contraseña inválida basada en la clave real.
     */
    public static String invalidPassword() {
        return password() + "_invalid";
    }

    /**
     * Asegura que el usuario configurado sí puede loguearse.
     * Primero intenta login, y si no existe, hace signup y vuelve a loguear.
     *
     * @param authService servicio de auth para llamadas API.
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
     * Lee una propiedad obligatoria y valida que no venga vacía.
     *
     * @param key nombre de la propiedad.
     * @return valor limpio.
     */
    private static String requiredProperty(String key) {
        String value = readFromSerenity(key)
                .or(() -> readFromSystemProperty(key))
                .or(() -> readFromClasspathFile("serenity.conf", key))
                .or(() -> readFromClasspathFile("serenity.properties", key))
                .orElse(null);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Falta la propiedad obligatoria '" + key + "'. "
                            + "Configura serenity.conf o usa -D" + key + "=\"...\"."
            );
        }
        return value.trim();
    }

    /**
     * Busca la propiedad en variables que ya cargó Serenity.
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
     * Busca la propiedad por `-D`.
     */
    private static Optional<String> readFromSystemProperty(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    /**
     * Busca la propiedad en `serenity.conf` o `serenity.properties`.
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
