package no.cerca.util;

import no.cerca.api.response.CommonAPIResponse;
import no.cerca.api.service.YClientsAPIService;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.ServiceDTO;
import no.cerca.dtos.exchange.*;
import no.cerca.entities.Auth;
import no.cerca.entities.Record;
import no.cerca.entities.Service;
import no.cerca.state.impl.AuthState;
import no.cerca.state.impl.CreateRecordState;
import org.springframework.stereotype.Component;
import ru.mail.im.botapi.BotApiClientController;
import ru.mail.im.botapi.BotLogger;
import ru.mail.im.botapi.api.entity.SendTextRequest;
import ru.mail.im.botapi.fetcher.Chat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jadae on 17.04.2025
 */
@Component
public class CommandProcessor {
    private final BotApiClientController botController;
    private final YClientsAPIService yClientsAPIService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CommandProcessor(BotApiClientController botController, YClientsAPIService yClientsAPIService) {
        this.botController = botController;
        this.yClientsAPIService = yClientsAPIService;
    }

    public void processCommand(String command, Chat chat, UserSession session) {
        String[] parts = command.split(" ", 2);
        String baseCommand = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        try {
            switch (baseCommand) {
                case "/start":
                    handleStartCommand(chat, session);
                    break;
                case "/auth":
                    handleAuthCommand(chat, arguments, session);
                    break;
                case "/records":
                    handleRecordsCommand(chat, session);
                    break;
                case "/recordstoday":
                    handleRecordsTodayCommand(chat, session);
                    break;
                case "/recordsnexthour":
                    handleRecordsNextHourCommand(chat, session);
                    break;
                case "/currentrecord":
                    handleCurrentRecordCommand(chat, session);
                    break;
                case "/nextrecord":
                    handleNextRecordCommand(chat, session);
                    break;
                case "/createrecord":
                    handleCreateRecordCommand(chat, arguments, session);
                    break;
                case "/createpause":
                    handleCreatePauseCommand(chat, arguments, session);
                    break;
                case "/deleterecord":
                    handleDeleteRecordCommand(chat, arguments, session);
                    break;
                case "/updaterecord":
                    handleUpdateRecordCommand(chat, arguments, session);
                    break;
                case "/setsmsnotification":
                    handleSetSmsNotificationCommand(chat, arguments, session);
                    break;
                case "/setemailnotification":
                    handleSetEmailNotificationCommand(chat, arguments, session);
                    break;
                default:
                    sendMessage(chat.getChatId(), "Неизвестная команда. Введите /help для списка команд");
            }
        } catch (Exception e) {
            BotLogger.e(e, "Error processing command: " + command);
            sendMessage(chat.getChatId(), "Произошла ошибка при обработке команды");
        }
    }

    private void handleStartCommand(Chat chat, UserSession session) {
        session.setState(new AuthState());
        sendMessage(chat.getChatId(), """
            Добро пожаловать! Для работы с ботом необходимо авторизоваться.
            Формат команды: /auth [логин] [пароль] [партнерский_токен]
            """);
    }

    private void handleAuthCommand(Chat chat, String arguments, UserSession session) {
        String[] credentials = arguments.split(" ");
        if (credentials.length != 3) {
            sendMessage(chat.getChatId(), "Неверный формат. Требуется: /auth логин пароль токен");
            return;
        }

        RequestAuthDTO authDTO = new RequestAuthDTO(credentials[0], credentials[1], credentials[2]);
        CommonAPIResponse<Auth> response = yClientsAPIService.authenticateUser(authDTO);

        if ("success".equals(response.getStatus())) {
            session.setAuth(response.getData());
            session.setState(null);
            showMainMenu(chat.getChatId());
        } else {
            sendMessage(chat.getChatId(), "Ошибка авторизации: " + response.getMessage());
        }
    }

    private void handleRecordsCommand(Chat chat, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        CommonAPIResponse<List<Record>> response = yClientsAPIService.getAllRecords(
                session.getAuth().getId(),
                new RequestRecordsDTO()
        );

        processRecordsResponse(chat.getChatId(), response, "Все записи");
    }

