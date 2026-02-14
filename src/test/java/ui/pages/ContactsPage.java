package ui.pages;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ContactsPage {

    public static final Target ADD_CONTACT_BUTTON = Target.the("add contact button").located(By.id("add-contact"));
    public static final Target FIRST_NAME = Target.the("first name field").located(By.id("firstName"));
    public static final Target LAST_NAME = Target.the("last name field").located(By.id("lastName"));
    public static final Target BIRTHDATE = Target.the("birthdate field").located(By.id("birthdate"));
    public static final Target EMAIL = Target.the("contact email field").located(By.id("email"));
    public static final Target PHONE = Target.the("contact phone field").located(By.id("phone"));
    public static final Target STREET1 = Target.the("street1 field").located(By.id("street1"));
    public static final Target STREET2 = Target.the("street2 field").located(By.id("street2"));
    public static final Target CITY = Target.the("city field").located(By.id("city"));
    public static final Target STATE_PROVINCE = Target.the("state/province field").located(By.id("stateProvince"));
    public static final Target POSTAL_CODE = Target.the("postal code field").located(By.id("postalCode"));
    public static final Target COUNTRY = Target.the("country field").located(By.id("country"));
    public static final Target SAVE_CONTACT_BUTTON = Target.the("save contact button").located(By.id("submit"));
    public static final Target FORM_ERROR_MESSAGE = Target.the("contact form error message")
            .located(By.cssSelector("[id='error'], .error, .alert, p[role='alert']"));

    public static final Target CONTACTS_CONTAINER = Target.the("contacts container")
            .located(By.cssSelector("div.contacts"));
    public static final Target CONTACT_TABLE = Target.the("contact table")
            .located(By.cssSelector("table.contactTable"));
    public static final Target CONTACT_NAME_CELLS = Target.the("contact name cells")
            .locatedBy("//table[@id='myTable']//tr[contains(@class,'contactTableBodyRow')]/td[2]");
    public static final Target CONTACT_NAME_BY_FULL_NAME = Target.the("contact row by full name")
            .locatedBy("//table[@id='myTable']//tr[contains(@class,'contactTableBodyRow')]/td[2][normalize-space()='{0}']");

    private ContactsPage() {
    }
}
