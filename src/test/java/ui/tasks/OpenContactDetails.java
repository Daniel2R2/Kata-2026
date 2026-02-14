package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import ui.models.Contact;
import ui.pages.ContactsPage;

/**
 * Task Screenplay para abrir detalle de un contacto desde la tabla.
 */
public class OpenContactDetails implements Task {

    private final Contact contact;

    /**
     * Construye la tarea con el contacto objetivo.
     *
     * @param contact contacto a localizar en listado.
     */
    public OpenContactDetails(Contact contact) {
        this.contact = contact;
    }

    /**
     * Fábrica de tarea instrumentada.
     *
     * @param contact contacto objetivo.
     * @return tarea `Performable`.
     */
    public static Performable forContact(Contact contact) {
        return instrumented(OpenContactDetails.class, contact);
    }

    /**
     * Busca el nombre completo en tabla y abre su vista de detalle.
     */
    @Override
    @Step("{0} opens details for contact")
    public <T extends Actor> void performAs(T actor) {
        String fullName = contact.getFullName();
        actor.attemptsTo(
                WaitUntil.the(ContactsPage.CONTACT_NAME_BY_FULL_NAME.of(fullName), isClickable())
                        .forNoMoreThan(15)
                        .seconds(),
                Click.on(ContactsPage.CONTACT_NAME_BY_FULL_NAME.of(fullName))
        );
    }
}
