package no.cerca.entities;

import jakarta.persistence.*;
import no.cerca.dtos.basic.ServiceDTO;

/**
 * Created by jadae on 05.03.2025
 */
@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_inner_id", nullable = false, unique = true)
    private Long serviceInnerId;

    @Column(name = "id", nullable = false, unique = true)
    private Long externalServiceId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "seance_length")
    private Integer seanceLength;

    public Service() {
    }

    public Service(Long id, Long serviceInnerId, String title) {
        this.externalServiceId = id;
        this.serviceInnerId = serviceInnerId;
        this.title = title;
        this.weight = 1;
        this.seanceLength = 30;
    }

    public Service(ServiceDTO dto) {
        this.externalServiceId = dto.getId();
        this.title = dto.getTitle();
        this.weight = 1;
        this.seanceLength = 30;
    }

    public Long getExternalServiceId() {
        return externalServiceId;
    }

    public void setExternalServiceId(Long id) {
        this.externalServiceId = id;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getSeanceLength() {
        return seanceLength;
    }

    public void setSeanceLength(Integer seanceLength) {
        this.seanceLength = seanceLength;
    }
}
