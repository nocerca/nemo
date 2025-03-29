package no.cerca.dtos.exchange;

import java.util.List;

/**
 * Created by jadae on 29.03.2025
 */
public class RequestMessageDTO {
    private List<Long> clientIds;
    private String text;
    private String subject; // используется только для email

    public RequestMessageDTO(List<Long> clientIds, String text, String subject) {
        this.clientIds = clientIds;
        this.text = text;
        this.subject = subject;
    }

    public RequestMessageDTO(List<Long> clientIds, String text) {
        this(clientIds, text, null);
    }

    public RequestMessageDTO(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
