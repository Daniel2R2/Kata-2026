package stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import api.models.SignupRequest;
import api.services.AuthService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.models.UserCredentials;
import ui.tasks.SignUp;
import utils.DataFactory;

/**
 * Step definitions para registro de usuario en UI.
 * Cubre alta de usuario y rechazo por email duplicado.
 */
public class UiSignupStepDefinitions {

    private static final int SIGNUP_MAX_ATTEMPTS = 10;

    private final AuthService authService = new AuthService();
    private Actor actor;
    private UserCredentials signupCredentials;
    private UserCredentials existingSignupCredentials;

    /**
     * Abre la pantalla de registro de usuarios.
     */
    @Given("que el usuario esta en la pantalla de registro")
    public void usuarioEnPantallaRegistro() {
        actor = OnStage.theActorCalled("UsuarioSignup");
        actor.attemptsTo(Open.url(resolveBaseUrl() + "addUser"));
    }

    /**
     * Precondicion: crea un usuario por API para validar email repetido en UI.
     */
    @Given("que existe un usuario ya registrado para signup UI")
    public void existeUsuarioYaRegistrado() {
        existingSignupCredentials = createUserByApiWithRetry();
    }

    /**
     * Registra un usuario nuevo por UI con retry automatico por email existente.
     */
    @When("se registra por UI con un email unico")
    public void signupConEmailUnico() {
        signupCredentials = signUpByUiWithRetry();
        assertThat(signupCredentials).isNotNull();
    }

    /**
     * Intenta registrar un email existente para comprobar control de duplicados.
     */
    @When("intenta registrarse por UI con un email ya existente")
    public void signupConEmailExistente() {
        actor.attemptsTo(SignUp.with(existingSignupCredentials));
    }

    /**
     * Verifica rechazo de signup esperando explicitamente feedback de UI.
     */
    @Then("debe permanecer en signup o mostrar error")
    public void validaErrorSignupExistente() {
        waitForSignupRejectedFeedback();

        String currentUrl = BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
        String errorMessage = readSignupErrorText();

        boolean remainsInSignup = currentUrl.contains("/addUser");
        boolean displaysError = !errorMessage.isBlank();

        assertThat(remainsInSignup || displaysError).isTrue();
    }

    /**
     * Sincroniza el check negativo para evitar falsos fallos por render tardio.
     */
    private void waitForSignupRejectedFeedback() {
        WebDriverWait wait = new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Duration.ofSeconds(8));
        wait.until(driver -> {
            boolean remainsInSignup = driver.getCurrentUrl().contains("/addUser");
            String errorMessage = readSignupErrorText();
            boolean displaysError = !errorMessage.isBlank();
            return remainsInSignup || displaysError;
        });
    }

    /**
     * Ejecuta signup UI y reintenta cuando el email ya existe.
     */
    private UserCredentials signUpByUiWithRetry() {
        for (int attempt = 1; attempt <= SIGNUP_MAX_ATTEMPTS; attempt++) {
            UserCredentials candidate = DataFactory.uniqueUserCredentials();
            actor.attemptsTo(
                    Open.url(resolveBaseUrl() + "addUser"),
                    SignUp.with(candidate)
            );

            waitForSignupResult();
            if (isInContactList()) {
                return candidate;
            }

            String errorMessage = readSignupErrorText();
            if (isEmailAlreadyExistsMessage(errorMessage) && attempt < SIGNUP_MAX_ATTEMPTS) {
                continue;
            }

            throw new IllegalStateException(
                    "El signup UI no fue exitoso. "
                            + "url=" + BrowseTheWeb.as(actor).getDriver().getCurrentUrl()
                            + ", error=" + errorMessage
            );
        }

        throw new IllegalStateException(
                "No fue posible completar signup UI despues de "
                        + SIGNUP_MAX_ATTEMPTS
                        + " intentos por email existente."
        );
    }

    /**
     * Crea usuario por API y reintenta automaticamente ante email existente.
     */
    private UserCredentials createUserByApiWithRetry() {
        for (int attempt = 1; attempt <= SIGNUP_MAX_ATTEMPTS; attempt++) {
            UserCredentials candidate = DataFactory.uniqueUserCredentials();
            SignupRequest signupRequest = new SignupRequest(
                    candidate.getFirstName(),
                    candidate.getLastName(),
                    candidate.getEmail(),
                    candidate.getPassword()
            );

            Response signupResponse = authService.signUp(signupRequest);
            if (signupResponse.statusCode() == 201) {
                return candidate;
            }

            if (isEmailAlreadyExistsResponse(signupResponse) && attempt < SIGNUP_MAX_ATTEMPTS) {
                continue;
            }

            throw new IllegalStateException(
                    "No se pudo crear usuario para precondicion de signup UI. "
                            + "status=" + signupResponse.statusCode()
                            + ", body=" + signupResponse.asString()
            );
        }

        throw new IllegalStateException(
                "No fue posible crear usuario por API para signup UI despues de "
                        + SIGNUP_MAX_ATTEMPTS
                        + " intentos por email existente."
        );
    }

    /**
     * Espera el resultado de signup: exito en contactList o error en addUser.
     */
    private void waitForSignupResult() {
        WebDriverWait wait = new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Duration.ofSeconds(10));
        wait.until(driver -> {
            String url = driver.getCurrentUrl();
            boolean isSuccess = url.contains("/contactList");
            String errorMessage = readSignupErrorText();
            boolean hasError = !errorMessage.isBlank();
            return isSuccess || hasError;
        });
    }

    /**
     * Determina si la navegacion ya termino en la pantalla de contactos.
     */
    private boolean isInContactList() {
        return BrowseTheWeb.as(actor).getDriver().getCurrentUrl().contains("/contactList");
    }

    /**
     * Detecta mensaje de email duplicado en UI.
     */
    private boolean isEmailAlreadyExistsMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return false;
        }
        String normalized = errorMessage.toLowerCase(Locale.ROOT);
        return normalized.contains("already exists")
                || normalized.contains("already in use")
                || normalized.contains("already been used")
                || normalized.contains("duplicate");
    }

    /**
     * Obtiene texto de error de signup sin lanzar excepcion cuando el elemento no existe.
     */
    private String readSignupErrorText() {
        List<WebElement> errorElements = BrowseTheWeb.as(actor).getDriver()
                .findElements(By.cssSelector("[id='error'], .error, .alert, p[role='alert']"));

        return errorElements.stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse("");
    }

    /**
     * Detecta respuesta API de colision de email.
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

    /**
     * Resuelve la base URL configurable de la aplicacion UI.
     *
     * @return URL base para navegacion de UI.
     */
    private String resolveBaseUrl() {
        return System.getProperty("webdriver.base.url", "https://thinking-tester-contact-list.herokuapp.com/");
    }
}
