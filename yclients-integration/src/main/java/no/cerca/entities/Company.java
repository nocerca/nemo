package no.cerca.entities;

import jakarta.persistence.*;

/**
 * Created by jadae on 12.03.2025
 */
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_inner_id")
    private Long companyInnerId;

    @Column(name = "id")
    private Long companyExternalId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="phone", nullable = false)
    private String phone;

    @Column(name="country", nullable = false)
    private String country;

    @Column(name = "site")
    private String site;

    @Column(name = "allow_delete_record", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean allowDeleteRecord;

    @Column(name = "allow_change_record", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean allowChangeRecord;

    public Company() {
    }

    public Company(Long id, String title, String phone, String country, String site, boolean allowDeleteRecord, boolean allowChangeRecord) {
        this.companyExternalId = id;
        this.title = title;
        this.phone = phone;
        this.country = country;
        this.site = site;
        this.allowDeleteRecord = allowDeleteRecord;
        this.allowChangeRecord = allowChangeRecord;
    }

    public void setCompanyInnerId(Long companyInnerId) {
        this.companyInnerId = companyInnerId;
    }

    public Long getCompanyInnerId() {
        return companyInnerId;
    }

    public Long getCompanyExternalId() {
        return companyExternalId;
    }

    public void setCompanyExternalId(Long id) {
        this.companyExternalId = id;
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
