package ui.pages;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class SignupPage {

    public static final Target FIRST_NAME = Target.the("signup first name").located(By.id("firstName"));
    public static final Target LAST_NAME = Target.the("signup last name").located(By.id("lastName"));
    public static final Target EMAIL = Target.the("signup email").located(By.id("email"));
    public static final Target PASSWORD = Target.the("signup password").located(By.id("password"));
    public static final Target SUBMIT = Target.the("signup submit").located(By.id("submit"));
    public static final Target ERROR_MESSAGE = Target.the("signup error")
            .located(By.cssSelector("[id='error'], .error, .alert, p[role='alert']"));

    private SignupPage() {
    }
}
