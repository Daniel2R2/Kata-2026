package stepdefinitions;

import api.services.AuthService;
import api.services.ContactsService;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import utils.ApiEvidenceFilter;
import utils.ScenarioContext;

/**
 * Hooks globales de Cucumber.
 * Inicializa contexto/actor, adjunta evidencia API y ejecuta limpieza best-effort.
 */
public class Hooks {

    private final ContactsService contactsService = new ContactsService();
    private final AuthService authService = new AuthService();

    /**
     * Configuración previa a cada escenario.
     * Habilita WebDriver, limpia contexto y prepara el escenario Screenplay.
     */
    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ScenarioContext.clear();
        ApiEvidenceFilter.clear();
        OnStage.setTheStage(new OnlineCast());
    }

    /**
     * Post-proceso principal por escenario.
     * Adjunta evidencia API según política y ejecuta limpieza de datos.
     *
     * @param scenario escenario actual ejecutado por Cucumber.
     */
    @After
    public void tearDown(Scenario scenario) {
        attachApiEvidenceIfNeeded(scenario);
        cleanupContactIfPresent();
        cleanupUserIfPresent();
        ApiEvidenceFilter.clear();
        ScenarioContext.clear();
        OnStage.drawTheCurtain();
    }

    /**
     * Adjunta evidencia en el paso donde ocurre fallo para mejorar trazabilidad.
     *
     * @param scenario escenario en ejecución.
     */
    @AfterStep
    public void attachApiEvidenceOnFailedStep(Scenario scenario) {
        if (scenario.isFailed()) {
            ApiEvidenceFilter.attachLastExchangeToSerenity("API request/response (failure)");
        }
    }

    /**
     * Adjunta evidencia API según modo configurado:
     * - `on-failure` (default): solo último intercambio fallido.
     * - `always`: todos los intercambios del escenario.
     */
    private void attachApiEvidenceIfNeeded(Scenario scenario) {
        if (ApiEvidenceFilter.shouldAttachAlwaysEvidence()) {
            ApiEvidenceFilter.attachAllExchangesToSerenity("API request/response");
            return;
        }

        if (scenario.isFailed()) {
            ApiEvidenceFilter.attachLastExchangeToSerenity("API request/response (failure)");
        }
    }

    /**
     * Limpieza best-effort de contacto creado en el escenario.
     * Evita dependencias entre ejecuciones en ambientes volátiles.
     */
    private void cleanupContactIfPresent() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);

        if (token == null || token.isBlank() || contactId == null || contactId.isBlank()) {
            return;
        }

        try {
            contactsService.deleteContact(contactId, token);
        } catch (Exception ignored) {
            // Best-effort cleanup.
        }
    }

    /**
     * Limpieza best-effort del usuario autenticado en contexto.
     */
    private void cleanupUserIfPresent() {
        String token = ScenarioContext.get("token", String.class);
        if (token == null || token.isBlank()) {
            return;
        }

        try {
            authService.deleteCurrentUser(token);
        } catch (Exception ignored) {
            // Best-effort cleanup.
        }
    }
}
