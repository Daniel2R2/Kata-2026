package stepdefinitions;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.containsText;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import api.services.AuthService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.models.Contact;
import ui.models.UserCredentials;
import ui.pages.ContactDetailsPage;
import ui.pages.ContactsPage;
import ui.questions.ContactAppearsInList;
import ui.questions.ContactDetailsMatch;
import ui.questions.ContactDoesNotAppearInList;
import ui.tasks.CreateContact;
import ui.tasks.DeleteCurrentContact;
import ui.tasks.EditContact;
import ui.tasks.Login;
import ui.tasks.OpenContactDetails;
import ui.tasks.SubmitEmptyContactForm;
import utils.AuthProperties;
import utils.DataFactory;
import utils.ScenarioContext;

/**
 * Steps UI para el ciclo de vida de contactos.
 * Implementa creación, consulta, edición, eliminación y validaciones required.
 */
public class UiContactsStepDefinitions {

    private final AuthService authService = new AuthService();
    private Actor actor;

    /**
     * Crea usuario por API e inicia sesión por UI para dejar el escenario listo.
     */
    @Given("que el usuario registrado inicia sesion en la aplicacion")
    public void usuarioRegistradoIniciaSesionEnAplicacion() {
        AuthProperties.ensureConfiguredUserCanLogin(authService);
        UserCredentials user = AuthProperties.configuredUserCredentials();

        actor = OnStage.theActorCalled("GestorContactos");
        actor.attemptsTo(
                Open.url(resolveBaseUrl()),
                Login.with(user.getEmail(), user.getPassword())
        );
    }

    /**
     * Crea un contacto por UI y lo guarda en contexto para pasos posteriores.
     */
    @When("crea un nuevo contacto")
    public void creaUnNuevoContacto() {
        Contact contact = DataFactory.validUiContact();
        ScenarioContext.set("uiContact", contact);
        actor.attemptsTo(CreateContact.with(contact));
    }

    /**
     * Crea un contacto diligenciando todos los campos disponibles en Add Contact.
     */
    @When("crea un nuevo contacto con todos los campos del formulario")
    public void creaUnNuevoContactoConTodosLosCampos() {
        Contact contact = DataFactory.validUiContact();
        ScenarioContext.set("uiContact", contact);
        actor.attemptsTo(CreateContact.with(contact));
    }

    /**
     * Verifica aparición del contacto en listado, con espera explícita.
     */
    @Then("debe visualizar el contacto en el listado")
    public void debeVisualizarContactoEnListado() {
        Contact contact = ScenarioContext.get("uiContact", Contact.class);
        actor.attemptsTo(
                WaitUntil.the(ContactsPage.CONTACTS_CONTAINER, containsText(contact.getFullName()))
                        .forNoMoreThan(15)
                        .seconds()
        );
        actor.should(seeThat(ContactAppearsInList.contact(contact), equalTo(true)));
    }

    /**
     * Abre detalle del contacto recién creado.
     */
    @When("abre el detalle del contacto creado")
    public void abreDetalleDelContacto() {
        Contact contact = ScenarioContext.get("uiContact", Contact.class);
        actor.attemptsTo(OpenContactDetails.forContact(contact));
    }

    /**
     * Valida consistencia de datos en la vista de detalle.
     */
    @Then("debe visualizar el detalle del contacto creado")
    public void validaDetalleContactoCreado() {
        Contact contact = ScenarioContext.get("uiContact", Contact.class);
        actor.attemptsTo(WaitUntil.the(ContactDetailsPage.FIRST_NAME, isVisible()).forNoMoreThan(10).seconds());
        actor.should(seeThat(ContactDetailsMatch.with(contact), equalTo(true)));
    }

    /**
     * Edita contacto con nuevos valores y guarda el estado esperado.
     */
    @When("edita el contacto creado con datos nuevos")
    public void editaContactoCreado() {
        Contact currentContact = ScenarioContext.get("uiContact", Contact.class);
        Contact updatedContact = DataFactory.updatedUiContact();
        ScenarioContext.set("updatedUiContact", updatedContact);
        actor.attemptsTo(EditContact.with(currentContact, updatedContact));
    }

    /**
     * Verifica que la actualización sea persistente en pantalla de detalle.
     */
    @Then("debe visualizar el detalle del contacto editado")
    public void validaDetalleContactoEditado() {
        Contact updatedContact = ScenarioContext.get("updatedUiContact", Contact.class);
        actor.attemptsTo(
                WaitUntil.the(ContactDetailsPage.FIRST_NAME, containsText(updatedContact.getFirstName()))
                        .forNoMoreThan(10)
                        .seconds()
        );
        actor.should(seeThat(ContactDetailsMatch.with(updatedContact), equalTo(true)));
    }

    /**
     * Elimina el contacto desde su detalle aceptando diálogo de confirmación.
     */
    @When("elimina el contacto desde el detalle")
    public void eliminaContactoDesdeDetalle() {
        actor.attemptsTo(DeleteCurrentContact.andAcceptAlert());
    }

    /**
     * Comprueba que el contacto eliminado ya no esté en el listado.
     */
    @Then("no debe visualizar el contacto eliminado en el listado")
    public void validaContactoEliminadoNoVisible() {
        Contact createdContact = ScenarioContext.get("uiContact", Contact.class);
        actor.attemptsTo(
                WaitUntil.the(ContactsPage.CONTACT_NAME_BY_FULL_NAME.of(createdContact.getFullName()), isNotVisible())
                        .forNoMoreThan(15)
                        .seconds()
        );
        actor.should(seeThat(ContactDoesNotAppearInList.contact(createdContact), equalTo(true)));
    }

    /**
     * Intenta guardar contacto vacío para validar reglas required.
     */
    @When("intenta guardar un contacto sin campos requeridos")
    public void intentaGuardarContactoSinCamposRequeridos() {
        actor.attemptsTo(SubmitEmptyContactForm.now());
    }

    /**
     * Espera feedback de rechazo y valida permanencia en formulario o mensaje de error.
     */
    @Then("debe permanecer en crear contacto o mostrar error")
    public void validaErrorCrearContactoRequired() {
        waitForCreateContactRejectedFeedback();

        String browserUrl = BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
        String errorMessage = ContactsPage.FORM_ERROR_MESSAGE.resolveFor(actor).getText();

        boolean remainsInCreateContact = browserUrl.contains("/addContact");
        boolean displaysError = errorMessage != null && !errorMessage.isBlank();

        assertThat(remainsInCreateContact || displaysError).isTrue();
    }

    /**
     * Wait explícito para reducir inestabilidad en validación negativa de creación.
     */
    private void waitForCreateContactRejectedFeedback() {
        WebDriverWait wait = new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Duration.ofSeconds(8));
        wait.until(driver -> {
            boolean remainsInCreateContact = driver.getCurrentUrl().contains("/addContact");
            String errorMessage = ContactsPage.FORM_ERROR_MESSAGE.resolveFor(actor).getText();
            boolean displaysError = errorMessage != null && !errorMessage.isBlank();
            return remainsInCreateContact || displaysError;
        });
    }

    /**
     * Resuelve URL base de UI configurable por propiedad de sistema.
     *
     * @return URL base para navegación de interfaz.
     */
    private String resolveBaseUrl() {
        return System.getProperty("webdriver.base.url", "https://thinking-tester-contact-list.herokuapp.com/");
    }
}
