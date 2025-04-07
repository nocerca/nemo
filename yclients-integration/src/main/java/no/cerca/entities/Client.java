package no.cerca.entities;

import jakarta.persistence.*;
import no.cerca.dtos.basic.ClientDTO;

/**
 * Created by jadae on 05.03.2025
 */
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_inner_id")
    private Long clientInnerId;

    @Column(name = "id")
    private Long clientExternalId;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public Client() {
    }

    public Client(Long clientExternalId, String phone, String name, String surname, String patronymic, String email) {
        this.clientExternalId = clientExternalId;
        this.phone = phone;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
    }

    public Client(ClientDTO dto) {
        this.clientExternalId = dto.getId();
        this.phone = dto.getPhone();
        this.name = dto.getName();
        this.surname = dto.getSurname();
        this.patronymic = dto.getPatronymic();
        this.email = dto.getEmail();
    }

    public Long getClientInnerId() {
        return clientInnerId;
    }

    public void setClientInnerId(Long clientInnerId) {
        this.clientInnerId = clientInnerId;
    }

    public Long getClientExternalId() {
        return clientExternalId;
    }

    public void setClientExternalId(Long id) {
        this.clientExternalId = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
