package no.cerca.api.exception.common;

import org.springframework.http.HttpStatusCode;

/**
 * Created by jadae on 19.03.2025
 */
public class UnauthorizedException extends YClientsApiException {
    public UnauthorizedException(String message) {
        super(HttpStatusCode.valueOf(401), message);
    }
}