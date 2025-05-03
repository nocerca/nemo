package no.cerca.state.impl;

import no.cerca.util.UserSession;
import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.dtos.exchange.RequestAuthDTO;
import no.cerca.entities.Auth;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.BotLogger;
import ru.mail.im.botapi.api.entity.SendTextRequest;

import java.io.IOException;

/**
 * Created by jadae on 17.04.2025
 */
public class AuthState implements BotState {
    @Override
    public void handleInput(String input, String chatId, UserSession session,
                            BotApiClientController botController, YClientsAPIService yClientsService) {
        try {
            String[] credentials = input.split(" ");
            if (credentials.length != 3) {
                botController.sendTextMessage(
                        new SendTextRequest()
                                .setChatId(chatId)
                                .setText("Формат: логин пароль токен_партнера")
                );
                return;
            }

            RequestAuthDTO authDTO = new RequestAuthDTO(
                    credentials[0],
                    credentials[1],
                    credentials[2]
            );

            CommonAPIResponse<Auth> response = yClientsService.authenticateUser(authDTO);

            if ("success".equals(response.getStatus())) {
                session.setAuth(response.getData());
                session.setState(null);
                showMainMenu(chatId, botController);
            } else {
                botController.sendTextMessage(
                        new SendTextRequest()
                                .setChatId(chatId)
                                .setText("Ошибка: " + response.getMessage())
                );
            }
        } catch (Exception e) {
            BotLogger.e(e, "Auth error for user: " + session.getUserId());
        }
    }

    private void showMainMenu(String chatId, BotApiClientController botController) {
        try {
            String menu = "Главное меню:\n" +
                    "/records - Мои записи\n" +
                    "/create - Создать запись\n" +
                    "/help - Помощь";

            botController.sendTextMessage(new SendTextRequest().setChatId(chatId).setText(menu));
        } catch (IOException e) {
            BotLogger.e(e, "Ошибка отправки меню");
        }
    }
}
