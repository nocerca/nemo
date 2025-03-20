package api.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.cerca.api.client.YClientsAPIClient;
import no.cerca.api.exception.common.YClientsApiException;
import no.cerca.api.exception.custom.DeleteRecordException;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.dtos.basic.ServiceDTO;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.exchange.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class YClientsAPIClientTest {

    @InjectMocks
    private YClientsAPIClient yClientsAPIClient;

    @Mock
    private RestTemplate restTemplate;

    private <T> ResponseDTO<T> getResponseFromFile(String filename, TypeReference<ResponseDTO<T>> typeReference) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        File responseFile = new File(getClass().getClassLoader().getResource(filename).getFile());
        return objectMapper.readValue(responseFile, typeReference);
    }

    @Test
    @DisplayName("Успешное получение ответа на авторизацию")
    public void successAuth() throws IOException {
        RequestAuthDTO requestBody = new RequestAuthDTO("username", "password");
        String partnerToken = "testPartnerToken";

        ResponseDTO<AuthDTO> apiResponse = getResponseFromFile(
                "api/client/response/response_auth_success.json",
                new TypeReference<ResponseDTO<AuthDTO>>() {}
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/vnd.yclients.v2+json");
        headers.add("Authorization", "Bearer " + partnerToken);

        HttpEntity<RequestAuthDTO> requestEntity = new HttpEntity<>(requestBody, headers);

        when(restTemplate.exchange(
                eq("https://api.yclients.com/api/v1/auth"),
                eq(HttpMethod.POST),
                eq(requestEntity),
                eq(new ParameterizedTypeReference<ResponseDTO<AuthDTO>>() {})
        )).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        ResponseDTO<AuthDTO> response = yClientsAPIClient.auth(requestBody, partnerToken);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("Тестов Тест Тестович", response.getData().getName());
        assertEquals("28f27276656851c90c3b32fcb8867f74", response.getData().getUserToken());
        assertEquals("sampleg@test.ru", response.getData().getEmail());
        assertEquals("79348435777", response.getData().getPhone());
        assertEquals("https://be.cdn.yclients.com/images/no-master.png", response.getData().getAvatar());
        assertEquals("79348435777", response.getData().getLogin());
        assertEquals(12887761L, response.getData().getId());
    }

    @Test
    @DisplayName("Ошибка при получении ответа на авторизацию")
    public void errorAuth() throws IOException {
        RequestAuthDTO requestBody = new RequestAuthDTO("username", "password");
        String partnerToken = "testPartnerToken";

        ResponseDTO<AuthDTO> apiResponse = getResponseFromFile(
                "api/client/response/response_auth_error.json",
                new TypeReference<ResponseDTO<AuthDTO>>() {}
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/vnd.yclients.v2+json");
        headers.add("Authorization", "Bearer " + partnerToken);

        HttpEntity<RequestAuthDTO> requestEntity = new HttpEntity<>(requestBody, headers);

        when(restTemplate.exchange(
                eq("https://api.yclients.com/api/v1/auth"),
                eq(HttpMethod.POST),
                eq(requestEntity),
                eq(new ParameterizedTypeReference<ResponseDTO<AuthDTO>>() {})
        )).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));


        YClientsApiException exception = assertThrows(YClientsApiException.class, () -> {
            yClientsAPIClient.auth(requestBody, partnerToken);
        });

        assertEquals("Неверный логин или пароль", exception.getErrorMessage());
    }

    @Test
    @DisplayName("Успешное получение данных нескольких записей по переданным параметрам")
    public void successGetRecords() throws IOException {
        Long companyId = 123L;
        Long staffId = 456L;
        Long clientId = 789L;
        String startDate = "2025-01-01";
        String endDate = "2025-01-31";
        String cStartDate = "2025-01-01";
        String cEndDate = "2025-01-15";
        String userToken = "testToken";
        int page = 1;
        int count = 50;

        ResponseDTO<List<RecordDTO>> apiResponse = getResponseFromFile(
                "api/client/response/response_get_records_success.json",
                new TypeReference<ResponseDTO<List<RecordDTO>>>() {}
        );

        ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>> responseType =
                new ParameterizedTypeReference<ResponseDTO<List<RecordDTO>>>() {};

        when(restTemplate.exchange(
                eq("https://api.yclients.com/api/v1/records/123?staff_id=456&client_id=789&start_date=2025-01-01&end_date=2025-01-31&c_start_date=2025-01-01&c_end_date=2025-01-15&page=1&count=50"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(responseType)
        )).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        RequestRecordsDTO requestRecordsDTO = new RequestRecordsDTO(staffId, clientId, startDate, endDate, cStartDate, cEndDate, page, count);

        ResponseDTO<List<RecordDTO>> response = yClientsAPIClient.getRecords(companyId, requestRecordsDTO, userToken);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());

        RecordDTO actualRecordDTO0 = response.getData().get(0);

        assertEquals(1022656468L, actualRecordDTO0.getId());
        assertEquals(255929879L ,actualRecordDTO0.getClient().getId());
        assertEquals("ninad13579@gmail.com" ,actualRecordDTO0.getClient().getEmail());
        assertEquals("Наукаркар" ,actualRecordDTO0.getClient().getSurname());
        assertEquals("79996283478" ,actualRecordDTO0.getClient().getPhone());
        assertEquals("Нинад" ,actualRecordDTO0.getClient().getName());

        assertEquals(3 ,actualRecordDTO0.getServices().size());
        assertEquals("Оплата за обучение ин. студенты", actualRecordDTO0.getServices().get(0).getTitle());
        assertEquals(17271095L, actualRecordDTO0.getServices().get(0).getId());

        assertEquals(3511073L, actualRecordDTO0.getStaffId());
        assertEquals(1129121L, actualRecordDTO0.getCompanyId());
        assertEquals("", actualRecordDTO0.getComment());
        assertEquals("2025-05-01 14:00:00", actualRecordDTO0.getDate());
        assertNotNull(actualRecordDTO0.getDatetime());
        assertNotNull(actualRecordDTO0.getCreateDate());
    }

    @Test
    @DisplayName("Успешное получение данных записи")
    public void successGetRecord() throws IOException {
        Long companyId = 123L;
        Long recordId = 456L;
        String userToken = "testToken";

        ResponseDTO<RecordDTO> apiResponse = getResponseFromFile("api/client/response/response_get_record_success.json", new TypeReference<ResponseDTO<RecordDTO>>() {});

        when(restTemplate.exchange(
                eq("https://api.yclients.com/api/v1/record/123/456"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        ResponseDTO<RecordDTO> response = yClientsAPIClient.getRecord(companyId, recordId, userToken);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        RecordDTO actualRecord = response.getData();

        assertEquals(1022656468L, actualRecord.getId());
        assertEquals(255929879L ,actualRecord.getClient().getId());
        assertEquals("ninad13579@gmail.com" ,actualRecord.getClient().getEmail());
        assertEquals("Наукаркар" ,actualRecord.getClient().getSurname());
        assertEquals("+79996283478" ,actualRecord.getClient().getPhone());
        assertEquals("Нинад" ,actualRecord.getClient().getName());

        assertEquals(3 ,actualRecord.getServices().size());
        assertEquals("Оплата за обучение ин. студенты", actualRecord.getServices().get(0).getTitle());
        assertEquals(17271095L, actualRecord.getServices().get(0).getId());

        assertEquals(3511073L, actualRecord.getStaffId());
        assertEquals(1129121L, actualRecord.getCompanyId());
        assertEquals("", actualRecord.getComment());
        assertEquals("2025-05-01 14:00:00", actualRecord.getDate());
        assertNotNull(actualRecord.getDatetime());
        assertNotNull(actualRecord.getCreateDate());
    }

    @Test
    @DisplayName("Успешное получение данных при создании записи")
    public void successCreateRecord() throws IOException {
        Long companyId = 123L;
        Long staffId = 8886L;
        List<ServiceDTO> services = List.of(
                new ServiceDTO(1L, "title1"),
                new ServiceDTO(2L, "title2")
        );
        ClientDTO client = new ClientDTO(1L, "name", "surname", "patronymic", "phone", "email", true);
        boolean saveIfBusy = false;
        String datetime = "2019-01-01 17:00:00";
        Integer seanceLength = 3600;
        boolean sendSms = true;
        String comment = "тестовая запись!";
        Integer smsRemainHours = 6;
        Integer emailRemainHours = 24;
        Integer attendance = 1;
        String apiId = "777";
        Map<String, Object> customFields = Map.of(
                "my_custom_field", 123,
                "some_another_field", List.of("first value", "second value")
        );
        String userToken = "testToken";

        ResponseDTO<List<RecordDTO>> apiResponse = getResponseFromFile("api/client/response/response_create_record_success.json", new TypeReference<ResponseDTO<List<RecordDTO>>>() {});

        when(restTemplate.exchange(
                eq("https://api.yclients.com/api/v1/records/123"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        RequestCreateDTO request = new RequestCreateDTO(
                staffId, services, client, saveIfBusy, datetime, seanceLength,
                sendSms, comment, smsRemainHours, emailRemainHours, attendance, apiId, customFields
        );

        ResponseDTO<List<RecordDTO>> response = yClientsAPIClient.createRecord(companyId, request, userToken);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(4564L, response.getData().get(0).getCompanyId());
        assertEquals(9L, response.getData().get(0).getStaffId());
        assertEquals(1, response.getData().get(0).getServices().size());
        assertEquals("не записывать", response.getData().get(0).getComment());
        assertFalse(response.getData().get(0).isDeleted());
    }

    @Test
    @DisplayName("Успешное получение данных при обновлении записи")
    void successUpdateRecord() throws IOException {
        Long companyId = 4564L;
        Long recordId = 999L;
        String userToken = "test_token";

        RequestUpdateDTO requestUpdateDTO = new RequestUpdateDTO();

        ResponseDTO<RecordDTO> expectedResponse = getResponseFromFile(
                "api/client/response/response_update_record_success.json",
                new TypeReference<ResponseDTO<RecordDTO>>() {}
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseDTO<RecordDTO> actualResponse = yClientsAPIClient.updateRecord(companyId, recordId, requestUpdateDTO, userToken);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isSuccess());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getServices().size(), actualResponse.getData().getServices().size());
        assertEquals(expectedResponse.getData().getClient().getPhone(), actualResponse.getData().getClient().getPhone());
    }

    @Test
    @DisplayName("Успешное удаление записи")
    public void testDeleteRecordSuccess() {
        Long companyId = 123L;
        Long recordId = 456L;
        String partnerToken = "testPartnerToken";
        String url = "https://api.yclients.com/api/v1/record/" + companyId + "/" + recordId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + partnerToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Void>() {})
        )).thenReturn(response);

        yClientsAPIClient.deleteRecord(companyId, recordId, partnerToken);

        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Void>() {})
        );

    }

    @Test
    @DisplayName("Неуспешное удаление записи - ошибка статуса")
    public void testDeleteRecordFailure() {
        Long companyId = 123L;
        Long recordId = 456L;
        String partnerToken = "testPartnerToken";
        String url = "https://api.yclients.com/api/v1/record/" + companyId + "/" + recordId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + partnerToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Void>() {})
        )).thenReturn(response);

        DeleteRecordException exception = assertThrows(DeleteRecordException.class, () -> {
            yClientsAPIClient.deleteRecord(companyId, recordId, partnerToken);
        });

        assertEquals("Failed to delete record. Status: 500 INTERNAL_SERVER_ERROR", exception.getErrorMessage());
    }
}
