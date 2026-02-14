package api.services;

import api.models.LoginRequest;
import api.models.SignupRequest;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import java.util.Map;

public class AuthService extends BaseApiService {

    public Response signUp(SignupRequest signupRequest) {
        return executeApiCall(() -> baseRequest()
                .body(signupRequest)
                .when()
                .post("/users")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response login(LoginRequest loginRequest) {
        return executeApiCall(() -> baseRequest()
                .body(loginRequest)
                .when()
                .post("/users/login")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response loginWithPayload(Map<String, Object> payload) {
        return executeApiCall(() -> baseRequest()
                .body(payload)
                .when()
                .post("/users/login")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response logout(String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .when()
                .post("/users/logout")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }

    public Response deleteCurrentUser(String token) {
        return executeApiCall(() -> authorizedRequest(token)
                .when()
                .delete("/users/me")
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .extract()
                .response());
    }
}
