package stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import api.models.ContactRequest;
import api.services.ContactsService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import utils.DataFactory;
import utils.ScenarioContext;

/**
 * Step definitions para CRUD de contactos por API.
 * Usa contexto de escenario para token, ids y payloads esperados.
 */
public class ApiContactsStepDefinitions {

    private final ContactsService contactsService = new ContactsService();

    private Response createContactResponse;
    private Response createWithoutTokenResponse;
    private Response getContactResponse;
    private Response updateContactResponse;
    private Response updateNonExistingContactResponse;
    private Response createIncompleteResponse;
    private Response getDeletedContactResponse;
    private ContactRequest createdContact;
    private ContactRequest updatedContact;

    /**
     * Crea contacto válido y guarda su id en contexto para pasos de seguimiento.
     */
    @When("crea un contacto con datos validos por API")
    public void creaContactoValidoPorApi() {
        String token = ScenarioContext.get("token", String.class);
        assertThat(token).as("Se esperaba un token en el contexto del escenario").isNotBlank();

        createdContact = DataFactory.validApiContact();
        createContactResponse = contactsService.createContact(createdContact, token);
        assertThat(createContactResponse.statusCode()).isEqualTo(201);

        String contactId = createContactResponse.jsonPath().getString("_id");
        assertThat(contactId).isNotBlank();

        ScenarioContext.set("contactId", contactId);
        ScenarioContext.set("createdContact", createdContact);
    }

