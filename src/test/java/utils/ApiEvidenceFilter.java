package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.serenitybdd.core.Serenity;

/**
 * Filtro de Rest Assured para capturar evidencia técnica de llamadas API.
 * Registra request/response en memoria por escenario y la expone para Serenity.
 */
public class ApiEvidenceFilter implements Filter {

    private static final String EVIDENCE_MODE_PROPERTY = "api.evidence.mode";
    private static final String EVIDENCE_MODE_ALWAYS = "always";

    private static final ThreadLocal<List<ApiExchange>> EXCHANGES = ThreadLocal.withInitial(ArrayList::new);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Intercepta la llamada API y guarda request/response o error asociado.
     *
     * @param requestSpec request en construcción.
     * @param responseSpec spec de respuesta (no usado directamente).
     * @param filterContext contexto encadenado de filtros Rest Assured.
     * @return respuesta original de la llamada.
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        String requestMethod = requestSpec.getMethod();
        String requestUri = requestSpec.getURI();
        String requestHeaders = formatHeaders(requestSpec.getHeaders().asList(), true);
        String requestBody = formatBody(requestSpec.getBody());
        long startedAt = System.currentTimeMillis();
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        try {
            Response response = filterContext.next(requestSpec, responseSpec);
            ApiExchange exchange = ApiExchange.success(
                    timestamp,
                    requestMethod,
                    requestUri,
                    requestHeaders,
                    requestBody,
                    response.statusCode(),
                    formatHeaders(response.getHeaders().asList(), false),
                    formatBody(response.getBody() == null ? null : response.getBody().asString()),
                    System.currentTimeMillis() - startedAt
            );
            register(exchange);
            return response;
        } catch (RuntimeException exception) {
            ApiExchange exchange = ApiExchange.failure(
                    timestamp,
                    requestMethod,
                    requestUri,
                    requestHeaders,
                    requestBody,
                    System.currentTimeMillis() - startedAt,
                    exception.toString()
            );
            register(exchange);
            throw exception;
        }
    }

    /**
     * Limpia evidencia acumulada para el hilo actual.
     */
    public static void clear() {
        EXCHANGES.remove();
    }

    /**
     * Evalúa si debe adjuntarse evidencia de todas las llamadas.
     *
     * @return `true` cuando `-Dapi.evidence.mode=always`.
     */
    public static boolean shouldAttachAlwaysEvidence() {
        String evidenceMode = System.getProperty(EVIDENCE_MODE_PROPERTY, "on-failure");
        return EVIDENCE_MODE_ALWAYS.equalsIgnoreCase(evidenceMode.trim());
    }

    /**
     * Entrega reporte textual del último intercambio registrado.
     *
     * @return texto de evidencia o `null` si no hay llamadas.
     */
    public static String lastExchangeReport() {
        List<ApiExchange> exchanges = EXCHANGES.get();
        if (exchanges.isEmpty()) {
            return null;
        }
        return exchanges.get(exchanges.size() - 1).asSerenityReportText();
    }

    /**
     * Entrega reportes de todos los intercambios del escenario actual.
     *
     * @return lista de textos en orden de ejecución.
     */
    public static List<String> allExchangeReports() {
        List<ApiExchange> exchanges = EXCHANGES.get();
        if (exchanges.isEmpty()) {
            return List.of();
        }
        return exchanges.stream().map(ApiExchange::asSerenityReportText).toList();
    }

    /**
     * Adjunta al reporte Serenity el último intercambio capturado.
     *
     * @param title título visible en reporte.
     */
    public static void attachLastExchangeToSerenity(String title) {
        String report = lastExchangeReport();
        if (report == null) {
            return;
        }
        Serenity.recordReportData()
                .withTitle(title)
                .andContents(report);
    }

    /**
     * Adjunta al reporte Serenity todos los intercambios capturados.
     *
     * @param titlePrefix prefijo para numeración de evidencia.
     */
    public static void attachAllExchangesToSerenity(String titlePrefix) {
        List<String> reports = allExchangeReports();
        for (int index = 0; index < reports.size(); index++) {
            Serenity.recordReportData()
                    .withTitle(titlePrefix + " #" + (index + 1))
                    .andContents(reports.get(index));
        }
    }

    /**
     * Registra un intercambio en el almacenamiento del hilo actual.
     */
    private static void register(ApiExchange exchange) {
        EXCHANGES.get().add(exchange);
    }

    /**
     * Convierte headers en texto legible para reporte.
     */
    private static String formatHeaders(List<Header> headers, boolean maskAuthorization) {
        if (headers == null || headers.isEmpty()) {
            return "<none>";
        }

        return headers.stream()
                .map(header -> {
                    String value = header.getValue();
                    if (maskAuthorization && "authorization".equalsIgnoreCase(header.getName())) {
                        value = maskToken(value);
                    }
                    return header.getName() + ": " + value;
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Oculta tokens sensibles en evidencia.
     */
    private static String maskToken(String value) {
        if (value == null || value.isBlank()) {
            return "<empty>";
        }
        String normalized = value.trim();
        if (normalized.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            return "Bearer ***";
        }
        return "***";
    }

    /**
     * Formatea body de request/response para reporte.
     */
    private static String formatBody(Object body) {
        if (body == null) {
            return "<empty>";
        }

        if (body instanceof String textBody) {
            if (textBody.isBlank()) {
                return "<empty>";
            }
            return prettyJson(textBody);
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        } catch (Exception ignored) {
            return String.valueOf(body);
        }
    }

    /**
     * Intenta serializar texto como JSON con sangría.
     */
    private static String prettyJson(String value) {
        try {
            Object json = OBJECT_MAPPER.readValue(value, Object.class);
            return OBJECT_MAPPER.writeValueAsString(json);
        } catch (Exception ignored) {
            return value;
        }
    }

    /**
     * Estructura interna de evidencia de una llamada HTTP.
     */
    private static class ApiExchange {

        private final String timestamp;
        private final String method;
        private final String uri;
        private final String requestHeaders;
        private final String requestBody;
        private final Integer statusCode;
        private final String responseHeaders;
        private final String responseBody;
        private final Long elapsedMs;
        private final String error;

        /**
         * Construye un intercambio de request/response.
         */
        private ApiExchange(String timestamp,
                            String method,
                            String uri,
                            String requestHeaders,
                            String requestBody,
                            Integer statusCode,
                            String responseHeaders,
                            String responseBody,
                            Long elapsedMs,
                            String error) {
            this.timestamp = timestamp;
            this.method = method;
            this.uri = uri;
            this.requestHeaders = requestHeaders;
            this.requestBody = requestBody;
            this.statusCode = statusCode;
            this.responseHeaders = responseHeaders;
            this.responseBody = responseBody;
            this.elapsedMs = elapsedMs;
            this.error = error;
        }

        /**
         * Fabrica intercambio exitoso.
         */
        private static ApiExchange success(String timestamp,
                                           String method,
                                           String uri,
                                           String requestHeaders,
                                           String requestBody,
                                           Integer statusCode,
                                           String responseHeaders,
                                           String responseBody,
                                           Long elapsedMs) {
            return new ApiExchange(timestamp, method, uri, requestHeaders, requestBody,
                    statusCode, responseHeaders, responseBody, elapsedMs, null);
        }

        /**
         * Fabrica intercambio fallido sin respuesta HTTP.
         */
        private static ApiExchange failure(String timestamp,
                                           String method,
                                           String uri,
                                           String requestHeaders,
                                           String requestBody,
                                           Long elapsedMs,
                                           String error) {
            return new ApiExchange(timestamp, method, uri, requestHeaders, requestBody,
                    null, "<none>", "<none>", elapsedMs, error);
        }

        /**
         * Renderiza el intercambio en formato texto para Serenity.
         */
        private String asSerenityReportText() {
            String line = System.lineSeparator();
            StringBuilder report = new StringBuilder();
            report.append("timestamp: ").append(timestamp).append(line);
            report.append("request.method: ").append(method).append(line);
            report.append("request.uri: ").append(uri).append(line);
            report.append("request.headers:").append(line).append(requestHeaders).append(line);
            report.append("request.body:").append(line).append(requestBody).append(line);
            report.append("response.statusCode: ").append(statusCode == null ? "<no response>" : statusCode).append(line);
            report.append("response.headers:").append(line).append(responseHeaders).append(line);
            report.append("response.body:").append(line).append(responseBody).append(line);
            report.append("duration.ms: ").append(elapsedMs).append(line);

            if (error != null) {
                report.append("error: ").append(error).append(line);
            }
            return report.toString();
        }
    }
}
