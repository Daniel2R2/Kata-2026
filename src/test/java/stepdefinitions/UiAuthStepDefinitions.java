package stepdefinitions;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import api.services.AuthService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.models.UserCredentials;
import ui.pages.LoginPage;
import ui.questions.ContactListIsVisible;
import ui.tasks.Login;
import ui.tasks.SubmitEmptyLoginForm;
import utils.AuthProperties;

/**
 * Steps de autenticacion UI.
 * Prepara un usuario real por API y valida comportamientos de login positivo/negativo.
 */
public class UiAuthStepDefinitions {

    private final AuthService authService = new AuthService();

    private Actor actor;
    private UserCredentials validUser;

    /**
     * Crea un usuario para la prueba y abre la pantalla inicial de login.
     */
    @Given("que el usuario esta en la pantalla de inicio")
    public void usuarioEnPantallaInicio() {
        AuthProperties.ensureConfiguredUserCanLogin(authService);
        validUser = AuthProperties.configuredUserCredentials();

        actor = OnStage.theActorCalled("UsuarioUI");
        actor.attemptsTo(Open.url(resolveBaseUrl()));
    }

    /**
     * Ejecuta login UI con credenciales correctas del usuario recién creado.
     */
    @When("inicia sesion con credenciales validas")
    public void iniciaSesionConCredencialesValidas() {
        currentActor().attemptsTo(Login.with(validUser.getEmail(), validUser.getPassword()));
    }

    /**
     * Verifica que la sesión redirige correctamente al listado de contactos.
     */
    @Then("debe visualizar la lista de contactos")
    public void debeVisualizarListaContactos() {
        currentActor().should(seeThat(ContactListIsVisible.now(), equalTo(true)));
    }

    /**
     * Ejecuta un intento de login con credenciales inválidas.
     */
    @When("inicia sesion con credenciales invalidas")
    public void iniciaSesionConCredencialesInvalidas() {
        currentActor().attemptsTo(Login.with(AuthProperties.email(), AuthProperties.invalidPassword()));
    }

    /**
     * Intenta autenticación vacía para validar reglas required del formulario.
     */
    @When("intenta iniciar sesion sin completar credenciales")
    public void loginSinCredenciales() {
        currentActor().attemptsTo(SubmitEmptyLoginForm.now());
    }

    /**
     * Valida rechazo de autenticación esperando feedback estable (URL o mensaje de error).
     */
    @Then("debe permanecer en login o mostrar error")
    public void validaLoginRechazado() {
        Actor activeActor = currentActor();
        waitForRejectedLoginFeedback(activeActor);

        String currentUrl = BrowseTheWeb.as(activeActor).getDriver().getCurrentUrl();
        String errorMessage = LoginPage.ERROR_MESSAGE.resolveFor(activeActor).getText();

        boolean remainsInLogin = currentUrl.endsWith("/") || currentUrl.contains("/login");
        boolean displaysError = errorMessage != null && !errorMessage.isBlank();

        assertThat(remainsInLogin || displaysError).isTrue();
    }

    /**
     * Espera explícitamente a que exista evidencia de rechazo para evitar flakiness.
     */
    private void waitForRejectedLoginFeedback(Actor activeActor) {
        WebDriverWait wait = new WebDriverWait(BrowseTheWeb.as(activeActor).getDriver(), Duration.ofSeconds(8));
        wait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            boolean remainsInLogin = currentUrl.endsWith("/") || currentUrl.contains("/login");
            String errorMessage = LoginPage.ERROR_MESSAGE.resolveFor(activeActor).getText();
            boolean displaysError = errorMessage != null && !errorMessage.isBlank();
            return remainsInLogin || displaysError;
        });
    }

    /**
     * Obtiene la URL base de UI desde `-Dwebdriver.base.url` o usa el valor por defecto.
     */
    private String resolveBaseUrl() {
        return System.getProperty("webdriver.base.url", "https://thinking-tester-contact-list.herokuapp.com/");
    }

    /**
     * Retorna el actor actual. Si no fue creado localmente, usa el actor en spotlight.
     */
    private Actor currentActor() {
        if (actor != null) {
            return actor;
        }
        return OnStage.theActorInTheSpotlight();
    }
}
