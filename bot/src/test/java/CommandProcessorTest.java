import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.exchange.RequestAuthDTO;
import no.cerca.dtos.exchange.RequestCreateDTO;
import no.cerca.dtos.exchange.RequestUpdateDTO;
import no.cerca.entities.Auth;
import no.cerca.entities.Record;
import no.cerca.entities.Service;
import no.cerca.state.impl.AuthState;
import no.cerca.state.impl.CreateRecordState;
import no.cerca.util.CommandProcessor;
import no.cerca.util.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.fetcher.Chat;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

/**
 * Created by jadae on 04.05.2025
 */
class CommandProcessorTest {

    @Mock
    private BotApiClientController botController;

    @Mock
    private YClientsAPIService yClientsAPIService;

    private CommandProcessor commandProcessor;
    private Chat testChat;
    private UserSession testSession;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commandProcessor = new CommandProcessor(botController, yClientsAPIService);
        testChat = new Chat();
        try {
            Field chatIdField = Chat.class.getDeclaredField("chatId");
            chatIdField.setAccessible(true);
            chatIdField.set(testChat, "12345");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        testSession = new UserSession("user123");
        testSession.setAuth(createTestAuth());
    }

    @Test
    @DisplayName("При получении неизвестной команды должен отправить сообщение об ошибке")
    public void errorUnknownCommand_ShouldSendErrorMessage() throws IOException {
        commandProcessor.processCommand("/unknown", testChat, testSession);
        verify(botController).sendTextMessage(argThat(req ->
                req.getChatId().equals("12345") &&
                        req.getText().contains("Неизвестная команда")
        ));
    }

    @Test
    @DisplayName("При команде /start должен установить состояние авторизации и отправить приветственное сообщение")
    public void successStartCommand_ShouldSetAuthStateAndSendWelcomeMessage() throws IOException {
        commandProcessor.processCommand("/start", testChat, testSession);

        assertTrue(testSession.getState() instanceof AuthState);
        verify(botController).sendTextMessage(argThat(req ->
                req.getChatId().equals("12345") &&
                        req.getText().contains("Добро пожаловать")
        ));
    }

    @Test
    @DisplayName("При успешной авторизации должен установить данные аутентификации и показать главное меню")
    public void successAuthCommand_ValidCredentials_ShouldSetAuthDataAndShowMainMenu() throws IOException {
        RequestAuthDTO authDTO = new RequestAuthDTO("login", "pass", "token");
        Auth authData = new Auth();
        authData.setId(1L);

        when(yClientsAPIService.authenticateUser(authDTO))
                .thenReturn(new CommonAPIResponse<>("success", authData, "OK"));

        commandProcessor.processCommand("/auth login pass token", testChat, testSession);

        assertNotNull(testSession.getAuth());
        assertEquals(1L, testSession.getAuth().getId());
        assertNull(testSession.getState());
        verify(botController, atLeastOnce()).sendTextMessage(any());
    }

