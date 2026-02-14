package api.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.serenitybdd.rest.SerenityRest;
import utils.ApiEvidenceFilter;

public abstract class BaseApiService {

    private static final String API_BASE_URL_PROPERTY = "api.base.url";
    private static final String DEFAULT_API_BASE_URL = "https://thinking-tester-contact-list.herokuapp.com";
    private static final Pattern API_BASE_URL_PATTERN = Pattern.compile("api\\.base\\.url\\s*[=:]\\s*\"?([^\"\\s]+)\"?");
    private static final ApiEvidenceFilter API_EVIDENCE_FILTER = new ApiEvidenceFilter();

    protected RequestSpecification baseRequest() {
        return SerenityRest.given()
                .baseUri(resolveBaseUri())
                .filter(API_EVIDENCE_FILTER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    protected RequestSpecification authorizedRequest(String token) {
        return baseRequest().header("Authorization", "Bearer " + token);
    }

    protected Response executeApiCall(Supplier<Response> requestExecution) {
        try {
            return requestExecution.get();
        } catch (RuntimeException exception) {
            ApiEvidenceFilter.attachLastExchangeToSerenity("API request/response (failure)");
            String evidence = ApiEvidenceFilter.lastExchangeReport();
            if (evidence != null && !evidence.isBlank()) {
                throw new RuntimeException("API request/response (failure)" + System.lineSeparator() + evidence, exception);
            }
            throw exception;
        }
    }

    protected String resolveBaseUri() {
        String fromSystemProperties = System.getProperty(API_BASE_URL_PROPERTY);
        if (isNotBlank(fromSystemProperties)) {
            return fromSystemProperties;
        }

        Optional<String> fromSerenityConfig = readApiBaseUrlFromClasspathFile("serenity.conf");
        if (fromSerenityConfig.isPresent()) {
            return fromSerenityConfig.get();
        }

        Optional<String> fromSerenityProperties = readApiBaseUrlFromClasspathFile("serenity.properties");
        return fromSerenityProperties.orElse(DEFAULT_API_BASE_URL);
    }

    private Optional<String> readApiBaseUrlFromClasspathFile(String resourceName) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .map(API_BASE_URL_PATTERN::matcher)
                        .filter(Matcher::find)
                        .map(matcher -> matcher.group(1))
                        .filter(this::isNotBlank)
                        .findFirst();
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
