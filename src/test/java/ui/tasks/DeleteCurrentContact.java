package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.pages.ContactDetailsPage;

/**
 * Task Screenplay para eliminar el contacto visible en detalle.
 * Incluye aceptación explícita del diálogo de confirmación del navegador.
 */
public class DeleteCurrentContact implements Task {

    /**
     * Fábrica de tarea instrumentada.
     *
     * @return tarea que elimina y acepta alerta.
     */
    public static Performable andAcceptAlert() {
        return instrumented(DeleteCurrentContact.class);
    }

    /**
     * Ejecuta click en eliminar y confirma el diálogo modal del navegador.
     */
    @Override
    @Step("{0} deletes the current contact and accepts confirmation dialog")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(ContactDetailsPage.DELETE_CONTACT_BUTTON, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(ContactDetailsPage.DELETE_CONTACT_BUTTON)
        );
        acceptConfirmationAlert(actor);
    }

    /**
     * Espera presencia de alerta de confirmación y la acepta.
     */
    private <T extends Actor> void acceptConfirmationAlert(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }
}
