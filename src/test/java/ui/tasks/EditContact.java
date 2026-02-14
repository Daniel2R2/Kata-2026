package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.models.Contact;
import ui.pages.ContactDetailsPage;
import ui.pages.EditContactPage;

/**
 * Task Screenplay para edición de contacto.
 * Espera carga inicial de datos antes de sobrescribir para evitar condiciones de carrera.
 */
public class EditContact implements Task {

    private final Contact currentContact;
    private final Contact updatedContact;

    /**
     * Construye la edición partiendo de un contacto actual hacia datos actualizados.
     *
     * @param currentContact estado previo esperado en formulario de edición.
     * @param updatedContact nuevos valores a persistir.
     */
    public EditContact(Contact currentContact, Contact updatedContact) {
        this.currentContact = currentContact;
        this.updatedContact = updatedContact;
    }

    /**
     * Fábrica de tarea instrumentada por Serenity.
     *
     * @param currentContact contacto visible actualmente.
     * @param updatedContact datos objetivo.
     * @return tarea `Performable`.
     */
    public static Performable with(Contact currentContact, Contact updatedContact) {
        return instrumented(EditContact.class, currentContact, updatedContact);
    }

    /**
     * Navega a edición, sincroniza carga y guarda los nuevos datos del contacto.
     */
    @Override
    @Step("{0} edits a contact")
    public <T extends Actor> void performAs(T actor) {
        List<Performable> steps = new ArrayList<>();
        steps.add(WaitUntil.the(ContactDetailsPage.EDIT_CONTACT_BUTTON, isClickable()).forNoMoreThan(10).seconds());
        steps.add(Click.on(ContactDetailsPage.EDIT_CONTACT_BUTTON));
        steps.add(WaitUntil.the(EditContactPage.FIRST_NAME, isVisible()).forNoMoreThan(10).seconds());
        actor.attemptsTo(steps.toArray(new Performable[0]));
        waitForOriginalDataToLoad(actor);
        steps.clear();

        steps.add(Clear.field(EditContactPage.FIRST_NAME));
        steps.add(Enter.theValue(updatedContact.getFirstName()).into(EditContactPage.FIRST_NAME));
        steps.add(Clear.field(EditContactPage.LAST_NAME));
        steps.add(Enter.theValue(updatedContact.getLastName()).into(EditContactPage.LAST_NAME));
        setOptionalField(steps, EditContactPage.BIRTHDATE, updatedContact.getBirthdate());
        setOptionalField(steps, EditContactPage.EMAIL, updatedContact.getEmail());
        setOptionalField(steps, EditContactPage.PHONE, updatedContact.getPhone());
        setOptionalField(steps, EditContactPage.STREET1, updatedContact.getStreet1());
        setOptionalField(steps, EditContactPage.STREET2, updatedContact.getStreet2());
        setOptionalField(steps, EditContactPage.CITY, updatedContact.getCity());
        setOptionalField(steps, EditContactPage.STATE_PROVINCE, updatedContact.getStateProvince());
        setOptionalField(steps, EditContactPage.POSTAL_CODE, updatedContact.getPostalCode());
        setOptionalField(steps, EditContactPage.COUNTRY, updatedContact.getCountry());

        steps.add(Click.on(EditContactPage.SUBMIT));
        actor.attemptsTo(steps.toArray(new Performable[0]));
    }

    /**
     * Espera a que la UI cargue valores previos antes de editar.
     * Esto evita sobrescribir durante render asíncrono del formulario.
     */
    private <T extends Actor> void waitForOriginalDataToLoad(T actor) {
        if (currentContact == null || currentContact.getFirstName() == null || currentContact.getFirstName().isBlank()) {
            return;
        }

        WebDriverWait wait = new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Duration.ofSeconds(10));
        wait.until(driver -> {
            String currentValue = EditContactPage.FIRST_NAME.resolveFor(actor).getValue();
            return currentContact.getFirstName().equals(currentValue);
        });
    }

    /**
     * Clears and updates optional field when value is provided.
     * If value is blank, the field stays cleared.
     */
    private void setOptionalField(List<Performable> steps, Target field, String value) {
        if (value == null) {
            return;
        }
        steps.add(Clear.field(field));
        if (!value.isBlank()) {
            steps.add(Enter.theValue(value).into(field));
        }
    }
}
