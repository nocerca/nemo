package no.cerca.dtos.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

/**
 * Created by jadae on 15.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestRecordsDTO {

    private Long staffId;
    private Long clientId;
    private String startDate;
    private String endDate;
    private String cStartDate;
    private String cEndDate;
    private Integer page;
    private Integer count;

    public RequestRecordsDTO(Long staffId, Long clientId, String startDate, String endDate, String cStartDate, String cEndDate, Integer page, Integer count) {
        this.staffId = staffId;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cStartDate = cStartDate;
        this.cEndDate = cEndDate;
        this.page = page;
        this.count = count;
    }

    public RequestRecordsDTO() {
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getcStartDate() {
        return cStartDate;
    }

    public void setcStartDate(String cStartDate) {
        this.cStartDate = cStartDate;
    }

    public String getcEndDate() {
        return cEndDate;
    }

    public void setcEndDate(String cEndDate) {
        this.cEndDate = cEndDate;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}