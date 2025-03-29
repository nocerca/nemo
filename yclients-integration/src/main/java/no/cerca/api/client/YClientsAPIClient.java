package no.cerca.api.client;

import no.cerca.api.exception.common.ForbiddenException;
import no.cerca.api.exception.common.UnauthorizedException;
import no.cerca.api.exception.common.YClientsApiException;
import no.cerca.api.exception.custom.DeleteRecordException;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.dtos.exchange.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by jadae on 13.03.2025
 */
@Component
public class YClientsAPIClient {

    private static final String BASE_URL = "https://api.yclients.com/api/v1";
    private static final String API_VERSION = "application/vnd.yclients.v2+json";
    private final RestTemplate restTemplate;

    public YClientsAPIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders(String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.valueOf(API_VERSION)));
        headers.setBearerAuth(userToken);
        return headers;
    }

    public ResponseDTO<AuthDTO> auth(RequestAuthDTO requestBody) {
        return executeRequest(
                BASE_URL + "/auth",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, createHeaders(requestBody.getPartnerToken())),
                new ParameterizedTypeReference<ResponseDTO<AuthDTO>>() {}
        ).getBody();
    }

    public ResponseDTO<List<RecordDTO>> getRecords(Long companyId, RequestRecordsDTO requestDTO, String userToken) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(BASE_URL + "/records/" + companyId)
                .queryParamIfPresent("staff_id", Optional.ofNullable(requestDTO.getStaffId()))
                .queryParamIfPresent("client_id", Optional.ofNullable(requestDTO.getClientId()))
                .queryParamIfPresent("start_date", Optional.ofNullable(requestDTO.getStartDate()))
                .queryParamIfPresent("end_date", Optional.ofNullable(requestDTO.getEndDate()))
                .queryParamIfPresent("c_start_date", Optional.ofNullable(requestDTO.getcStartDate()))
                .queryParamIfPresent("c_end_date", Optional.ofNullable(requestDTO.getcEndDate()))
                .queryParamIfPresent("page", Optional.ofNullable(requestDTO.getPage()))
                .queryParamIfPresent("count", Optional.ofNullable(requestDTO.getCount()));

        return executeRequest(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(userToken)),
                new ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>>() {}
        ).getBody();
    }

    public ResponseDTO<RecordDTO> getRecord(Long companyId, Long recordId, String userToken) {
        return executeRequest(
                BASE_URL + "/record/" + companyId + "/" + recordId,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(userToken)),
                new ParameterizedTypeReference<ResponseDTO<RecordDTO>>() {}
        ).getBody();
    }

    public ResponseDTO<List<RecordDTO>> createRecord(Long companyId, RequestCreateDTO request, String userToken) {
        return executeRequest(
                BASE_URL + "/records/" + companyId,
                HttpMethod.POST,
                new HttpEntity<>(request, createHeaders(userToken)),
                new ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>>() {}
        ).getBody();
    }

    public ResponseDTO<RecordDTO> updateRecord(Long companyId, Long recordId, RequestUpdateDTO recordUpdateRequest, String userToken) {
        return executeRequest(
                BASE_URL + "/record/" + companyId + "/" + recordId,
                HttpMethod.PUT,
                new HttpEntity<>(recordUpdateRequest, createHeaders(userToken)),
                new ParameterizedTypeReference<ResponseDTO<RecordDTO>>() {}
        ).getBody();
    }

    public void deleteRecord(Long companyId, Long recordId, String partnerToken) {
        ResponseEntity<Void> response = executeRequest(
                BASE_URL + "/record/" + companyId + "/" + recordId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders(partnerToken)),
                new ParameterizedTypeReference<Void>() {}
        );

        if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            //logger
            throw new DeleteRecordException(response.getStatusCode(), "Не удалось удалить запись: " + response.getStatusCode());
        }
    }

    public ResponseDTO<Void> sendCustomSms(Long companyId, RequestMessageDTO request, String userToken) {
        String url = BASE_URL + "/sms/clients/by_id/" + companyId;

        HttpHeaders headers = createHeaders(userToken);
        HttpEntity<RequestMessageDTO> httpEntity = new HttpEntity<>(request, headers);

        ParameterizedTypeReference<ResponseDTO<Void>> responseRef = new ParameterizedTypeReference<>() {};
        ResponseEntity<ResponseDTO<Void>> response = executeRequest(url, HttpMethod.POST, httpEntity, responseRef);

        return response.getBody();
    }

    public ResponseDTO<Void> sendCustomEmail(Long companyId, RequestMessageDTO request, String userToken) {
        String url = BASE_URL + "/email/clients/by_id/" + companyId;

        HttpHeaders headers = createHeaders(userToken);
        HttpEntity<RequestMessageDTO> httpEntity = new HttpEntity<>(request, headers);

        ParameterizedTypeReference<ResponseDTO<Void>> responseRef = new ParameterizedTypeReference<>() {};
        ResponseEntity<ResponseDTO<Void>> response = executeRequest(url, HttpMethod.POST, httpEntity, responseRef);

        return response.getBody();
    }


    private <T> ResponseEntity<T> executeRequest(String url, HttpMethod method, HttpEntity<?> request, ParameterizedTypeReference<T> responseType) {
        ResponseEntity<T> response = restTemplate.exchange(url, method, request, responseType);
        checkForErrors(response);
        return response;
    }

    private void checkForErrors(ResponseEntity<?> response) {
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //logger
            throw new UnauthorizedException("Не указан идентификатор пользователя или партнера");
        }
        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            //logger
            throw new ForbiddenException("Недостаточно прав");
        }
        if (response.getBody() instanceof ResponseDTO<?> dto && !dto.isSuccess()) {
            //logger
            throw new YClientsApiException(response.getStatusCode(), ((LinkedHashMap<String, String>) dto.getMeta().get(0)).get("message"));
        }
    }
}
