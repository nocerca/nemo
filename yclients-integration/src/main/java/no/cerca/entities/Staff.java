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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_inner_id", nullable = false, unique = true)
    private Long staffInnerId;

    @Column(name = "id", nullable = false, unique = true)
    private Long externalStaffId;

    @Column(nullable = false)
    private String name;

    @Column(name = "specialization")
    private String specialization;

    public Staff() {
    }

    public Staff(Long id, String name, String specialization) {
        this.externalStaffId = id;
        this.name = name;
        this.specialization = specialization;
    }

    public Staff(StaffDTO dto) {
        this.externalStaffId = dto.getId();
        this.name = dto.getName();
        this.specialization = dto.getSpecialization();
    }

    public Long getExternalStaffId() {
        return externalStaffId;
    }

    public void setExternalStaffId(Long id) {
        this.externalStaffId = id;
    }

    public Long getServiceInnerId() {
        return staffInnerId;
    }

    public void setServiceInnerId(Long serviceInnerId) {
        this.staffInnerId = serviceInnerId;
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