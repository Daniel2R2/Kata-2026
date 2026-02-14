package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import java.util.ArrayList;
import java.util.List;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;
import ui.models.Contact;
import ui.pages.ContactsPage;

/**
 * Task Screenplay para crear contactos desde la UI.
 * Soporta opcionalidad de email y teléfono para pruebas negativas/positivas.
 */
public class CreateContact implements Task {

    private final Contact contact;

    /**
     * Construye la tarea para crear el contacto recibido.
     *
     * @param contact datos de contacto a persistir.
     */
    public CreateContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Fábrica fluida de tarea instrumentada.
     *
     * @param contact contacto a crear.
     * @return tarea `Performable`.
     */
    public static Performable with(Contact contact) {
        return instrumented(CreateContact.class, contact);
    }

    /**
     * Ejecuta apertura de formulario, ingreso de datos y guardado.
     */
    @Override
    @Step("{0} creates a new contact")
    public <T extends Actor> void performAs(T actor) {
        List<Performable> steps = new ArrayList<>();

        steps.add(WaitUntil.the(ContactsPage.ADD_CONTACT_BUTTON, isClickable()).forNoMoreThan(10).seconds());
        steps.add(Click.on(ContactsPage.ADD_CONTACT_BUTTON));
        steps.add(WaitUntil.the(ContactsPage.FIRST_NAME, isVisible()).forNoMoreThan(10).seconds());
        steps.add(Enter.theValue(contact.getFirstName()).into(ContactsPage.FIRST_NAME));
        steps.add(Enter.theValue(contact.getLastName()).into(ContactsPage.LAST_NAME));

        if (contact.getBirthdate() != null && !contact.getBirthdate().isBlank()) {
            steps.add(Enter.theValue(contact.getBirthdate()).into(ContactsPage.BIRTHDATE));
        }

        if (contact.getEmail() != null && !contact.getEmail().isBlank()) {
            steps.add(Enter.theValue(contact.getEmail()).into(ContactsPage.EMAIL));
        }

        if (contact.getPhone() != null && !contact.getPhone().isBlank()) {
            steps.add(Enter.theValue(contact.getPhone()).into(ContactsPage.PHONE));
        }

        if (contact.getStreet1() != null && !contact.getStreet1().isBlank()) {
            steps.add(Enter.theValue(contact.getStreet1()).into(ContactsPage.STREET1));
        }

        if (contact.getStreet2() != null && !contact.getStreet2().isBlank()) {
            steps.add(Enter.theValue(contact.getStreet2()).into(ContactsPage.STREET2));
        }

        if (contact.getCity() != null && !contact.getCity().isBlank()) {
            steps.add(Enter.theValue(contact.getCity()).into(ContactsPage.CITY));
        }

        if (contact.getStateProvince() != null && !contact.getStateProvince().isBlank()) {
            steps.add(Enter.theValue(contact.getStateProvince()).into(ContactsPage.STATE_PROVINCE));
        }

        if (contact.getPostalCode() != null && !contact.getPostalCode().isBlank()) {
            steps.add(Enter.theValue(contact.getPostalCode()).into(ContactsPage.POSTAL_CODE));
        }

        if (contact.getCountry() != null && !contact.getCountry().isBlank()) {
            steps.add(Enter.theValue(contact.getCountry()).into(ContactsPage.COUNTRY));
        }

        steps.add(Click.on(ContactsPage.SAVE_CONTACT_BUTTON));
        actor.attemptsTo(steps.toArray(new Performable[0]));
    }
}
