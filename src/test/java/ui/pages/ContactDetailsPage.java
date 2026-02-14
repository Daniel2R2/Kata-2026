package ui.pages;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ContactDetailsPage {

    public static final Target FIRST_NAME = Target.the("details first name").located(By.id("firstName"));
    public static final Target LAST_NAME = Target.the("details last name").located(By.id("lastName"));
    public static final Target BIRTHDATE = Target.the("details birthdate").located(By.id("birthdate"));
    public static final Target EMAIL = Target.the("details email").located(By.id("email"));
    public static final Target PHONE = Target.the("details phone").located(By.id("phone"));
    public static final Target STREET1 = Target.the("details street1").located(By.id("street1"));
    public static final Target STREET2 = Target.the("details street2").located(By.id("street2"));
    public static final Target CITY = Target.the("details city").located(By.id("city"));
    public static final Target STATE_PROVINCE = Target.the("details state/province").located(By.id("stateProvince"));
    public static final Target POSTAL_CODE = Target.the("details postal code").located(By.id("postalCode"));
    public static final Target COUNTRY = Target.the("details country").located(By.id("country"));

    public static final Target EDIT_CONTACT_BUTTON = Target.the("edit contact button").located(By.id("edit-contact"));
    public static final Target DELETE_CONTACT_BUTTON = Target.the("delete contact button").located(By.id("delete"));
    public static final Target RETURN_TO_LIST_BUTTON = Target.the("return to contact list").located(By.id("return"));

    private ContactDetailsPage() {
    }
}
