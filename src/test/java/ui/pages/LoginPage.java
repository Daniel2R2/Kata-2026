package ui.pages;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginPage {

    public static final Target EMAIL = Target.the("email field").located(By.id("email"));
    public static final Target PASSWORD = Target.the("password field").located(By.id("password"));
    public static final Target SUBMIT = Target.the("submit button").located(By.id("submit"));
    public static final Target SIGN_UP = Target.the("sign up button").located(By.id("signup"));
    public static final Target ERROR_MESSAGE = Target.the("error message")
            .located(By.cssSelector("[id='error'], .error, .alert, p[role='alert']"));

    private LoginPage() {
    }
}
