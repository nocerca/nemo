package no.cerca.api.exception.common;

import org.springframework.http.HttpStatusCode;

/**
 * Created by jadae on 19.03.2025
 */
public class ForbiddenException extends YClientsApiException {
    public ForbiddenException(String message) {
        super(HttpStatusCode.valueOf(403), message);
    }
}