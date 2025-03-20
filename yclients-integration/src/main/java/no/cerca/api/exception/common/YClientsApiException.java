package no.cerca.api.exception.common;

import org.springframework.http.HttpStatusCode;

/**
 * Created by jadae on 19.03.2025
 */
public class YClientsApiException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;
    private final String errorMessage;

    public YClientsApiException(HttpStatusCode httpStatusCode, String errorMessage) {
        super(errorMessage);
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
    }

    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}