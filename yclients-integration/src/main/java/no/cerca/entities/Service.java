package no.cerca.entities;

import jakarta.persistence.*;

/**
 * Created by jadae on 05.03.2025
 */
@Entity
@Table(name = "service")
public class Service {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private Long serviceInnerId;

    @Column(nullable = false)
    private String title;

    public Service() {
    }

    public Service(Long id, Long serviceInnerId, String title) {
        this.id = id;
        this.serviceInnerId = serviceInnerId;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceInnerId() {
        return serviceInnerId;
    }

    public void setServiceInnerId(Long serviceInnerId) {
        this.serviceInnerId = serviceInnerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
