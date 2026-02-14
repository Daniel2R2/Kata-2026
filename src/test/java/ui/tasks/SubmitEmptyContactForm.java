package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import ui.pages.ContactsPage;

/**
 * Task para disparar validaciones required de formulario de contactos.
 */
public class SubmitEmptyContactForm implements Task {

    /**
     * Fábrica de tarea instrumentada.
     *
     * @return tarea de envío sin completar campos.
     */
    public static Performable now() {
        return instrumented(SubmitEmptyContactForm.class);
    }

    /**
     * Abre formulario de alta y lo envía vacío para verificar reglas de validación.
     */
    @Override
    @Step("{0} submits empty contact form")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(ContactsPage.ADD_CONTACT_BUTTON, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(ContactsPage.ADD_CONTACT_BUTTON),
                WaitUntil.the(ContactsPage.SAVE_CONTACT_BUTTON, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(ContactsPage.SAVE_CONTACT_BUTTON)
        );
    }
}
