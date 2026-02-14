package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import ui.pages.LoginPage;

/**
 * Task para provocar validaciones required en formulario de login.
 */
public class SubmitEmptyLoginForm implements Task {

    /**
     * Fábrica de tarea instrumentada.
     *
     * @return tarea de envío vacío en login.
     */
    public static Performable now() {
        return instrumented(SubmitEmptyLoginForm.class);
    }

    /**
     * Ejecuta submit sin ingresar credenciales.
     */
    @Override
    @Step("{0} submits login form without credentials")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(LoginPage.SUBMIT, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(LoginPage.SUBMIT)
        );
    }
}