    private void handleRecordsTodayCommand(Chat chat, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        CommonAPIResponse<List<Record>> response = yClientsAPIService.getAllRecordsForDay(
                session.getAuth().getId(),
                new RequestRecordsDTO()
        );

        processRecordsResponse(chat.getChatId(), response, "Записи на сегодня");
    }

    private void handleRecordsNextHourCommand(Chat chat, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        CommonAPIResponse<List<Record>> response = yClientsAPIService.getAllRecordsForHour(
                session.getAuth().getId(),
                new RequestRecordsDTO()
        );

        processRecordsResponse(chat.getChatId(), response, "Записи в ближайший час");
    }

    private void handleCurrentRecordCommand(Chat chat, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        CommonAPIResponse<Record> response = yClientsAPIService.getCurrentRecord(
                session.getAuth().getId(),
                new RequestRecordsDTO()
        );

        if ("success".equals(response.getStatus())) {
            sendMessage(chat.getChatId(), formatRecord(response.getData(), "Текущая запись"));
        } else {
            sendMessage(chat.getChatId(), "Нет текущих записей: " + response.getMessage());
        }
    }

    private void handleNextRecordCommand(Chat chat, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        CommonAPIResponse<Record> response = yClientsAPIService.getNextRecord(
                session.getAuth().getId(),
                new RequestRecordsDTO()
        );

        if ("success".equals(response.getStatus())) {
            sendMessage(chat.getChatId(), formatRecord(response.getData(), "Следующая запись"));
        } else {
            sendMessage(chat.getChatId(), "Нет следующих записей: " + response.getMessage());
        }
    }

    private void handleCreateRecordCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        if (arguments == null || arguments.trim().isEmpty()) {
            session.setState(new CreateRecordState());
            sendCreateRecordInstructions(chat.getChatId());
            return;
        }

