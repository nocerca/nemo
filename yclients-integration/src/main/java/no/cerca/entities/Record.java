package no.cerca.entities;

import jakarta.persistence.*;
import java.time.Instant;
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
    private Long id;

    @Column(nullable = false, unique = true)
    private Long recordInnerId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "record_service",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Instant datetime;

    @Column(nullable = false)
    private Instant createDate;

    @Column(nullable = false)
    private int length;

    private String comment;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notifyBySms;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notifyByEmail;

    public Record() {
    }

    public Record(Long id, Long recordInnerId, Company company, Staff staff, Client client, Set<Service> services, LocalDateTime date, Instant datetime, Instant createDate, int length, String comment, boolean deleted, boolean notifyBySms, boolean notifyByEmail) {
        this.id = id;
        this.recordInnerId = recordInnerId;
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
