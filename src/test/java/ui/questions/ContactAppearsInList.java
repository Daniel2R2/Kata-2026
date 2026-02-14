package ui.questions;

import java.util.Collection;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import ui.models.Contact;
import ui.pages.ContactsPage;

/**
 * Pregunta que valida si un contacto aparece en el listado por nombre completo.
 */
public class ContactAppearsInList implements Question<Boolean> {

    private final Contact contact;

    /**
     * Construye la validación para un contacto esperado.
     *
     * @param contact contacto a buscar.
     */
    public ContactAppearsInList(Contact contact) {
        this.contact = contact;
    }

    /**
     * Fábrica fluida para usar en aserciones Screenplay.
     *
     * @param contact contacto esperado.
     * @return pregunta de visibilidad en listado.
     */
    public static ContactAppearsInList contact(Contact contact) {
        return new ContactAppearsInList(contact);
    }

    /**
     * Evalúa si al menos una fila coincide con el nombre completo esperado.
     */
    @Override
    public Boolean answeredBy(Actor actor) {
        String fullName = contact.getFullName().trim();
        Collection<String> names = Text.ofEach(ContactsPage.CONTACT_NAME_CELLS).answeredBy(actor);
        return names.stream().anyMatch(name -> name.trim().equalsIgnoreCase(fullName));
    }
}
