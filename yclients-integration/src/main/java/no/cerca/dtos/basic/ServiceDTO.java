package no.cerca.dtos.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jadae on 13.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDTO {
    private Long id;
    private String title;

    public ServiceDTO() {
    }

    public ServiceDTO(Long id, String title) {
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
