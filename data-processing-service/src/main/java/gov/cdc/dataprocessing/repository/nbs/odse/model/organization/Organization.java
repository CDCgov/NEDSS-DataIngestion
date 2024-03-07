package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "Organization")
public class Organization {

    @Id
    @Column(name = "organization_uid")
    private Long organizationUid;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd", length = 50)
    private String code;

    @Column(name = "cd_desc_txt", length = 100)
    private String codeDescTxt;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "duration_amt", length = 20)
    private String durationAmt;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id", length = 50)
    private String localId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "standard_industry_class_cd", length = 20)
    private String standardIndustryClassCd;

    @Column(name = "standard_industry_desc_txt", length = 100)
    private String standardIndustryDescTxt;

    @Column(name = "status_cd", length = 1)
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    @Column(name = "display_nm", length = 100)
    private String displayNm;

    @Column(name = "street_addr1", length = 100)
    private String streetAddr1;

    @Column(name = "street_addr2", length = 100)
    private String streetAddr2;

    @Column(name = "city_cd", length = 20)
    private String cityCd;

    @Column(name = "city_desc_txt", length = 100)
    private String cityDescTxt;

    @Column(name = "state_cd", length = 20)
    private String stateCd;

    @Column(name = "cnty_cd", length = 20)
    private String cntyCd;

    @Column(name = "cntry_cd", length = 20)
    private String cntryCd;

    @Column(name = "zip_cd", length = 20)
    private String zipCd;

    @Column(name = "phone_nbr", length = 20)
    private String phoneNbr;

    @Column(name = "phone_cntry_cd", length = 20)
    private String phoneCntryCd;

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

    @Column(name = "electronic_ind", length = 1)
    private String electronicInd;

    @Column(name = "edx_ind", length = 1)
    private String edxInd;

    public Organization() {
    }
    public Organization(OrganizationDT organizationDT) {
        this.organizationUid = organizationDT.getOrganizationUid();
        this.addReasonCode = organizationDT.getAddReasonCd();
        this.addTime = organizationDT.getAddTime();
        this.addUserId = organizationDT.getAddUserId();
        this.code = organizationDT.getCd();
        this.codeDescTxt = organizationDT.getCdDescTxt();
        this.description = organizationDT.getDescription();
        this.durationAmt = organizationDT.getDurationAmt();
        this.durationUnitCd = organizationDT.getDurationUnitCd();
        this.fromTime = organizationDT.getFromTime();
        this.lastChgReasonCd = organizationDT.getLastChgReasonCd();
        this.lastChgTime = organizationDT.getLastChgTime();
        this.lastChgUserId = organizationDT.getLastChgUserId();
        this.localId = organizationDT.getLocalId();
        this.recordStatusCd = organizationDT.getRecordStatusCd();
        this.recordStatusTime = organizationDT.getRecordStatusTime();
        this.standardIndustryClassCd = organizationDT.getStandardIndustryClassCd();
        this.standardIndustryDescTxt = organizationDT.getStandardIndustryDescTxt();
        this.statusCd = organizationDT.getStatusCd();
        this.statusTime = organizationDT.getStatusTime();
        this.toTime = organizationDT.getToTime();
        this.userAffiliationTxt = organizationDT.getUserAffiliationTxt();
        this.displayNm = organizationDT.getDisplayNm();
        this.streetAddr1 = organizationDT.getStreetAddr1();
        this.streetAddr2 = organizationDT.getStreetAddr2();
        this.cityCd = organizationDT.getCityCd();
        this.cityDescTxt = organizationDT.getCityDescTxt();
        this.stateCd = organizationDT.getStateCd();
        this.cntyCd = organizationDT.getCntyCd();
        this.cntryCd = organizationDT.getCntryCd();
        this.zipCd = organizationDT.getZipCd();
        this.phoneNbr = organizationDT.getPhoneNbr();
        this.phoneCntryCd = organizationDT.getPhoneCntryCd();
        this.versionCtrlNbr = organizationDT.getVersionCtrlNbr();
        this.electronicInd = organizationDT.getElectronicInd();
        this.edxInd = organizationDT.getEdxInd();
    }
}