    /**
     * Verifica que el contacto creado exista en el listado por id o por nombre completo.
     */
    @Then("el contacto debe aparecer en el listado")
    public void contactoDebeAparecerEnListado() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);
        ContactRequest expectedContact = ScenarioContext.get("createdContact", ContactRequest.class);

        Response listContactsResponse = contactsService.listContacts(token);
        assertThat(listContactsResponse.statusCode()).isEqualTo(200);

        List<Map<String, Object>> contacts = listContactsResponse.jsonPath().getList("$");
        assertThat(contacts).isNotEmpty();

        boolean foundByIdOrName = contacts.stream().anyMatch(contact -> {
            String id = String.valueOf(contact.get("_id"));
            String firstName = String.valueOf(contact.get("firstName"));
            String lastName = String.valueOf(contact.get("lastName"));

            boolean matchById = contactId.equals(id);
            boolean matchByName = expectedContact.getFirstName().equals(firstName)
                    && expectedContact.getLastName().equals(lastName);

            return matchById || matchByName;
        });

        assertThat(foundByIdOrName).isTrue();
    }

    /**
     * Intenta crear contacto sin autorización para validar seguridad.
     */
    @When("intenta crear un contacto por API sin token")
    public void intentaCrearSinToken() {
        createWithoutTokenResponse = contactsService.createContactWithoutToken(DataFactory.validApiContact());
    }

    /**
     * Verifica rechazo por falta de token.
     */
    @Then("la respuesta de crear contacto sin token debe ser 401")
    public void validaCreateContactoSinToken() {
        assertThat(createWithoutTokenResponse.statusCode()).isEqualTo(401);
    }

    /**
     * Consulta por id el contacto creado en el escenario.
     */
    @When("consulta por API el contacto creado por id")
    public void consultaContactoCreadoPorId() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);
        getContactResponse = contactsService.getContactById(contactId, token);
    }

    /**
     * Valida status exitoso de consulta por id.
     */
    @Then("la respuesta del get contacto debe ser 200")
    public void validaGetContactoStatus() {
        assertThat(getContactResponse.statusCode()).isEqualTo(200);
    }

    /**
     * Compara payload consultado contra datos de creación.
     */
    @Then("el payload del contacto consultado debe coincidir con los datos creados")
    public void validaPayloadContactoConsultado() {
        ContactRequest expectedContact = ScenarioContext.get("createdContact", ContactRequest.class);
        assertContactMatchesResponse(expectedContact, getContactResponse);
    }

    /**
     * Actualiza contacto existente con PUT y guarda payload esperado.
     */
    @When("actualiza por API el contacto creado con datos nuevos usando PUT")
    public void actualizaContactoConPut() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);

        updatedContact = DataFactory.updatedApiContact();
        updateContactResponse = contactsService.updateContactPut(contactId, updatedContact, token);
        ScenarioContext.set("updatedContact", updatedContact);
    }

    /**
     * Actualiza contacto existente con PATCH y guarda payload esperado.
     */
    @When("actualiza por API el contacto creado con datos nuevos usando PATCH")
    public void actualizaContactoConPatch() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);

        updatedContact = DataFactory.updatedApiContact();
        updateContactResponse = contactsService.updateContactPatch(contactId, updatedContact, token);
        ScenarioContext.set("updatedContact", updatedContact);
    }

    /**
     * Verifica status exitoso de actualización.
     */
    @Then("la respuesta de actualizar contacto debe ser 200")
    public void validaStatusUpdateContacto() {
        assertThat(updateContactResponse.statusCode()).isEqualTo(200);
    }

    /**
     * Reconsulta contacto y valida persistencia de cambios.
     */
    @Then("los cambios del contacto deben persistir por API")
    public void validaPersistenciaUpdateContacto() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);
        ContactRequest expectedUpdatedContact = ScenarioContext.get("updatedContact", ContactRequest.class);

        Response getAfterUpdate = contactsService.getContactById(contactId, token);
        assertThat(getAfterUpdate.statusCode()).isEqualTo(200);
        assertContactMatchesResponse(expectedUpdatedContact, getAfterUpdate);
    }

    /**
     * Intenta actualizar id inexistente para validar control de errores.
     */
    @When("intenta actualizar por API un contacto inexistente")
    public void actualizaContactoInexistente() {
        String token = ScenarioContext.get("token", String.class);
        String nonExistingContactId = "012345678901234567890123";
        ContactRequest payload = DataFactory.updatedApiContact();

        updateNonExistingContactResponse = contactsService.updateContactPut(nonExistingContactId, payload, token);
    }

    /**
     * Verifica status esperado para actualización sobre recurso no existente.
     */
    @Then("la respuesta de actualizar contacto inexistente debe ser 404")
    public void validaStatusUpdateContactoInexistente() {
        assertThat(updateNonExistingContactResponse.statusCode()).isEqualTo(404);
    }

    /**
     * Intenta crear contacto inválido para cubrir validaciones de payload.
     */
    @When("intenta crear un contacto por API con payload incompleto")
    public void creaContactoIncompleto() {
        String token = ScenarioContext.get("token", String.class);
        createIncompleteResponse = contactsService.createContact(DataFactory.incompleteApiContactMissingLastName(), token);
    }

    /**
     * Valida rechazo por payload incompleto.
     */
    @Then("la respuesta de crear contacto incompleto debe ser 400")
    public void validaStatusCreateIncompleto() {
        assertThat(createIncompleteResponse.statusCode()).isEqualTo(400);
    }

    /**
     * Elimina el contacto creado y guarda status para aserción posterior.
     */
    @When("elimina el contacto creado por API")
    public void eliminaContactoCreado() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);

        Response deleteResponse = contactsService.deleteContact(contactId, token);
        ScenarioContext.set("deleteStatus", deleteResponse.statusCode());
    }

    /**
     * Valida que la eliminación retorne status permitido por contrato.
     */
    @Then("la respuesta de eliminar contacto debe ser 200 or 204")
    public void validaDelete() {
        Integer status = ScenarioContext.get("deleteStatus", Integer.class);
        assertThat(status).isIn(200, 204);
    }

    /**
     * Consulta el recurso eliminado para confirmar ausencia.
     */
    @When("consulta por API el contacto eliminado por id")
    public void consultaContactoEliminadoPorId() {
        String token = ScenarioContext.get("token", String.class);
        String contactId = ScenarioContext.get("contactId", String.class);
        getDeletedContactResponse = contactsService.getContactById(contactId, token);
    }

    /**
     * Verifica que el recurso eliminado no sea recuperable.
     */
    @Then("la respuesta de consultar contacto eliminado debe ser 404")
    public void validaGetDeletedStatus() {
        assertThat(getDeletedContactResponse.statusCode()).isEqualTo(404);
    }

    /**
     * Validates that all populated fields in the expected payload are returned unchanged by the API.
     */
    private void assertContactMatchesResponse(ContactRequest expectedContact, Response response) {
        assertThat(response.jsonPath().getString("firstName")).isEqualTo(expectedContact.getFirstName());
        assertThat(response.jsonPath().getString("lastName")).isEqualTo(expectedContact.getLastName());
        assertThat(response.jsonPath().getString("birthdate")).isEqualTo(expectedContact.getBirthdate());
        assertThat(response.jsonPath().getString("email")).isEqualTo(expectedContact.getEmail());
        assertThat(response.jsonPath().getString("phone")).isEqualTo(expectedContact.getPhone());
        assertThat(response.jsonPath().getString("street1")).isEqualTo(expectedContact.getStreet1());
        assertThat(response.jsonPath().getString("street2")).isEqualTo(expectedContact.getStreet2());
        assertThat(response.jsonPath().getString("city")).isEqualTo(expectedContact.getCity());
        assertThat(response.jsonPath().getString("stateProvince")).isEqualTo(expectedContact.getStateProvince());
        assertThat(response.jsonPath().getString("postalCode")).isEqualTo(expectedContact.getPostalCode());
        assertThat(response.jsonPath().getString("country")).isEqualTo(expectedContact.getCountry());
    }
}
