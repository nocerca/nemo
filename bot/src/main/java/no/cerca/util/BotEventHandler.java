package no.cerca.util;

import lombok.RequiredArgsConstructor;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.state.AuthState;
import org.springframework.stereotype.Component;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.BotLogger;
import ru.mail.im.botapi.api.entity.SendTextRequest;
import ru.mail.im.botapi.fetcher.Chat;
import ru.mail.im.botapi.fetcher.OnEventFetchListener;
import ru.mail.im.botapi.fetcher.User;
import ru.mail.im.botapi.fetcher.event.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jadae on 17.04.2025
 */
@Component
@RequiredArgsConstructor
public class BotEventHandler implements OnEventFetchListener {
    private final Map<String, UserSession> userSessions = new ConcurrentHashMap<>();
    private final YClientsAPIService yClientsService;
    private final BotApiClientController botController;
    private final CommandProcessor commandProcessor;

    @Override
    public void onEventFetch(List<Event> events) {
        for (Event event : events) {
            if (event instanceof NewMessageEvent) {
                handleNewMessage((NewMessageEvent) event);
            } else if (event instanceof CallbackQueryEvent) {
                handleCallback((CallbackQueryEvent) event);
            }
        }
    }

    private void handleNewMessage(NewMessageEvent event) {
        try {
            User user = event.getFrom();
            String text = event.getText();
            Chat chat = event.getChat();

            if (text == null || text.trim().isEmpty()) return;

            String userId = user.getUserId();
            UserSession session = userSessions.computeIfAbsent(
                    userId,
                    id -> new UserSession(userId)
            );

            if (text.startsWith("/")) {
                commandProcessor.processCommand(text, chat, session);
            } else if (session.getState() != null) {
                session.getState().handleInput(
                        text,
                        chat.getChatId(),
                        session,
                        botController,
                        yClientsService
                );
            }
        } catch (Exception e) {
            BotLogger.e(e, "Error handling message");
        }
    }

    private void handleCallback(CallbackQueryEvent event) {
        // Обработка callback-ов
    }
}