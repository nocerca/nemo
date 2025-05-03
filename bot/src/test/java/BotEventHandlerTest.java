import no.cerca.api.service.YClientsAPIService;
import no.cerca.state.BotState;
import no.cerca.util.BotEventHandler;
import no.cerca.util.CommandProcessor;
import no.cerca.util.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.fetcher.event.NewMessageEvent;
import util.TestableNewMessageEvent;

import java.util.List;

import static org.mockito.Mockito.*;

public class BotEventHandlerTest {
    private final YClientsAPIService yClientsService = mock(YClientsAPIService.class);
    private final BotApiClientController botController = mock(BotApiClientController.class);
    private final CommandProcessor commandProcessor = mock(CommandProcessor.class);
    private final BotEventHandler botEventHandler = new BotEventHandler(
            yClientsService, botController, commandProcessor
    );

    @Test
    @DisplayName("Успешная идентификация команды")
    public void shouldProcessCommandWhenMessageStartsWithSlash() {
        NewMessageEvent event = new TestableNewMessageEvent("/start", "user123", "chat123");
        botEventHandler.onEventFetch(List.of(event));
        verify(commandProcessor).processCommand(
                eq("/start"),
                eq(event.getChat()),
                any(UserSession.class)
        );
    }

    @Test
    @DisplayName("Успешная обработка состояния при наличии")
    public void shouldHandleStateWhenSessionHasState() {
        NewMessageEvent event = new TestableNewMessageEvent("test input", "user123", "chat123");
        BotState mockState = mock(BotState.class);

        botEventHandler.onEventFetch(List.of(event));
        UserSession session = botEventHandler.getUserSession("user123");
        session.setState(mockState);

        botEventHandler.onEventFetch(List.of(event));

        verify(mockState).handleInput(
                eq("test input"),
                eq("chat123"),
                same(session),
                same(botController),
                same(yClientsService)
        );
    }

    @Test
    @DisplayName("Игнорирование пустых сообщений")
    public void shouldIgnoreEmptyMessage() {
        NewMessageEvent event = new TestableNewMessageEvent("", "user123", "chat123");
        botEventHandler.onEventFetch(List.of(event));
        verifyNoInteractions(commandProcessor);
    }

}