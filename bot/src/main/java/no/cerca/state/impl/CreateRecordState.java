package no.cerca.state.impl;

import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.ServiceDTO;
import no.cerca.dtos.exchange.RequestCreateDTO;
import no.cerca.util.UserSession;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.BotLogger;
import ru.mail.im.botapi.api.entity.SendTextRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jadae on 17.04.2025
 */
public class CreateRecordState implements BotState {
    private enum Step {
        DATETIME, STAFF, SERVICE, CLIENT, DURATION,
        SMS_NOTIFY, EMAIL_NOTIFY, COMMENT, CONFIRM
    }

    private Step currentStep = Step.DATETIME;
    private final RequestCreateDTO draft = new RequestCreateDTO();
    private final List<ServiceDTO> services = new ArrayList<>();
    private final Map<String, Object> customFields = new HashMap<>();

    @Override
    public void handleInput(String input, String chatId, UserSession session,
                            BotApiClientController botController, YClientsAPIService yClientsService) {
        try {
            switch (currentStep) {
                case DATETIME:
                    draft.setDatetime(input);
                    currentStep = Step.STAFF;
                    askForInput(chatId, botController, "Введите ID сотрудника:");
                    break;

                case STAFF:
                    draft.setStaffId(Long.parseLong(input));
                    currentStep = Step.SERVICE;
                    askForInput(chatId, botController, "Введите ID услуги:");
                    break;

                case SERVICE:
                    ServiceDTO service = new ServiceDTO();
                    service.setId(Long.parseLong(input));
                    services.add(service);
                    draft.setServices(services);
                    currentStep = Step.CLIENT;
                    askForInput(chatId, botController, "Введите ID клиента:");
                    break;

                case CLIENT:
                    List<String> creds = Arrays.stream(input.split(" ")).toList();
                    if (creds.size() != 5) {
                        throw new CustomBotException("данные введены неправильно, придерживайтесь формата (фамилия имя отчество телефон email)");
                    }

                    ClientDTO client = new ClientDTO();
                    client.setId(Long.parseLong(input));
                    draft.setClient(client);
                    currentStep = Step.DURATION;
                    askForInput(chatId, botController, "Введите длительность в минутах:");
                    break;

                case DURATION:
                    try {
                        draft.setSeanceLength(Integer.parseInt(input));
                    } catch (Exception e) {
                        throw new CustomBotException("число введено некорректно");
                    }
                    currentStep = Step.SMS_NOTIFY;
                    askForInput(chatId, botController, "За сколько часов уведомить по SMS (0 чтобы пропустить):");
                    break;

                case SMS_NOTIFY:
                    int smsHours;
                    try {
                        smsHours = Integer.parseInt(input);
                    } catch (Exception e) {
                        throw new CustomBotException("число введено некорректно");
                    }
                    if (smsHours > 0) {
                        draft.setSmsRemainHours(smsHours);
                        draft.setSendSms(true);
                    }
                    currentStep = Step.EMAIL_NOTIFY;
                    askForInput(chatId, botController, "За сколько часов уведомить по email (0 чтобы пропустить):");
                    break;

                case EMAIL_NOTIFY:
                    int emailHours;
                    try {
                        emailHours = Integer.parseInt(input);
                    } catch (Exception e) {
                        throw new CustomBotException("число введено некорректно");
                    }
                    if (emailHours > 0) {
                        draft.setEmailRemainHours(emailHours);
                    }
                    currentStep = Step.COMMENT;
                    askForInput(chatId, botController, "Введите комментарий (или 'пропустить'):");
                    break;

                case COMMENT:
                    if (!"пропустить".equalsIgnoreCase(input)) {
                        draft.setComment(input);
                    }
                    currentStep = Step.CONFIRM;
                    sendConfirmation(chatId, botController);
                    break;

                case CONFIRM:
                    if ("да".equalsIgnoreCase(input)) {
                        CommonAPIResponse<Void> response = yClientsService.createRecord(
                                session.getAuth().getId(),
                                draft
                        );
                        botController.sendTextMessage(new SendTextRequest()
                                .setChatId(chatId)
                                .setText(response.getMessage()));
                    } else {
                        botController.sendTextMessage(new SendTextRequest()
                                .setChatId(chatId)
                                .setText("Создание записи отменено"));
                    }
                    session.setState(null);
                    break;
            }
        } catch (Exception e) {
            handleError(chatId, botController, e);
        }
    }

    private void askForInput(String chatId, BotApiClientController botController, String message) {
        try {
            botController.sendTextMessage(new SendTextRequest()
                    .setChatId(chatId)
                    .setText(message));
        } catch (IOException e) {
            BotLogger.e(e, "Error sending message");
        }
    }

    private void sendConfirmation(String chatId, BotApiClientController botController) {
        try {
            StringBuilder sb = new StringBuilder("Подтвердите данные записи:\n");
            sb.append("Дата: ").append(draft.getDatetime()).append("\n");
            sb.append("Сотрудник ID: ").append(draft.getStaffId()).append("\n");
            sb.append("Услуги: ").append(draft.getServices().stream()
                    .map(ServiceDTO::getId)
                    .collect(Collectors.toList())).append("\n");
            sb.append("Клиент ID: ").append(draft.getClient().getId()).append("\n");
            sb.append("Длительность: ").append(draft.getSeanceLength()).append(" мин\n");
            if (draft.getSmsRemainHours() != null) {
                sb.append("SMS уведомление за: ").append(draft.getSmsRemainHours()).append(" ч\n");
            }
            if (draft.getEmailRemainHours() != null) {
                sb.append("Email уведомление за: ").append(draft.getEmailRemainHours()).append(" ч\n");
            }
            if (draft.getComment() != null) {
                sb.append("Комментарий: ").append(draft.getComment()).append("\n");
            }
            sb.append("\nОтветьте 'да' или 'нет'");

            botController.sendTextMessage(new SendTextRequest()
                    .setChatId(chatId)
                    .setText(sb.toString()));
        } catch (IOException e) {
            BotLogger.e(e, "Error sending confirmation");
        }
    }

    private void handleError(String chatId, BotApiClientController botController, Exception e) {
        try {
            String errorMessage = "Ошибка: " + e.getMessage() + "\nПовторите ввод:";
            botController.sendTextMessage(new SendTextRequest()
                    .setChatId(chatId)
                    .setText(errorMessage));
        } catch (IOException ioException) {
            BotLogger.e(ioException, "Error sending error message");
        }
    }
}