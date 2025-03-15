package no.cerca.dtos.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by jadae on 12.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordDTO {
    private Long id;
    @JsonProperty("company_id")
    private Long companyId;
    @JsonProperty("staff_id")
    private Long staffId;
    private List<ServiceDTO> services;
    private StaffDTO staff;
    private ClientDTO client;
    private String date;
    private OffsetDateTime datetime;
    @JsonProperty("create_date")
    private OffsetDateTime createDate;
    private String comment;
    @JsonProperty("seance_length")
    private int seanceLength;
    private int length;
    private boolean deleted;

    public RecordDTO() {
    }

    public RecordDTO(Long id, Long companyId, Long staffId, List<ServiceDTO> services, StaffDTO staff, ClientDTO client, String date, OffsetDateTime datetime, OffsetDateTime createDate, String comment, int seanceLength, int length, boolean deleted) {
        this.id = id;
        this.companyId = companyId;
        this.staffId = staffId;
        this.services = services;
        this.staff = staff;
        this.client = client;
        this.date = date;
        this.datetime = datetime;
        this.createDate = createDate;
        this.comment = comment;
        this.seanceLength = seanceLength;
        this.length = length;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public StaffDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(OffsetDateTime datetime) {
        this.datetime = datetime;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSeanceLength() {
        return seanceLength;
    }

    public void setSeanceLength(int seanceLength) {
        this.seanceLength = seanceLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
