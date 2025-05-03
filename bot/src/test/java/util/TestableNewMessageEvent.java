package util;

import ru.mail.im.botapi.fetcher.Chat;
import ru.mail.im.botapi.fetcher.User;
import ru.mail.im.botapi.fetcher.event.NewMessageEvent;

import java.lang.reflect.Field;

/**
 * Created by jadae on 02.05.2025
 */
public class TestableNewMessageEvent extends NewMessageEvent {
    private final String text;
    private final User from;
    private final Chat chat;

    public TestableNewMessageEvent(String text, String userId, String chatId) {
        this.text = text;

        this.from = new User();
        try {
            Field userIdField = User.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(from, userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.chat = new Chat();
        try {
            Field chatIdField = Chat.class.getDeclaredField("chatId");
            chatIdField.setAccessible(true);
            chatIdField.set(chat, chatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public User getFrom() {
        return from;
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public long getMessageId() {
        return 12345L;
    }

    @Override
    public long getTimestamp() {
        return System.currentTimeMillis();
    }
}