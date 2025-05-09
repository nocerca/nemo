package no.cerca.dtos.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Created by jadae on 13.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestAuthDTO {

    @NotNull
    private String login;
    @NotNull
    private String password;

    private String partnerToken;

    public RequestAuthDTO(String login, String password, String partnerToken) {
        this.login = login;
        this.password = password;
        this.partnerToken = partnerToken;
    }

    public RequestAuthDTO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPartnerToken() {
        return partnerToken;
    }

    public void setPartnerToken(String partnerToken) {
        this.partnerToken = partnerToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestAuthDTO that)) return false;
        return Objects.equals(getLogin(), that.getLogin()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getPartnerToken(), that.getPartnerToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin(), getPassword(), getPartnerToken());
    }
}