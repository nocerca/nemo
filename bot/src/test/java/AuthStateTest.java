import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.entities.Auth;
import no.cerca.state.impl.AuthState;
import no.cerca.util.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mail.im.botapi.BotApiClientController;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by jadae on 02.05.2025
 */
public class AuthStateTest {
    private final BotApiClientController botController = mock(BotApiClientController.class);
    private final YClientsAPIService yClientsService = mock(YClientsAPIService.class);
    private final AuthState authState = new AuthState();

    @Test
    @DisplayName("Запрос данных авторизации при некорректном вводе")
    public void shouldRequestCredentialsFormatOnInvalidInput() throws IOException {
        String chatId = "chat123";
        UserSession session = new UserSession("user123");

        authState.handleInput("invalid format", chatId, session, botController, yClientsService);

        verify(botController).sendTextMessage(argThat(req ->
                req.getChatId().equals(chatId) &&
                        req.getText().contains("Формат: логин пароль токен_партнера")
        ));
    }

    @Test
    @DisplayName("Успешная установка авторизации и очистка состояния в сессии")
    public void shouldSetAuthAndClearStateOnSuccess() throws IOException {
        String chatId = "chat123";
        UserSession session = new UserSession("user123");

        Auth auth = new Auth();
        auth.setId(1L);
        CommonAPIResponse<Auth> successResponse = new CommonAPIResponse<>("success", auth, "Успешное получение авторизации");
        successResponse.setStatus("success");
        successResponse.setData(auth);

        when(yClientsService.authenticateUser(any())).thenReturn(successResponse);

        authState.handleInput("login pass token", chatId, session, botController, yClientsService);

        assertEquals(auth, session.getAuth());
        assertNull(session.getState());
        verify(botController).sendTextMessage(argThat(req ->
                req.getChatId().equals(chatId) &&
                        req.getText().contains("Главное меню")
        ));
    }
}