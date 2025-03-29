package no.cerca.dtos.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jadae on 13.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDTO {
    private Long id;
    @JsonProperty("user_token")
    private String userToken;
    private String name;
    private String phone;
    private String login;
    private String email;
    private String avatar;
    private boolean isApproved;
    private String companyId;
    private String partnerToken;

    public AuthDTO() {
    }

    public AuthDTO(Long id, String userToken, String name, String phone, String login, String email, String avatar, boolean isApproved, String companyId, String partnerToken) {
        this.id = id;
        this.userToken = userToken;
        this.name = name;
        this.phone = phone;
        this.login = login;
        this.email = email;
        this.avatar = avatar;
        this.isApproved = isApproved;
        this.companyId = companyId;
        this.partnerToken = partnerToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPartnerToken() {
        return partnerToken;
    }

    public void setPartnerToken(String partnerToken) {
        this.partnerToken = partnerToken;
    }
}
