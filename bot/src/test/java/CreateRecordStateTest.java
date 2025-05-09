import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.exchange.RequestCreateDTO;
import no.cerca.entities.Auth;
import no.cerca.state.impl.CreateRecordState;
import no.cerca.util.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.api.entity.SendTextRequest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateRecordStateTest {
    private final BotApiClientController botController = mock(BotApiClientController.class);
    private final YClientsAPIService yClientsService = mock(YClientsAPIService.class);
    private final CreateRecordState state = new CreateRecordState();

    @Test
    @DisplayName("Успешное создание записи")
    void shouldCreateRecordThroughAllSteps() throws IOException {
        String chatId = "chat123";
        UserSession session = new UserSession("user123");
        session.setAuth(createTestAuth());

        CommonAPIResponse<Void> successResponse = new CommonAPIResponse<>("success", null, "Record created");
        when(yClientsService.createRecord(any(), any())).thenReturn(successResponse);

        // Act
        // Шаг 1: Дата и время
        state.handleInput("2023-05-20 14:00", chatId, session, botController, yClientsService);

        // Шаг 2: ID сотрудника
        state.handleInput("123", chatId, session, botController, yClientsService);

        // Шаг 3: Название услуги
        state.handleInput("sample title", chatId, session, botController, yClientsService);

        // Шаг 4: Данные клиента (фамилия имя отчество телефон email)
        state.handleInput("фамилия имя отчество 79054564433 vano@gmail.com", chatId, session, botController, yClientsService);

        // Шаг 5: Длительность
        state.handleInput("60", chatId, session, botController, yClientsService);

        // Шаг 6: SMS уведомление
        state.handleInput("0", chatId, session, botController, yClientsService);

        // Шаг 7: Email уведомление
        state.handleInput("0", chatId, session, botController, yClientsService);

        // Шаг 8: Комментарий
        state.handleInput("Тестовый комментарий", chatId, session, botController, yClientsService);

        // Шаг 9: Подтверждение
        state.handleInput("1", chatId, session, botController, yClientsService);

        // 1. Проверка отправленных сообщений
        ArgumentCaptor<SendTextRequest> messageCaptor = ArgumentCaptor.forClass(SendTextRequest.class);
        verify(botController, times(9)).sendTextMessage(messageCaptor.capture());

        List<String> sentMessages = messageCaptor.getAllValues().stream()
                .map(SendTextRequest::getText)
                .toList();

        assertThat(sentMessages.get(0).toLowerCase()).contains("id сотрудника");
        assertThat(sentMessages.get(1).toLowerCase()).contains("название услуги");
        assertThat(sentMessages.get(2).toLowerCase()).contains("данные клиента");
        assertThat(sentMessages.get(3).toLowerCase()).contains("длительность в минутах");
        assertThat(sentMessages.get(4).toLowerCase()).contains("уведомить по sms");
        assertThat(sentMessages.get(5).toLowerCase()).contains("уведомить по email");
        assertThat(sentMessages.get(6).toLowerCase()).contains("введите комментарий");
        assertThat(sentMessages.get(7).toLowerCase()).contains("подтвердите данные записи");

        // 2. Проверка созданной записи
        ArgumentCaptor<RequestCreateDTO> recordCaptor = ArgumentCaptor.forClass(RequestCreateDTO.class);
        verify(yClientsService).createRecord(eq(session.getAuth().getId()), recordCaptor.capture());

        RequestCreateDTO createdRecord = recordCaptor.getValue();

        assertThat(createdRecord)
                .hasFieldOrPropertyWithValue("datetime", "2023-05-20 14:00")
                .hasFieldOrPropertyWithValue("staffId", 123L)
                .hasFieldOrPropertyWithValue("seanceLength", 60)
                .hasFieldOrPropertyWithValue("comment", "Тестовый комментарий");

        assertThat(createdRecord.getServices())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("title", "sample title");

        assertThat(createdRecord.getClient())
                .hasFieldOrPropertyWithValue("surname", "фамилия")
                .hasFieldOrPropertyWithValue("name", "имя")
                .hasFieldOrPropertyWithValue("patronymic", "отчество")
                .hasFieldOrPropertyWithValue("phone", "79054564433")
                .hasFieldOrPropertyWithValue("email", "vano@gmail.com");

        // Проверка состояния сессии
        assertThat(session.getState()).isNull();
    }

    @Test
    @DisplayName("Ошибка при некорректном формате ввода данных клиента")
    void errorWhileEnteringClientData() throws IOException {
        // Arrange
        String chatId = "chat123";
        UserSession session = new UserSession("user123");
        session.setAuth(createTestAuth());

        CommonAPIResponse<Void> successResponse = new CommonAPIResponse<>("success", null, "Record created");
        when(yClientsService.createRecord(any(), any())).thenReturn(successResponse);

        // Act
        // Шаг 1: Дата и время
        state.handleInput("2023-05-20 14:00", chatId, session, botController, yClientsService);

        // Шаг 2: ID сотрудника
        state.handleInput("123", chatId, session, botController, yClientsService);

        // Шаг 3: Название услуги
        state.handleInput("sample title", chatId, session, botController, yClientsService);

        // Шаг 4: Данные клиента (фамилия имя отчество телефон email)
        state.handleInput("invalid", chatId, session, botController, yClientsService);

        // 1. Проверка отправленных сообщений
        ArgumentCaptor<SendTextRequest> messageCaptor = ArgumentCaptor.forClass(SendTextRequest.class);
        verify(botController, times(4)).sendTextMessage(messageCaptor.capture());

        List<String> sentMessages = messageCaptor.getAllValues().stream()
                .map(SendTextRequest::getText)
                .toList();

        assertThat(sentMessages.get(0).toLowerCase()).contains("id сотрудника");
        assertThat(sentMessages.get(1).toLowerCase()).contains("название услуги");
        assertThat(sentMessages.get(2).toLowerCase()).contains("данные клиента");
        assertThat(sentMessages.get(3).toLowerCase()).contains("ошибка: данные введены неправильно");
    }

    @Test
    @DisplayName("Успешное создание записи пошагово: перед каждым успешным шагом вводятся некорректные данные")
    void should() throws IOException {
        String chatId = "chat123";
        UserSession session = new UserSession("user123");
        session.setAuth(createTestAuth());

        CommonAPIResponse<Void> successResponse = new CommonAPIResponse<>("success", null, "Record created");
        when(yClientsService.createRecord(any(), any())).thenReturn(successResponse);

        // Act
        // Шаг 1: Дата и время
        state.handleInput("2023-05-20 14:00", chatId, session, botController, yClientsService);

        // Шаг 2: ID сотрудника
        state.handleInput("123", chatId, session, botController, yClientsService);

        // Шаг 3: Название услуги
        state.handleInput("sample title", chatId, session, botController, yClientsService);

        // Шаг 4: Данные клиента (фамилия имя отчество телефон email)
        state.handleInput("фамилия имя отчество 79054564433 vano@gmail.com", chatId, session, botController, yClientsService);

        // Шаг 5: Длительность (некорректный ввод)
        state.handleInput("invalid", chatId, session, botController, yClientsService);

        // Шаг 6: Длительность
        state.handleInput("60", chatId, session, botController, yClientsService);

        // Шаг 7: SMS уведомление (некорректный ввод)
        state.handleInput("invalid", chatId, session, botController, yClientsService);

        // Шаг 8: SMS уведомление
        state.handleInput("0", chatId, session, botController, yClientsService);

        // Шаг 9: Email уведомление (некорректный ввод)
        state.handleInput("invalid", chatId, session, botController, yClientsService);

        // Шаг 10: Email уведомление
        state.handleInput("0", chatId, session, botController, yClientsService);

        // Шаг 11: Комментарий
        state.handleInput("Тестовый комментарий", chatId, session, botController, yClientsService);

        // Шаг 12: Подтверждение (некорректный ввод)
        state.handleInput("invalid", chatId, session, botController, yClientsService);

        // Шаг 13: Подтверждение
        state.handleInput("1", chatId, session, botController, yClientsService);

        // 1. Проверка отправленных сообщений
        ArgumentCaptor<SendTextRequest> messageCaptor = ArgumentCaptor.forClass(SendTextRequest.class);
        verify(botController, times(13)).sendTextMessage(messageCaptor.capture());

        List<String> sentMessages = messageCaptor.getAllValues().stream()
                .map(SendTextRequest::getText)
                .toList();

        assertThat(sentMessages.get(0).toLowerCase()).contains("id сотрудника");
        assertThat(sentMessages.get(1).toLowerCase()).contains("название услуги");
        assertThat(sentMessages.get(2).toLowerCase()).contains("данные клиента");
        assertThat(sentMessages.get(3).toLowerCase()).contains("длительность в минутах");
        assertThat(sentMessages.get(4).toLowerCase()).contains("число введено некорректно");
        assertThat(sentMessages.get(5).toLowerCase()).contains("уведомить по sms");
        assertThat(sentMessages.get(6).toLowerCase()).contains("число введено некорректно");
        assertThat(sentMessages.get(7).toLowerCase()).contains("уведомить по email");
        assertThat(sentMessages.get(8).toLowerCase()).contains("число введено некорректно");
        assertThat(sentMessages.get(9).toLowerCase()).contains("введите комментарий");
        assertThat(sentMessages.get(10).toLowerCase()).contains("'0' - нет или '1' - да");
        assertThat(sentMessages.get(11).toLowerCase()).contains("создание записи отменено");
        assertThat(sentMessages.get(12).toLowerCase()).contains("record created");

        // 2. Проверка созданной записи
        ArgumentCaptor<RequestCreateDTO> recordCaptor = ArgumentCaptor.forClass(RequestCreateDTO.class);
        verify(yClientsService).createRecord(eq(session.getAuth().getId()), recordCaptor.capture());

        RequestCreateDTO createdRecord = recordCaptor.getValue();

        assertThat(createdRecord)
                .hasFieldOrPropertyWithValue("datetime", "2023-05-20 14:00")
                .hasFieldOrPropertyWithValue("staffId", 123L)
                .hasFieldOrPropertyWithValue("seanceLength", 60)
                .hasFieldOrPropertyWithValue("comment", "Тестовый комментарий");

        assertThat(createdRecord.getServices())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("title", "sample title");

        assertThat(createdRecord.getClient())
                .hasFieldOrPropertyWithValue("surname", "фамилия")
                .hasFieldOrPropertyWithValue("name", "имя")
                .hasFieldOrPropertyWithValue("patronymic", "отчество")
                .hasFieldOrPropertyWithValue("phone", "79054564433")
                .hasFieldOrPropertyWithValue("email", "vano@gmail.com");

        // Проверка состояния сессии
        assertThat(session.getState()).isNull();
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
        return new Auth(authDTO);
    }
}