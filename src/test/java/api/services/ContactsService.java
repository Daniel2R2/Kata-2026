package api.services;

import api.models.ContactRequest;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;

public class ContactsService extends BaseApiService {

    public Response createContact(ContactRequest contactRequest, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .body(contactRequest)
                .when()
                .post("/contacts")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response createContactWithoutToken(ContactRequest contactRequest) {
        return executeApiCall(() -> baseRequest()
                .body(contactRequest)
                .when()
                .post("/contacts")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response listContacts(String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .when()
                .get("/contacts")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response getContactById(String contactId, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .pathParam("contactId", contactId)
                .when()
                .get("/contacts/{contactId}")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response updateContact(String contactId, ContactRequest contactRequest, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .pathParam("contactId", contactId)
                .body(contactRequest)
                .when()
                .patch("/contacts/{contactId}")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response updateContactPut(String contactId, ContactRequest contactRequest, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .pathParam("contactId", contactId)
                .body(contactRequest)
                .when()
                .put("/contacts/{contactId}")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response updateContactPatch(String contactId, ContactRequest contactRequest, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .pathParam("contactId", contactId)
                .body(contactRequest)
                .when()
                .patch("/contacts/{contactId}")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response deleteContact(String contactId, String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .pathParam("contactId", contactId)
                .when()
                .delete("/contacts/{contactId}")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }
}
