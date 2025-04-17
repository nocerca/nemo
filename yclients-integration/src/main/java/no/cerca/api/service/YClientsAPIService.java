package no.cerca.api.service;

import jakarta.transaction.Transactional;
import no.cerca.api.client.YClientsAPIClient;
import no.cerca.api.exception.custom.DeleteRecordException;
import no.cerca.api.response.CommonAPIResponse;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.dtos.exchange.*;
import no.cerca.entities.Auth;
import no.cerca.entities.Record;
import no.cerca.services.AuthService;
import no.cerca.services.ClientService;
import no.cerca.services.RecordService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Created by jadae on 18.03.2025
 */
@Component
public class YClientsAPIService {

    private final YClientsAPIClient apiClient;
    private final ClientService clientService;
    private final AuthService authService;
    private final RecordService recordService;

    public YClientsAPIService(YClientsAPIClient apiClient, ClientService clientService, AuthService authService, RecordService recordService) {
        this.apiClient = apiClient;
        this.clientService = clientService;
        this.authService = authService;
        this.recordService = recordService;
    }

    @Transactional
    public CommonAPIResponse<Auth> authenticateUser(RequestAuthDTO requestAuth) {
        return authService.getByLogin(requestAuth.getLogin())
                .map(auth -> new CommonAPIResponse<>("success", auth, "Пользователь уже авторизован"))
                .orElseGet(() -> {
                    try {
                        ResponseDTO<AuthDTO> authResponse = apiClient.auth(requestAuth);
                        if (!authResponse.isSuccess()) {
                            return new CommonAPIResponse<>("error", null, String.valueOf(authResponse.getMeta().get(0)));
                        }
                        Auth auth = authService.save(new Auth(authResponse.getData()));
                        return new CommonAPIResponse<>("success", auth, "Успешное выполнение авторизации пользователя через API YClients");
                    } catch (Exception e) {
                        return new CommonAPIResponse<>("error", null, "Ошибка авторизации: " + e.getMessage());
                    }
                });
    }

    @Transactional
    public CommonAPIResponse<List<Record>> getAllRecordsForClient(Long authId, RequestRecordsDTO requestRecordsDTO) {
        return fetchRecords(authId, requestRecordsDTO, recordService::getAllRecordsForClient);
    }

    @Transactional
    public CommonAPIResponse<List<Record>> getAllRecordsForClientForDay(Long authId, RequestRecordsDTO requestRecordsDTO) {
        return fetchRecords(authId, requestRecordsDTO, recordService::getRecordsForToday);
    }

    @Transactional
    public CommonAPIResponse<List<Record>> getAllRecordsForClientForHour(Long authId, RequestRecordsDTO requestRecordsDTO) {
        return fetchRecords(authId, requestRecordsDTO, recordService::getRecordsForNextHour);
    }

    @Transactional
    public CommonAPIResponse<Record> getCurrentRecord(Long authId, RequestRecordsDTO requestRecordsDTO) {
        return fetchSingleRecord(authId, requestRecordsDTO, recordService::getCurrentRecord);
    }

    @Transactional
    public CommonAPIResponse<Record> getNextRecord(Long authId, RequestRecordsDTO requestRecordsDTO) {
        return fetchSingleRecord(authId, requestRecordsDTO, recordService::getNextRecord);
    }

    @Transactional
    public CommonAPIResponse<Record> sendSmsWithRecordInfo(Long authId, Long recordId, Boolean sendSms) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        RequestUpdateDTO requestUpdateDTO = new RequestUpdateDTO();
        requestUpdateDTO.setSendSms(sendSms);

        ResponseDTO<RecordDTO> responseDTO = apiClient.updateRecord(auth.getCompanyId(), recordId, requestUpdateDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось установить флаг отправки SMS с информацией о записи");
        }

