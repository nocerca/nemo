package no.cerca.api.exception.custom;

import no.cerca.api.exception.common.YClientsApiException;
import org.springframework.http.HttpStatusCode;

/**
 * Created by jadae on 19.03.2025
 */
public class DeleteRecordException extends YClientsApiException {

    public DeleteRecordException(HttpStatusCode httpStatusCode, String errorMessage) {
        super(httpStatusCode, errorMessage);
    }
}