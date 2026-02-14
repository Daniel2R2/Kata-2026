package ui.pages;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class EditContactPage {

    public static final Target FIRST_NAME = Target.the("edit first name").located(By.id("firstName"));
    public static final Target LAST_NAME = Target.the("edit last name").located(By.id("lastName"));
    public static final Target BIRTHDATE = Target.the("edit birthdate").located(By.id("birthdate"));
    public static final Target EMAIL = Target.the("edit email").located(By.id("email"));
    public static final Target PHONE = Target.the("edit phone").located(By.id("phone"));
    public static final Target STREET1 = Target.the("edit street1").located(By.id("street1"));
    public static final Target STREET2 = Target.the("edit street2").located(By.id("street2"));
    public static final Target CITY = Target.the("edit city").located(By.id("city"));
    public static final Target STATE_PROVINCE = Target.the("edit state/province").located(By.id("stateProvince"));
    public static final Target POSTAL_CODE = Target.the("edit postal code").located(By.id("postalCode"));
    public static final Target COUNTRY = Target.the("edit country").located(By.id("country"));
    public static final Target SUBMIT = Target.the("edit submit").located(By.id("submit"));
    public static final Target ERROR_MESSAGE = Target.the("edit error")
            .located(By.cssSelector("[id='error'], .error, .alert, p[role='alert']"));

    private EditContactPage() {
    }
}