        Record updatedRecord = recordService.createOrUpdateRecordFromDTO(responseDTO.getData());
        return new CommonAPIResponse<>("success", updatedRecord, "SMS с информацией о записи будет отправлено");
    }

    @Transactional
    public CommonAPIResponse<Void> deleteRecord(Long authId, Long recordId) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        try {
            apiClient.deleteRecord(auth.getCompanyId(), recordId, auth.getUserToken());
            return new CommonAPIResponse<>("success", null, "Запись успешно удалена");
        } catch (DeleteRecordException e) {
            return new CommonAPIResponse<>("error", null, "Не удалось удалить запись: " + e.getMessage());
        }
    }

    @Transactional
    public CommonAPIResponse<Void> createRecord(Long authId, RequestCreateDTO requestCreateDTO) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        ResponseDTO<List<RecordDTO>> responseDTO = apiClient.createRecord(auth.getCompanyId(), requestCreateDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось создать запись");
        }

        return new CommonAPIResponse<>("success", null, "Запись успешно создана");
    }

    @Transactional
    public CommonAPIResponse<Void> createPauseRecord(Long authId, LocalDateTime dateTimeToCreatePauseFor) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        RequestCreateDTO requestCreateDTO = new RequestCreateDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        requestCreateDTO.setDatetime(dateTimeToCreatePauseFor.format(formatter));
        requestCreateDTO.setComment("Запись - заглушка");

        Auth auth = authOptional.get();
        ResponseDTO<List<RecordDTO>> responseDTO = apiClient.createRecord(auth.getCompanyId(), requestCreateDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось создать запись-паузу");
        }

        return new CommonAPIResponse<>("success", null, "Запись-пауза успешно создана");
    }

    @Transactional
    public CommonAPIResponse<Record> updateRecord(Long authId, Long recordId, RequestUpdateDTO recordUpdateRequest) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        ResponseDTO<RecordDTO> responseDTO = apiClient.updateRecord(auth.getCompanyId(), recordId, recordUpdateRequest, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось обновить запись");
        }

        Record updatedRecord = recordService.createOrUpdateRecordFromDTO(responseDTO.getData());
        return new CommonAPIResponse<>("success", updatedRecord, "Запись успешно обновлена");
    }

    @Transactional
    public CommonAPIResponse<Record> setEmailNotificationRequired(Long authId, Long recordId, Integer amountOfHoursBeforeEmailNotification) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        RequestUpdateDTO requestUpdateDTO = new RequestUpdateDTO();
        requestUpdateDTO.setEmailRemainHours(amountOfHoursBeforeEmailNotification);

        ResponseDTO<RecordDTO> responseDTO = apiClient.updateRecord(auth.getCompanyId(), recordId, requestUpdateDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось установить уведомление по email");
        }

        Record updatedRecord = recordService.createOrUpdateRecordFromDTO(responseDTO.getData());
        return new CommonAPIResponse<>("success", updatedRecord, "Уведомление по email успешно установлено");
    }

    @Transactional
    public CommonAPIResponse<Record> setSmsNotificationRequired(Long authId, Long recordId, Integer amountOfHoursBeforeSmsNotification) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        RequestUpdateDTO requestUpdateDTO = new RequestUpdateDTO();
        requestUpdateDTO.setSmsRemainHours(amountOfHoursBeforeSmsNotification);

        ResponseDTO<RecordDTO> responseDTO = apiClient.updateRecord(auth.getCompanyId(), recordId, requestUpdateDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось установить время для отправки SMS-напоминания");
        }

        Record updatedRecord = recordService.createOrUpdateRecordFromDTO(responseDTO.getData());
        return new CommonAPIResponse<>("success", updatedRecord, "Время SMS-напоминания успешно установлено");
    }

    @Transactional
    public CommonAPIResponse<Void> sendCustomSmsToClients(Long authId, RequestMessageDTO requestMessageDTO) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        if (requestMessageDTO.getText() == null) {
            return new CommonAPIResponse<>("error", null, "Отсутствует текст сообщения");
        }

        Auth auth = authOptional.get();
        ResponseDTO<Void> responseDTO = apiClient.sendCustomSms(auth.getCompanyId(), requestMessageDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось отправить SMS рассылку");
        }

        return new CommonAPIResponse<>("success", null, "SMS успешно отправлены");
    }

    @Transactional
    public CommonAPIResponse<Void> sendCustomEmailToClients(Long authId, RequestMessageDTO requestMessageDTO) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        if (requestMessageDTO.getText() == null || requestMessageDTO.getSubject() == null) {
            return new CommonAPIResponse<>("error", null, "Отсутствует текст/тема письма");
        }

        Auth auth = authOptional.get();
        ResponseDTO<Void> responseDTO = apiClient.sendCustomEmail(auth.getCompanyId(), requestMessageDTO, auth.getUserToken());

        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось отправить Email рассылку");
        }

        return new CommonAPIResponse<>("success", null, "Email успешно отправлены");
    }

    @Transactional
    public CommonAPIResponse<Void> notifyClientsAboutRecordUpdate(Long authId, List<Long> clientIds) {
        return sendNotification(authId, clientIds, "Ваша запись обновлена", "Обновление записи");
    }

    @Transactional
    public CommonAPIResponse<Void> notifyClientsAboutRecordDeletion(Long authId, List<Long> clientIds) {
        return sendNotification(authId, clientIds, "Ваша запись отменена", "Удаление записи");
    }

    private CommonAPIResponse<Void> sendNotification(Long authId, List<Long> clientIds, String message, String subject) {
        Optional<Auth> authOptional = getAuth(authId);
        if (authOptional.isEmpty()) {
            return new CommonAPIResponse<>("error", null, "Авторизация не найдена");
        }

        Auth auth = authOptional.get();
        RequestMessageDTO request = new RequestMessageDTO(clientIds, message, subject);

        ResponseDTO<Void> smsResponse = apiClient.sendCustomSms(auth.getCompanyId(), request, auth.getUserToken());
        if (!smsResponse.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось отправить SMS уведомление");
        }

        ResponseDTO<Void> emailResponse = apiClient.sendCustomEmail(auth.getCompanyId(), request, auth.getUserToken());
        if (!emailResponse.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Не удалось отправить Email уведомление");
        }

        return new CommonAPIResponse<>("success", null, "Уведомления успешно отправлены");
    }

    private CommonAPIResponse<List<Record>> fetchRecords(Long authId, RequestRecordsDTO requestRecordsDTO,
                                                         java.util.function.Function<Long, List<Record>> recordFetcher) {
        return getAuth(authId).map(auth -> {
            if (recordService.wasUpdatedLessThan15minAgo(requestRecordsDTO.getClientId())) {
                return new CommonAPIResponse<>("success", recordFetcher.apply(requestRecordsDTO.getClientId()), "Успешное получение записей клиента");
            }
            return fetchAndUpdateRecords(auth, requestRecordsDTO, recordFetcher);
        }).orElseGet(() -> new CommonAPIResponse<>("error", null, "Авторизация не найдена"));
    }

    private CommonAPIResponse<Record> fetchSingleRecord(Long authId, RequestRecordsDTO requestRecordsDTO,
                                                        java.util.function.Function<Long, Record> recordFetcher) {
        return getAuth(authId).map(auth -> {
            if (recordService.wasUpdatedLessThan15minAgo(requestRecordsDTO.getClientId())) {
                return new CommonAPIResponse<>("success", recordFetcher.apply(requestRecordsDTO.getClientId()), "Успешное получение записей клиента");
            }
            return fetchAndUpdateSingleRecord(auth, requestRecordsDTO, recordFetcher);
        }).orElseGet(() -> new CommonAPIResponse<>("error", null, "Авторизация не найдена"));
    }

    private Optional<Auth> getAuth(Long authId) {
        return Optional.ofNullable(authId).flatMap(authService::get);
    }

    private CommonAPIResponse<List<Record>> fetchAndUpdateRecords(Auth auth, RequestRecordsDTO requestRecordsDTO,
                                                                  java.util.function.Function<Long, List<Record>> recordFetcher) {
        ResponseDTO<List<RecordDTO>> responseDTO = apiClient.getRecords(auth.getCompanyId(), requestRecordsDTO, auth.getUserToken());
        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Ошибка при получении записей клиента");
        }
        if (responseDTO.getData().isEmpty()) {
            return new CommonAPIResponse<>("success", null, "Отсутствуют данные записей для клиента с id: " + requestRecordsDTO.getClientId());
        }
        responseDTO.getData().forEach(recordService::createOrUpdateRecordFromDTO);
        return new CommonAPIResponse<>("success", recordFetcher.apply(requestRecordsDTO.getClientId()), "Успешное получение записей клиента");
    }

    private CommonAPIResponse<Record> fetchAndUpdateSingleRecord(Auth auth, RequestRecordsDTO requestRecordsDTO,
                                                                 java.util.function.Function<Long, Record> recordFetcher) {
        ResponseDTO<List<RecordDTO>> responseDTO = apiClient.getRecords(auth.getCompanyId(), requestRecordsDTO, auth.getUserToken());
        if (!responseDTO.isSuccess()) {
            return new CommonAPIResponse<>("error", null, "Ошибка при получении записей клиента");
        }
        if (responseDTO.getData().isEmpty()) {
            return new CommonAPIResponse<>("success", null, "Отсутствуют данные записей для клиента с id: " + requestRecordsDTO.getClientId());
        }
        responseDTO.getData().forEach(recordService::createOrUpdateRecordFromDTO);
        return new CommonAPIResponse<>("success", recordFetcher.apply(requestRecordsDTO.getClientId()), "Успешное получение записей клиента");
    }

}