        try {
            Matcher matcher = Pattern.compile(
                    "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}) " + // datetime
                            "(\\d+) " + // staffId
                            "([\\w\\sа-яА-ЯёЁ]+) " + // serviceName
                            "([\\w\\sа-яА-ЯёЁ]+ [\\w\\sа-яА-ЯёЁ]+ [\\w\\sа-яА-ЯёЁ]+ \\+?\\d{10,11}(?: [\\w.-]+@[\\w.-]+\\.\\w+)?) " + // clientData (Фамилия Имя Отчество Телефон Email)
                            "(\\d+)" + // duration
                            "(?: (\\d+))?" + // smsRemainHours (optional)
                            "(?: (\\d+))?" + // emailRemainHours (optional)
                            "(?: (.*))?$" // comment (optional)
            ).matcher(arguments.trim());

            if (!matcher.find()) {
                throw new IllegalArgumentException("Неверный формат команды");
            }

            RequestCreateDTO createDTO = new RequestCreateDTO();
            createDTO.setDatetime(matcher.group(1));
            createDTO.setStaffId(Long.parseLong(matcher.group(2)));

            ServiceDTO service = new ServiceDTO();
            service.setTitle(matcher.group(3));
            createDTO.setServices(List.of(service));

            String[] clientParts = matcher.group(4).split(" ");
            ClientDTO client = new ClientDTO();
            client.setSurname(clientParts[0]);
            client.setName(clientParts.length > 1 ? clientParts[1] : "");
            client.setPatronymic(clientParts.length > 2 ? clientParts[2] : "");
            client.setPhone(clientParts.length > 3 ? clientParts[3] : "");
            client.setEmail(clientParts.length > 4 ? clientParts[4] : "");
            createDTO.setClient(client);

            createDTO.setSeanceLength(Integer.parseInt(matcher.group(5)));

            if (matcher.group(6) != null) {
                createDTO.setSmsRemainHours(Integer.parseInt(matcher.group(6)));
            }
            if (matcher.group(7) != null) {
                createDTO.setEmailRemainHours(Integer.parseInt(matcher.group(7)));
            }
            if (matcher.group(8) != null) {
                createDTO.setComment(matcher.group(8));
            }

            CommonAPIResponse<Void> response = yClientsAPIService.createRecord(
                    session.getAuth().getId(),
                    createDTO
            );

            sendMessage(chat.getChatId(), response.getMessage());
        } catch (Exception e) {
            sendMessage(chat.getChatId(), """
        Ошибка формата. Используйте:
        /createrecord ГГГГ-ММ-ДД ЧЧ:ММ ID_сотрудника Название_услуги "Фамилия Имя Отчество Телефон Email" длительность [sms_часы] [email_часы] [комментарий]
        Пример: /createrecord 2023-05-20 14:00 123 Стрижка "Иванов Иван Иванович 79161234567 client@mail.ru" 60 24 48
        """);
            BotLogger.e(e, "Create record error");
        }
    }

    private void sendCreateRecordInstructions(String chatId) {
        sendMessage(chatId, """
        Введите данные для создания записи:
        1. Дата и время (ГГГГ-ММ-ДД ЧЧ:ММ)
        2. ID сотрудника
        3. Название услуги
        4. Данные клиента (фамилия имя отчество телефон email)
        5. Длительность (мин)
        6. За сколько часов уведомить по SMS (опционально)
        7. За сколько часов уведомить по email (опционально)
        8. Комментарий (опционально)
        
        Можно ввести все данные одной командой как в примере выше
        или отвечать по одному пункту.
        """);
    }

    private void handleCreatePauseCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        try {
            LocalDateTime pauseTime = LocalDateTime.parse(arguments, dateTimeFormatter);
            CommonAPIResponse<Void> response = yClientsAPIService.createPauseRecord(
                    session.getAuth().getId(),
                    pauseTime
            );

            sendMessage(chat.getChatId(), response.getMessage());
        } catch (Exception e) {
            sendMessage(chat.getChatId(), "Неверный формат даты. Используйте ГГГГ-ММ-ДД ЧЧ:ММ");
        }
    }

    private void handleDeleteRecordCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        try {
            Long recordId = Long.parseLong(arguments);
            CommonAPIResponse<Void> response = yClientsAPIService.deleteRecord(
                    session.getAuth().getId(),
                    recordId
            );

            sendMessage(chat.getChatId(), response.getMessage());
        } catch (NumberFormatException e) {
            sendMessage(chat.getChatId(), "Неверный формат ID записи");
        }
    }

    private boolean checkAuth(String chatId, UserSession session) {
        if (session.getAuth() == null) {
            sendMessage(chatId, "Сначала авторизуйтесь с помощью /auth");
            return false;
        }
        return true;
    }

    private void processRecordsResponse(String chatId, CommonAPIResponse<List<Record>> response, String title) {
        if ("success".equals(response.getStatus())) {
            if (response.getData().isEmpty()) {
                sendMessage(chatId, title + ": нет записей");
            } else {
                StringBuilder sb = new StringBuilder(title).append(":\n");
                response.getData().forEach(record ->
                        sb.append(formatRecord(record, "Запись")).append("\n---\n"));
                sendMessage(chatId, sb.toString());
            }
        } else {
            sendMessage(chatId, "Ошибка получения записей: " + response.getMessage());
        }
    }

    private String formatRecord(Record record, String prefix) {
        return String.format("%s:\nДата: %s\nУслуги: %s\nКомментарий: %s",
                prefix,
                record.getDatetime(),
                record.getServices().stream().map(Service::getTitle).collect(Collectors.joining(", ")),
                record.getComment());
    }

    private void showMainMenu(String chatId) {
        String menu = """
            Главное меню:
            /records - Все записи
            /recordstoday - Записи на сегодня
            /recordsnexthour - Записи в ближайший час
            /currentrecord - Текущая запись
            /nextrecord - Следующая запись
            /createrecord - Создать запись
            /createpause - Создать паузу
            /deleterecord - Удалить запись
            /setsmsnotification - Настроить SMS-уведомление
            /setemailnotification - Настроить email-уведомление
            /sendsms - Отправить SMS клиентам
            /sendemail - Отправить email клиентам
            """;
        sendMessage(chatId, menu);
    }

    private void sendMessage(String chatId, String text) {
        try {
            botController.sendTextMessage(new SendTextRequest()
                    .setChatId(chatId)
                    .setText(text));
        } catch (IOException e) {
            BotLogger.e(e, "Error sending message");
        }
    }

    private void handleUpdateRecordCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        try {
            String[] parts = arguments.split(" ", 4);
            if (parts.length < 4) {
                sendMessage(chat.getChatId(), "Формат: /updaterecord [id] [дата время] [комментарий]");
                return;
            }

            String dateText = parts[1] + " " + parts[2];
            Long recordId = Long.parseLong(parts[0]);
            LocalDateTime newDateTime = LocalDateTime.parse(dateText,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            RequestUpdateDTO updateDTO = new RequestUpdateDTO();
            updateDTO.setDatetime(newDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            updateDTO.setComment(parts[2]);

            CommonAPIResponse<Record> response = yClientsAPIService.updateRecord(
                    session.getAuth().getId(),
                    recordId,
                    updateDTO
            );

            if ("success".equals(response.getStatus())) {
                sendMessage(chat.getChatId(), "Запись обновлена:\n" +
                        formatRecord(response.getData(), "Обновленная запись"));
            } else {
                sendMessage(chat.getChatId(), "Ошибка обновления: " + response.getMessage());
            }
        } catch (DateTimeParseException e) {
            sendMessage(chat.getChatId(), "Неверный формат даты. Используйте ГГГГ-ММ-ДД ЧЧ:ММ");
        } catch (NumberFormatException e) {
            sendMessage(chat.getChatId(), "Неверный формат ID записи");
        } catch (Exception e) {
            sendMessage(chat.getChatId(), "Ошибка при обновлении записи");
            BotLogger.e(e, "Update record error");
        }
    }

    private void handleSetSmsNotificationCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        try {
            String[] parts = arguments.split(" ");
            if (parts.length != 2) {
                sendMessage(chat.getChatId(), "Формат: /setsmsnotification [id_записи] [часы_до_напоминания]");
                return;
            }

            Long recordId = Long.parseLong(parts[0]);
            Integer hours = Integer.parseInt(parts[1]);

            CommonAPIResponse<Record> response = yClientsAPIService.setSmsNotificationRequired(
                    session.getAuth().getId(),
                    recordId,
                    hours
            );

            if ("success".equals(response.getStatus())) {
                sendMessage(chat.getChatId(), "SMS-уведомление настроено:\n" +
                        formatRecord(response.getData(), "Запись с уведомлением"));
            } else {
                sendMessage(chat.getChatId(), "Ошибка: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            sendMessage(chat.getChatId(), "Неверный формат чисел. Используйте целые числа");
        } catch (Exception e) {
            sendMessage(chat.getChatId(), "Ошибка настройки уведомления");
            BotLogger.e(e, "Set SMS notification error");
        }
    }

    private void handleSetEmailNotificationCommand(Chat chat, String arguments, UserSession session) {
        if (!checkAuth(chat.getChatId(), session)) return;

        try {
            String[] parts = arguments.split(" ");
            if (parts.length != 2) {
                sendMessage(chat.getChatId(), "Формат: /setemailnotification [id_записи] [часы_до_напоминания]");
                return;
            }

            Long recordId = Long.parseLong(parts[0]);
            Integer hours = Integer.parseInt(parts[1]);

            CommonAPIResponse<Record> response = yClientsAPIService.setEmailNotificationRequired(
                    session.getAuth().getId(),
                    recordId,
                    hours
            );

            if ("success".equals(response.getStatus())) {
                sendMessage(chat.getChatId(), "Email-уведомление настроено:\n" +
                        formatRecord(response.getData(), "Запись с уведомлением"));
            } else {
                sendMessage(chat.getChatId(), "Ошибка: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            sendMessage(chat.getChatId(), "Неверный формат чисел. Используйте целые числа");
        } catch (Exception e) {
            sendMessage(chat.getChatId(), "Ошибка настройки уведомления");
            BotLogger.e(e, "Set email notification error");
        }
    }

}