package no.cerca.entities;

import jakarta.persistence.*;

/**
 * Created by jadae on 12.03.2025
 */
@Entity
@Table(name = "company")
public class Company {

    @Id
    private Long companyInnerId;

    private Long id;

    @Column(nullable = false)
    private String title;

    private String phone;
    private String country;
    private String site;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean allowDeleteRecord;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean allowChangeRecord;

    public Company() {
    }

    public Company(Long companyInnerId, Long id, String title, String phone, String country, String site, boolean allowDeleteRecord, boolean allowChangeRecord) {
        this.companyInnerId = companyInnerId;
        this.id = id;
        this.title = title;
        this.phone = phone;
        this.country = country;
        this.site = site;
        this.allowDeleteRecord = allowDeleteRecord;
        this.allowChangeRecord = allowChangeRecord;
    }

    public Long getCompanyInnerId() {
        return companyInnerId;
    }

    public void setCompanyInnerId(Long companyInnerId) {
        this.companyInnerId = companyInnerId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public boolean isAllowDeleteRecord() {
        return allowDeleteRecord;
    }

    public void setAllowDeleteRecord(boolean allowDeleteRecord) {
        this.allowDeleteRecord = allowDeleteRecord;
    }

    public boolean isAllowChangeRecord() {
        return allowChangeRecord;
    }

    public void setAllowChangeRecord(boolean allowChangeRecord) {
        this.allowChangeRecord = allowChangeRecord;
    }
}
