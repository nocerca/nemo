package no.cerca.entities;

import jakarta.persistence.*;
import no.cerca.dtos.basic.AuthDTO;

/**
 * Created by jadae on 13.03.2025
 */
@Entity
@Table(name = "data")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_token", nullable = false)
    private String userToken;

    private String name;

    private String phone;

    private String login;

    private String email;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    public Auth() {
    }

    public Auth(Long id, String userToken, String name, String phone, String login, String email, boolean isApproved) {
        this.id = id;
        this.userToken = userToken;
        this.name = name;
        this.phone = phone;
        this.login = login;
        this.email = email;
        this.isApproved = isApproved;
    }

    // Constructor for AuthDTO
    public Auth(AuthDTO authDTO) {
        this.userToken = authDTO.getUserToken();
        this.name = authDTO.getName();
        this.phone = authDTO.getPhone();
        this.login = authDTO.getLogin();
        this.email = authDTO.getEmail();
        this.isApproved = authDTO.isApproved();
    }

    // Getters and setters
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
