package ui.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import ui.pages.ContactsPage;

/**
 * Pregunta que confirma si el listado de contactos está visible.
 */
public class ContactListIsVisible implements Question<Boolean> {

    /**
     * Fábrica simple para uso declarativo en assertions Screenplay.
     *
     * @return instancia de la pregunta.
     */
    public static ContactListIsVisible now() {
        return new ContactListIsVisible();
    }

    /**
     * Evalúa presencia del botón de alta como indicador de pantalla lista.
     */
    @Override
    public Boolean answeredBy(Actor actor) {
        return ContactsPage.ADD_CONTACT_BUTTON.resolveFor(actor).isVisible();
    }
}
