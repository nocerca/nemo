package no.cerca.dtos.exchange;

/**
 * Created by jadae on 13.03.2025
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.ServiceDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestUpdateDTO {
    @NotNull
    private Long staffId;
    private List<ServiceDTO> services;
    @NotNull
    private ClientDTO client;
    private Boolean saveIfBusy;
    @NotNull
    private String datetime;
    private Integer seanceLength;
    private Boolean sendSms;
    private String comment;
    private Integer smsRemainHours;
    private Integer emailRemainHours;
    private Integer attendance;

    public RequestUpdateDTO() {
    }

    public RequestUpdateDTO(Long staffId, List<ServiceDTO> services, ClientDTO client, Boolean saveIfBusy, String datetime, Integer seanceLength, Boolean sendSms, String comment, Integer smsRemainHours, Integer emailRemainHours, Integer attendance) {
        this.staffId = staffId;
        this.services = services;
        this.client = client;
        this.saveIfBusy = saveIfBusy;
        this.datetime = datetime;
        this.seanceLength = seanceLength;
        this.sendSms = sendSms;
        this.comment = comment;
        this.smsRemainHours = smsRemainHours;
        this.emailRemainHours = emailRemainHours;
        this.attendance = attendance;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public Boolean getSaveIfBusy() {
        return saveIfBusy;
    }

    public void setSaveIfBusy(Boolean saveIfBusy) {
        this.saveIfBusy = saveIfBusy;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getSeanceLength() {
        return seanceLength;
    }

    public void setSeanceLength(Integer seanceLength) {
        this.seanceLength = seanceLength;
    }

    public Boolean getSendSms() {
        return sendSms;
    }

    public void setSendSms(Boolean sendSms) {
        this.sendSms = sendSms;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getSmsRemainHours() {
        return smsRemainHours;
    }

    public void setSmsRemainHours(Integer smsRemainHours) {
        this.smsRemainHours = smsRemainHours;
    }

    public Integer getEmailRemainHours() {
        return emailRemainHours;
    }

    public void setEmailRemainHours(Integer emailRemainHours) {
        this.emailRemainHours = emailRemainHours;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }
}
