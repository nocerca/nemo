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

    public ResponseDTO<AuthDTO> auth(RequestAuthDTO requestBody, String partnerToken) {
        String url = BASE_URL + "/auth";
        HttpHeaders headers = createHeaders(partnerToken);

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<ResponseDTO<AuthDTO>> responseRef = new ParameterizedTypeReference<ResponseDTO<AuthDTO>>() {};

        ResponseEntity<ResponseDTO<AuthDTO>> response = executeRequest(url, HttpMethod.POST, request, responseRef);

        return response.getBody();
    }

    public ResponseDTO<List<RecordDTO>> getRecords(Long companyId, RequestRecordsDTO requestDTO, String userToken) {
        String url = BASE_URL + "/records/" + companyId;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(URI.create(url));

        if (requestDTO.getStaffId() != null) uriBuilder.queryParam("staff_id", requestDTO.getStaffId());
        if (requestDTO.getClientId() != null) uriBuilder.queryParam("client_id", requestDTO.getClientId());
        if (requestDTO.getStartDate() != null) uriBuilder.queryParam("start_date", requestDTO.getStartDate());
        if (requestDTO.getEndDate() != null) uriBuilder.queryParam("end_date", requestDTO.getEndDate());
        if (requestDTO.getcStartDate() != null) uriBuilder.queryParam("c_start_date", requestDTO.getcStartDate());
        if (requestDTO.getcEndDate() != null) uriBuilder.queryParam("c_end_date", requestDTO.getcEndDate());
        if (requestDTO.getPage() != null) uriBuilder.queryParam("page", requestDTO.getPage());
        if (requestDTO.getCount() != null) uriBuilder.queryParam("count", requestDTO.getCount());

        HttpHeaders headers = createHeaders(userToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>> responseRef = new ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>>() {};

        ResponseEntity<ResponseDTO<List<RecordDTO>>> response = executeRequest(uriBuilder.toUriString(), HttpMethod.GET, request, responseRef);

        return response.getBody();
    }

    public ResponseDTO<RecordDTO> getRecord(Long companyId, Long recordId, String userToken) {
        String url = BASE_URL + "/record/" + companyId + "/" + recordId;
        HttpHeaders headers = createHeaders(userToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ParameterizedTypeReference<ResponseDTO<RecordDTO>> responseRef = new ParameterizedTypeReference<ResponseDTO<RecordDTO>>() {};

        ResponseEntity<ResponseDTO<RecordDTO>> response = executeRequest(url, HttpMethod.GET, request, responseRef);

        return response.getBody();
    }

    public ResponseDTO<List<RecordDTO>> createRecord(Long companyId, RequestCreateDTO request, String userToken) {
        String url = BASE_URL + "/records/" + companyId;

        HttpHeaders headers = createHeaders(userToken);
        HttpEntity<RequestCreateDTO> httpEntity = new HttpEntity<>(request, headers);
        ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>> responseRef = new ParameterizedTypeReference<>() {};

        ResponseEntity<ResponseDTO<List<RecordDTO>>> response = executeRequest(url, HttpMethod.POST, httpEntity, responseRef);

        return response.getBody();
    }

    public ResponseDTO<RecordDTO> updateRecord(Long companyId, Long recordId, RequestUpdateDTO recordUpdateRequest, String userToken) {
        String url = BASE_URL + "/record/" + companyId + "/" + recordId;
        HttpHeaders headers = createHeaders(userToken);

        HttpEntity<RequestUpdateDTO> request = new HttpEntity<>(recordUpdateRequest, headers);
        ParameterizedTypeReference<ResponseDTO<RecordDTO>> responseRef = new ParameterizedTypeReference<ResponseDTO<RecordDTO>>() {};

        ResponseEntity<ResponseDTO<RecordDTO>> response = executeRequest(url, HttpMethod.PUT, request, responseRef);

        return response.getBody();
    }

    public void deleteRecord(Long companyId, Long recordId, String partnerToken) {
        String url = BASE_URL + "/record/" + companyId + "/" + recordId;
        HttpHeaders headers = createHeaders(partnerToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ParameterizedTypeReference<Void> responseRef = new ParameterizedTypeReference<Void>() {};

        ResponseEntity<Void> response = executeRequest(url, HttpMethod.DELETE, request, responseRef);

        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            //logger
            throw new DeleteRecordException(response.getStatusCode(), "Failed to delete record. Status: " + response.getStatusCode());
        }
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
