package no.cerca.exception;

/**
 * Created by jadae on 17.04.2025
 */
public class CustomBotException extends RuntimeException {

    public static final String MESSAGE = "Кастомная ошибка";

    public CustomBotException() {
        super(MESSAGE);
    }
    public CustomBotException(String message) {
        super(message);
    }
}
