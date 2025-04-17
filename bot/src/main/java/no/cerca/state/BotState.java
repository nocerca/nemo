package no.cerca.state;

import no.cerca.util.UserSession;
import no.cerca.api.service.YClientsAPIService;
import ru.mail.im.botapi.BotApiClientController;

/**
 * Created by jadae on 17.04.2025
 */
public interface BotState {
    void handleInput(String input, String chatId, UserSession session, BotApiClientController botController, YClientsAPIService yClientsService);
}
