package ui.tasks;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;
import ui.models.UserCredentials;
import ui.pages.SignupPage;

/**
 * Tarea Screenplay para registro de usuario por UI.
 */
public class SignUp implements Task {

    private final UserCredentials credentials;

    /**
     * Construye la tarea de signup con credenciales proporcionadas.
     *
     * @param credentials datos de usuario a registrar.
     */
    public SignUp(UserCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Fábrica de tarea instrumentada para Serenity.
     *
     * @param credentials datos de registro.
     * @return tarea `Performable` de signup.
     */
    public static Performable with(UserCredentials credentials) {
        return instrumented(SignUp.class, credentials);
    }

    /**
     * Ejecuta el llenado y envío del formulario de registro con esperas explícitas.
     */
    @Override
    @Step("{0} signs up with a new user")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(SignupPage.FIRST_NAME, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue(credentials.getFirstName()).into(SignupPage.FIRST_NAME),
                Enter.theValue(credentials.getLastName()).into(SignupPage.LAST_NAME),
                Enter.theValue(credentials.getEmail()).into(SignupPage.EMAIL),
                Enter.theValue(credentials.getPassword()).into(SignupPage.PASSWORD),
                WaitUntil.the(SignupPage.SUBMIT, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(SignupPage.SUBMIT)
        );
    }
}
