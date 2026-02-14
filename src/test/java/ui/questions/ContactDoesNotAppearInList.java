package ui.questions;

import java.util.Collection;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import ui.models.Contact;
import ui.pages.ContactsPage;

/**
 * Question que confirma ausencia de un contacto en el listado UI.
 */
public class ContactDoesNotAppearInList implements Question<Boolean> {

    private final Contact contact;

    /**
     * Construye la validación para el contacto que no debe existir.
     *
     * @param contact contacto esperado como ausente.
     */
    public ContactDoesNotAppearInList(Contact contact) {
        this.contact = contact;
    }

    /**
     * Fábrica de pregunta para assertions Screenplay.
     *
     * @param contact contacto a buscar como ausente.
     * @return pregunta booleana de ausencia.
     */
    public static ContactDoesNotAppearInList contact(Contact contact) {
        return new ContactDoesNotAppearInList(contact);
    }

    /**
     * Evalúa que ninguna fila del listado coincida con el nombre esperado.
     */
    @Override
    public Boolean answeredBy(Actor actor) {
        String fullName = contact.getFullName().trim();
        Collection<String> names = Text.ofEach(ContactsPage.CONTACT_NAME_CELLS).answeredBy(actor);
        return names.stream().noneMatch(name -> name.trim().equalsIgnoreCase(fullName));
    }
}
