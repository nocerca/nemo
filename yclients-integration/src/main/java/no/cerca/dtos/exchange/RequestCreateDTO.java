package no.cerca.dtos.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import no.cerca.dtos.basic.ClientDTO;
import no.cerca.dtos.basic.ServiceDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jadae on 13.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestCreateDTO {
    @JsonProperty("staff_id")
    @NotNull
    private Long staffId;
    private List<ServiceDTO> services;
    @NotNull
    private ClientDTO client;

    @JsonProperty("save_if_busy")
    private boolean saveIfBusy;
    @NotNull
    private String datetime;

    @JsonProperty("seance_length")
    private Integer seanceLength;

    @JsonProperty("send_sms")
    private boolean sendSms;
    private String comment;

    @JsonProperty("sms_remain_hours")
    private Integer smsRemainHours;

    @JsonProperty("email_remain_hours")
    private Integer emailRemainHours;
    private Integer attendance;

    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("custom_fields")
    private Map<String, Object> customFields;

    public RequestCreateDTO() {
    }

    public RequestCreateDTO(Long staffId, List<ServiceDTO> services, ClientDTO client, boolean saveIfBusy, String datetime, Integer seanceLength, boolean sendSms, String comment, Integer smsRemainHours, Integer emailRemainHours, Integer attendance, String apiId, Map<String, Object> customFields) {
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
        this.apiId = apiId;
        this.customFields = customFields;
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

    public boolean isSaveIfBusy() {
        return saveIfBusy;
    }

    public void setSaveIfBusy(boolean saveIfBusy) {
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

    public boolean isSendSms() {
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
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

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestCreateDTO that)) return false;
        return isSaveIfBusy() == that.isSaveIfBusy() && isSendSms() == that.isSendSms() && Objects.equals(getStaffId(), that.getStaffId()) && Objects.equals(getClient(), that.getClient()) && Objects.equals(getDatetime(), that.getDatetime()) && Objects.equals(getSeanceLength(), that.getSeanceLength()) && Objects.equals(getComment(), that.getComment()) && Objects.equals(getSmsRemainHours(), that.getSmsRemainHours()) && Objects.equals(getEmailRemainHours(), that.getEmailRemainHours()) && Objects.equals(getAttendance(), that.getAttendance()) && Objects.equals(getApiId(), that.getApiId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStaffId(), getClient(), isSaveIfBusy(), getDatetime(), getSeanceLength(), isSendSms(), getComment(), getSmsRemainHours(), getEmailRemainHours(), getAttendance(), getApiId());
    }
}