package no.cerca.entities;

import jakarta.persistence.*;

/**
 * Created by jadae on 05.03.2025
 */
@Entity
@Table(name = "client")
public class Client {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String name;

    private String surname;
    private String patronymic;

    @Column(nullable = false, unique = true)
    private String email;

    // Default constructor
    public Client() {
    }

    public Client(Long id, String phone, String name, String surname, String patronymic, String email) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
