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
import ui.pages.LoginPage;

/**
 * Tarea Screenplay para autenticarse desde la pantalla de login.
 */
public class Login implements Task {

    private final String email;
    private final String password;

    /**
     * Construye la acción de login con credenciales dadas.
     */
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Fábrica fluida para creación de la tarea instrumentada por Serenity.
     *
     * @param email email de acceso.
     * @param password contraseña de acceso.
     * @return tarea `Performable`.
     */
    public static Performable with(String email, String password) {
        return instrumented(Login.class, email, password);
    }

    /**
     * Ejecuta el flujo de login con esperas explícitas sobre campos y botón submit.
     */
    @Override
    @Step("{0} logs in with #email")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(LoginPage.EMAIL, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue(email).into(LoginPage.EMAIL),
                Enter.theValue(password).into(LoginPage.PASSWORD),
                WaitUntil.the(LoginPage.SUBMIT, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(LoginPage.SUBMIT)
        );
    }
}
