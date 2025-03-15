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

    @Column(nullable = false)
    private String title;

    public Service() {
    }

    public Service(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
