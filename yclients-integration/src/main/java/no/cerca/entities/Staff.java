package no.cerca.entities;

import jakarta.persistence.*;
import no.cerca.dtos.basic.StaffDTO;

/**
 * Created by jadae on 12.03.2025
 */
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private Long serviceInnerId;

    @Column(nullable = false)
    private String name;

    private String specialization;

    public Staff() {
    }

    public Staff(Long id, Long serviceInnerId, String name, String specialization) {
        this.id = id;
        this.serviceInnerId = serviceInnerId;
        this.name = name;
        this.specialization = specialization;
    }

    public Staff(StaffDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.specialization = dto.getSpecialization();
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