    @Test
    @DisplayName("При неверных учетных данных должен отправить сообщение об ошибке авторизации")
    public void errorAuthCommand_InvalidCredentials_ShouldSendAuthErrorMessage() throws IOException {
        RequestAuthDTO authDTO = new RequestAuthDTO("login", "pass", "token");

        when(yClientsAPIService.authenticateUser(authDTO))
                .thenReturn(new CommonAPIResponse<>("error", null, "Invalid credentials"));

        testSession.setAuth(null);
        commandProcessor.processCommand("/auth login pass token", testChat, testSession);

        assertNull(testSession.getAuth());
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Ошибка авторизации")
        ));
    }

    @Test
    @DisplayName("При неверном формате команды /auth должен отправить сообщение о неверном формате")
    public void errorAuthCommand_InvalidFormat_ShouldSendFormatErrorMessage() throws IOException {
        commandProcessor.processCommand("/auth login", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Неверный формат")
        ));
    }

    @Test
    @DisplayName("При запросе записей без авторизации должен отправить сообщение о необходимости авторизации")
    public void errorRecordsCommand_Unauthorized_ShouldSendAuthRequiredMessage() throws IOException {
        testSession.setAuth(null);
        commandProcessor.processCommand("/records", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Сначала авторизуйтесь")
        ));
    }

    @Test
    @DisplayName("При успешном запросе всех записей должен отправить список записей")
    public void successRecordsCommand_Authorized_ShouldSendRecordsList() throws IOException {
        testSession.setAuth(createTestAuth());
        Record testRecord = createTestRecord();

        when(yClientsAPIService.getAllRecords(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("success", List.of(testRecord), "OK"));

        commandProcessor.processCommand("/records", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Все записи") &&
                        req.getText().contains("Тестовая услуга")
        ));
    }

    @Test
    @DisplayName("При успешном запросе записей на сегодня должен отправить список записей")
    public void successRecordsTodayCommand_ShouldSendTodaysRecords() throws IOException {
        testSession.setAuth(createTestAuth());
        Record testRecord = createTestRecord();

        when(yClientsAPIService.getAllRecordsForDay(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("success", List.of(testRecord), "OK"));

        commandProcessor.processCommand("/recordstoday", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Записи на сегодня")
        ));
    }

    @Test
    @DisplayName("При успешном запросе записей на ближайший час должен отправить список записей")
    public void successRecordsNextHourCommand_ShouldSendNextHourRecords() throws IOException {
        testSession.setAuth(createTestAuth());
        Record testRecord = createTestRecord();

        when(yClientsAPIService.getAllRecordsForHour(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("success", List.of(testRecord), "OK"));

        commandProcessor.processCommand("/recordsnexthour", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Записи в ближайший час")
        ));
    }

    @Test
    @DisplayName("При отсутствии текущих записей должен отправить соответствующее сообщение")
    public void errorCurrentRecordCommand_NoRecords_ShouldSendNoRecordsMessage() throws IOException {
        testSession.setAuth(createTestAuth());

        when(yClientsAPIService.getCurrentRecord(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("error", null, "No records"));

        commandProcessor.processCommand("/currentrecord", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Нет текущих записей")
        ));
    }

    @Test
    @DisplayName("При успешном запросе следующей записи должен отправить информацию о записи")
    public void successNextRecordCommand_ShouldSendNextRecordInfo() throws IOException {
        testSession.setAuth(createTestAuth());
        Record testRecord = createTestRecord();

        when(yClientsAPIService.getNextRecord(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("success", testRecord, "OK"));

        commandProcessor.processCommand("/nextrecord", testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Следующая запись")
        ));
    }

    @Test
    @DisplayName("При вызове /createrecord без аргументов должен перейти в интерактивный режим")
    public void successCreateRecordCommand_NoArguments_ShouldEnterInteractiveMode() throws IOException {
        testSession.setAuth(createTestAuth());
        commandProcessor.processCommand("/createrecord", testChat, testSession);

        assertTrue(testSession.getState() instanceof CreateRecordState);
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Введите данные для создания записи")
        ));
    }

    @Test
    @DisplayName("При успешном создании записи с полной командой должен отправить подтверждение")
    public void successCreateRecordCommand_FullCommand_ShouldSendConfirmation() throws IOException {
        testSession.setAuth(createTestAuth());

        when(yClientsAPIService.createRecord(eq(1L), any()))
                .thenReturn(new CommonAPIResponse<>("success", null, "Record created"));

        String command = "/createrecord 2023-05-20 14:00 123 Стрижка Иванов Иван Иванович 79161234567 60 client@mail.ru 24 48 Комментарий";
        commandProcessor.processCommand(command, testChat, testSession);

        verify(yClientsAPIService).createRecord(eq(1L), any(RequestCreateDTO.class));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Record created")
        ));
    }

    @Test
    @DisplayName("При неверном формате команды /createrecord должен отправить сообщение об ошибке")
    public void errorCreateRecordCommand_InvalidFormat_ShouldSendErrorMessage() throws IOException {
        testSession.setAuth(createTestAuth());
        String invalidCommand = "/createrecord invalid";
        commandProcessor.processCommand(invalidCommand, testChat, testSession);

        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Ошибка формата")
        ));
    }

    @Test
    @DisplayName("При успешном создании паузы должен отправить подтверждение")
    public void successCreatePauseCommand_ValidDateTime_ShouldSendConfirmation() throws IOException {
        testSession.setAuth(createTestAuth());
        String datetime = LocalDateTime.now().format(formatter);

        when(yClientsAPIService.createPauseRecord(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new CommonAPIResponse<>("success", null, "Pause created"));

        commandProcessor.processCommand("/createpause " + datetime, testChat, testSession);

        verify(yClientsAPIService).createPauseRecord(eq(1L), any(LocalDateTime.class));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Pause created")
        ));
    }

    @Test
    @DisplayName("При успешном удалении записи должен отправить подтверждение")
    public void successDeleteRecordCommand_ValidRecordId_ShouldSendConfirmation() throws IOException {
        testSession.setAuth(createTestAuth());

        when(yClientsAPIService.deleteRecord(eq(1L), eq(123L)))
                .thenReturn(new CommonAPIResponse<>("success", null, "Record deleted"));

        commandProcessor.processCommand("/deleterecord 123", testChat, testSession);

        verify(yClientsAPIService).deleteRecord(eq(1L), eq(123L));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Record deleted")
        ));
    }

    @Test
    @DisplayName("При успешном обновлении записи должен отправить информацию об обновленной записи")
    public void successUpdateRecordCommand_ValidParameters_ShouldSendUpdatedRecordInfo() throws IOException {
        testSession.setAuth(createTestAuth());
        Record updatedRecord = createTestRecord();

        when(yClientsAPIService.updateRecord(eq(1L), eq(123L), any()))
                .thenReturn(new CommonAPIResponse<>("success", updatedRecord, "OK"));

        String command = "/updaterecord 123 2023-05-20 15:00 Новый комментарий";
        commandProcessor.processCommand(command, testChat, testSession);

        verify(yClientsAPIService).updateRecord(eq(1L), eq(123L), any(RequestUpdateDTO.class));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Обновленная запись")
        ));
    }

    @Test
    @DisplayName("При успешной настройке SMS-уведомления должен отправить информацию о записи")
    public void successSetSmsNotificationCommand_ValidParameters_ShouldSendRecordInfo() throws IOException {
        testSession.setAuth(createTestAuth());
        Record record = createTestRecord();

        when(yClientsAPIService.setSmsNotificationRequired(eq(1L), eq(123L), eq(24)))
                .thenReturn(new CommonAPIResponse<>("success", record, "OK"));

        commandProcessor.processCommand("/setsmsnotification 123 24", testChat, testSession);

        verify(yClientsAPIService).setSmsNotificationRequired(eq(1L), eq(123L), eq(24));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("SMS-уведомление настроено")
        ));
    }

    @Test
    @DisplayName("При успешной настройке email-уведомления должен отправить информацию о записи")
    public void successSetEmailNotificationCommand_ValidParameters_ShouldSendRecordInfo() throws IOException {
        testSession.setAuth(createTestAuth());
        Record record = createTestRecord();

        when(yClientsAPIService.setEmailNotificationRequired(eq(1L), eq(123L), eq(48)))
                .thenReturn(new CommonAPIResponse<>("success", record, "OK"));

        commandProcessor.processCommand("/setemailnotification 123 48", testChat, testSession);

        verify(yClientsAPIService).setEmailNotificationRequired(eq(1L), eq(123L), eq(48));
        verify(botController).sendTextMessage(argThat(req ->
                req.getText().contains("Email-уведомление настроено")
        ));
    }

    private Record createTestRecord() {
        Record record = new Record();
        record.setRecordInnerId(123L);
        record.setDatetime(LocalDateTime.of(2023, Month.MAY, 5, 14, 0).atZone(ZoneId.systemDefault()).toInstant().minus(10, ChronoUnit.MINUTES));
        record.setComment("Тестовый комментарий");

        Service service = new Service();
        service.setServiceInnerId(1L);
        service.setTitle("Тестовая услуга");
        record.setServices(Set.of(service));

        return record;
    }

    private Auth createTestAuth() {
        AuthDTO authDTO = new AuthDTO(
                "user_token_123",
                "Test User",
                "+79998887766",
                "test_login",
                "test@email.com",
                "avatar.jpg",
                true,
                "company_123",
                "partner_token_456"
        );
        Auth auth = new Auth(authDTO);
        auth.setId(1L);
        return auth;
    }
}