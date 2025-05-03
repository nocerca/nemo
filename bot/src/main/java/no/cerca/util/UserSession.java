package no.cerca.util;

import no.cerca.entities.Auth;
import no.cerca.state.BotState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jadae on 17.04.2025
 */
public class UserSession {
    private final String userId;
    private Auth auth;
    private BotState state;
    private Map<String, Object> context;

    public UserSession(String userId) {
        this.userId = userId;
        this.context = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public Object getContext(String key) {
        return context.get(key);
    }

    public void putContext(String key, Object value) {
        context.put(key, value);
    }

    public void clearContext() {
        context.clear();
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}