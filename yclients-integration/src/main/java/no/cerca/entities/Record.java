package no.cerca.entities;

import jakarta.persistence.*;
import no.cerca.dtos.basic.RecordDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jadae on 05.03.2025
 */
@Entity
@Table(name = "record")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_inner_id")
    private Long recordInnerId;

    @Column(name = "id", nullable = false, unique = true)
    private Long recordExternalId;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "id", nullable = false)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "record_service",
            joinColumns = @JoinColumn(name = "record_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id")
    )
    private Set<Service> services = new HashSet<>();

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "datetime", nullable = false)
    private Instant datetime;

    @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Column(name = "length", nullable = false)
    private int length;

    @Column(name = "comment")
    private String comment;

    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;

    @Column(name = "notify_by_sms",nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notifyBySms;

    @Column(name = "notify_by_email", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notifyByEmail;

    @Column(name = "updated")
    private Instant updated;

    public Record() {
        this.updated = Instant.now();
    }

    public Record(Long id, Company company, Staff staff, Client client, Set<Service> services, LocalDateTime date, Instant datetime, Instant createDate, int length, String comment, boolean deleted, boolean notifyBySms, boolean notifyByEmail) {
        this.recordExternalId = id;
        this.company = company;
        this.staff = staff;
        this.client = client;
        this.services = services;
        this.date = date;
        this.datetime = datetime;
        this.createDate = createDate;
        this.length = length;
        this.comment = comment;
        this.deleted = deleted;
        this.notifyBySms = notifyBySms;
        this.notifyByEmail = notifyByEmail;
        this.updated = Instant.now();
    }

    public Record(RecordDTO dto, Company company, Staff staff, Client client, Set<Service> services) {
        this.recordExternalId = dto.getId();
        this.company = company;
        this.staff = staff;
        this.client = client;
        this.services = services;
        this.date = LocalDate.parse(dto.getDate()).atStartOfDay();
        this.datetime = dto.getDatetime().toInstant();
        this.createDate = dto.getCreateDate().toInstant();
        this.comment = dto.getComment();
        this.length = dto.getLength();
        this.deleted = dto.isDeleted();
        this.updated = Instant.now();
    }

    public Long getRecordExternalId() {
        return recordExternalId;
    }

    public void setRecordExternalId(Long id) {
        this.recordExternalId = id;
    }

    public Long getRecordInnerId() {
        return recordInnerId;
    }

    public void setRecordInnerId(Long recordInnerId) {
        this.recordInnerId = recordInnerId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Instant getDatetime() {
        return datetime;
    }

    public void setDatetime(Instant datetime) {
        this.datetime = datetime;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNotifyBySms() {
        return notifyBySms;
    }

    public void setNotifyBySms(boolean notifyBySms) {
        this.notifyBySms = notifyBySms;
    }

    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }

    public Instant getUpdated() {
        return updated;
    }
}
