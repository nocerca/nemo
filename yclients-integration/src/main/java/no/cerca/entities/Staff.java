package no.cerca.entities;

import jakarta.persistence.*;

/**
 * Created by jadae on 12.03.2025
 */
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private String specialization;

    public Staff() {
    }

    public Staff(Long id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}