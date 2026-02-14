package stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import api.models.LoginRequest;
import api.models.SignupRequest;
import api.services.AuthService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ui.models.UserCredentials;
import utils.AuthProperties;
import utils.DataFactory;
import utils.ScenarioContext;

/**
  Step definitions para autenticación por API.
  Gestiona registro, login, logout y eliminación de usuario.
 */
public class ApiAuthStepDefinitions {

    private static final int SIGNUP_MAX_ATTEMPTS = 10;

    private final AuthService authService = new AuthService();

    private Response signupResponse;
    private Response loginResponse;
    private Response invalidLoginResponse;
    private Response missingFieldsLoginResponse;
    private Response logoutResponse;
    private Response deleteUserResponse;
    private String createdApiUserEmail;

    /**
      Precondición para escenarios que requieren un usuario existente.
     */
    @Given("que existe un usuario registrado por API")
    public void queExisteUsuarioRegistradoPorApi() {
        AuthProperties.ensureConfiguredUserCanLogin(authService);
        ScenarioContext.set("userEmail", AuthProperties.email());
    }

    /**
      Registra y autentica usuario para obtener token válido en contexto.
     */
    @Given("que el usuario se autentica por API y obtiene un token valido")
    public void autenticaPorApi() {
        AuthProperties.ensureConfiguredUserCanLogin(authService);
        loginResponse = authService.login(new LoginRequest(AuthProperties.email(), AuthProperties.password()));
        assertThat(loginResponse.statusCode()).isEqualTo(200);

        String token = loginResponse.jsonPath().getString("token");
        assertThat(token).isNotBlank();

        ScenarioContext.set("token", token);
        ScenarioContext.set("userEmail", AuthProperties.email());
    }

    /**
     * Ejecuta signup dedicado para escenarios de alta de usuario por API.
     */
    @When("registra un usuario nuevo por API")
    public void registraUsuarioNuevoPorApi() {
        registerNewUserByApi();
    }

    /**
     * Verifica código exitoso de creación de usuario.
     */
    @Then("la respuesta de signup por API debe ser 201")
    public void validaSignupExitosoPorApi() {
        assertThat(signupResponse.statusCode()).isEqualTo(201);
    }

    /**
     * Valida que el payload de respuesta contiene el email generado en runtime.
     */
    @Then("el usuario creado por API debe tener email generado")
    public void validaEmailGeneradoEnSignup() {
        assertThat(createdApiUserEmail).isNotBlank();
        String emailResponse = signupResponse.jsonPath().getString("user.email");
        assertThat(emailResponse).isEqualTo(createdApiUserEmail);
    }

    /**
     * Garantiza que el token fue guardado correctamente en contexto.
     */
    @Then("el token de autenticacion no debe estar vacio")
    public void tokenNoVacio() {
        String token = ScenarioContext.get("token", String.class);
        assertThat(token).isNotBlank();
    }

    /**
     * Ejecuta login inválido para cobertura negativa de autenticación.
     */
    @When("intenta autenticarse por API con credenciales invalidas")
    public void intentaAutenticarseConCredencialesInvalidas() {
        invalidLoginResponse = authService.login(new LoginRequest(AuthProperties.email(), AuthProperties.invalidPassword()));
    }

    /**
     * Ejecuta login con payload incompleto para validar contrato de error.
     */
    @When("intenta autenticarse por API con campos faltantes")
    public void loginConCamposFaltantes() {
        String configuredEmail = AuthProperties.email();
        AuthProperties.password();
        Map<String, Object> incompletePayload = new HashMap<>();
        incompletePayload.put("email", configuredEmail);
        missingFieldsLoginResponse = authService.loginWithPayload(incompletePayload);
    }

    /**
     Cierra sesión del usuario autenticado en el escenario actual.
     */
    @When("cierra sesion por API")
    public void cierraSesionPorApi() {
        String token = ScenarioContext.get("token", String.class);
        logoutResponse = authService.logout(token);
    }

    /**
     Elimina el usuario actual autenticado.
     */
    @When("elimina su usuario actual por API")
    public void eliminaUsuarioActualPorApi() {
        String token = ScenarioContext.get("token", String.class);
        deleteUserResponse = authService.deleteCurrentUser(token);
    }

    /**
      Valida status esperado para login inválido.
     */
    @Then("la respuesta de login invalido debe ser 400 o 401")
    public void validaStatusCredencialesInvalidas() {
        assertThat(invalidLoginResponse.statusCode()).isIn(400, 401);
    }

    /**
     Valida status esperado para login con campos faltantes.
     */
    @Then("la respuesta de login con campos faltantes debe ser 400 o 401")
    public void validaStatusCamposFaltantes() {
        assertThat(missingFieldsLoginResponse.statusCode()).isIn(400, 401);
    }

    /**
     Confirma que logout finaliza correctamente la sesión.
     */
    @Then("la respuesta de logout debe ser 200")
    public void validaLogoutStatus() {
        assertThat(logoutResponse.statusCode()).isEqualTo(200);
    }

    /**
     * Confirma eliminación correcta del usuario actual.
     */
    @Then("la respuesta de eliminar usuario actual debe ser 200")
    public void validaDeleteCurrentUserStatus() {
        assertThat(deleteUserResponse.statusCode()).isEqualTo(200);
    }

    /**
     * Registra un usuario nuevo y deja datos clave en contexto de escenario.
     *
     * @return credenciales creadas para operaciones posteriores.
     */
    private UserCredentials registerNewUserByApi() {
        for (int attempt = 1; attempt <= SIGNUP_MAX_ATTEMPTS; attempt++) {
            UserCredentials userCredentials = DataFactory.uniqueUserCredentials();
            SignupRequest signupRequest = new SignupRequest(
                    userCredentials.getFirstName(),
                    userCredentials.getLastName(),
                    userCredentials.getEmail(),
                    userCredentials.getPassword()
            );

            signupResponse = authService.signUp(signupRequest);

            if (signupResponse.statusCode() == 201) {
                createdApiUserEmail = userCredentials.getEmail();
                ScenarioContext.set("userEmail", createdApiUserEmail);
                String token = signupResponse.jsonPath().getString("token");
                if (token != null && !token.isBlank()) {
                    ScenarioContext.set("token", token);
                }
                return userCredentials;
            }

            if (isEmailAlreadyExistsResponse(signupResponse) && attempt < SIGNUP_MAX_ATTEMPTS) {
                continue;
            }

            throw new IllegalStateException(
                    "El signup por API no fue exitoso. "
                            + "status=" + signupResponse.statusCode()
                            + ", body=" + signupResponse.asString()
            );
        }

        throw new IllegalStateException(
                "No fue posible registrar usuario por API despues de "
                        + SIGNUP_MAX_ATTEMPTS
                        + " intentos por email existente."
        );
    }

    /**
     * Detecta respuesta de signup fallida por colision de email.
     */
    private boolean isEmailAlreadyExistsResponse(Response response) {
        int statusCode = response.statusCode();
        if (statusCode != 400 && statusCode != 409 && statusCode != 422) {
            return false;
        }
        String body = response.asString().toLowerCase(Locale.ROOT);
        return body.contains("already exists")
                || body.contains("already in use")
                || body.contains("already been used")
                || body.contains("duplicate");
    }
}
