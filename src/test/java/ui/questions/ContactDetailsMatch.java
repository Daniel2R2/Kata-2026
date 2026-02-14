package ui.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import ui.models.Contact;
import ui.pages.ContactDetailsPage;

/**
 * Question que compara datos visibles en detalle de contacto contra un modelo esperado.
 */
public class ContactDetailsMatch implements Question<Boolean> {

    private final Contact expectedContact;

    /**
     * Construye la validación para un contacto esperado.
     *
     * @param expectedContact datos esperados en la UI de detalle.
     */
    public ContactDetailsMatch(Contact expectedContact) {
        this.expectedContact = expectedContact;
    }

    /**
     * Fábrica fluida para aserciones Screenplay.
     *
     * @param contact datos esperados.
     * @return pregunta de comparación.
     */
    public static ContactDetailsMatch with(Contact contact) {
        return new ContactDetailsMatch(contact);
    }

    /**
     * Compara firstName, lastName y opcionalmente email según datos esperados.
     */
    @Override
    public Boolean answeredBy(Actor actor) {
        String firstName = ContactDetailsPage.FIRST_NAME.resolveFor(actor).getText().trim();
        String lastName = ContactDetailsPage.LAST_NAME.resolveFor(actor).getText().trim();
        String birthdate = ContactDetailsPage.BIRTHDATE.resolveFor(actor).getText().trim();
        String email = ContactDetailsPage.EMAIL.resolveFor(actor).getText().trim();
        String phone = ContactDetailsPage.PHONE.resolveFor(actor).getText().trim();
        String street1 = ContactDetailsPage.STREET1.resolveFor(actor).getText().trim();
        String street2 = ContactDetailsPage.STREET2.resolveFor(actor).getText().trim();
        String city = ContactDetailsPage.CITY.resolveFor(actor).getText().trim();
        String stateProvince = ContactDetailsPage.STATE_PROVINCE.resolveFor(actor).getText().trim();
        String postalCode = ContactDetailsPage.POSTAL_CODE.resolveFor(actor).getText().trim();
        String country = ContactDetailsPage.COUNTRY.resolveFor(actor).getText().trim();

        boolean namesMatch = expectedContact.getFirstName().equals(firstName)
                && expectedContact.getLastName().equals(lastName);

        return namesMatch
                && optionalMatches(expectedContact.getBirthdate(), birthdate)
                && optionalMatches(expectedContact.getEmail(), email)
                && optionalMatches(expectedContact.getPhone(), phone)
                && optionalMatches(expectedContact.getStreet1(), street1)
                && optionalMatches(expectedContact.getStreet2(), street2)
                && optionalMatches(expectedContact.getCity(), city)
                && optionalMatches(expectedContact.getStateProvince(), stateProvince)
                && optionalMatches(expectedContact.getPostalCode(), postalCode)
                && optionalMatches(expectedContact.getCountry(), country);
    }

    /**
     * Only enforces exact match when expected value is provided.
     */
    private boolean optionalMatches(String expected, String actual) {
        if (expected == null || expected.isBlank()) {
            return true;
        }
        return expected.trim().equalsIgnoreCase(actual == null ? "" : actual.trim());
    }
}
