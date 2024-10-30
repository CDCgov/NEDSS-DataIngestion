package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
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
    public Organization(OrganizationDto organizationDto) {
        this.organizationUid = organizationDto.getOrganizationUid();
        this.addReasonCode = organizationDto.getAddReasonCd();
        this.addTime = organizationDto.getAddTime();
        this.addUserId = organizationDto.getAddUserId();
        this.code = organizationDto.getCd();
        this.codeDescTxt = organizationDto.getCdDescTxt();
        this.description = organizationDto.getDescription();
        this.durationAmt = organizationDto.getDurationAmt();
        this.durationUnitCd = organizationDto.getDurationUnitCd();
        this.fromTime = organizationDto.getFromTime();
        this.lastChgReasonCd = organizationDto.getLastChgReasonCd();
        this.lastChgTime = organizationDto.getLastChgTime();
        this.lastChgUserId = organizationDto.getLastChgUserId();
        this.localId = organizationDto.getLocalId();
        this.recordStatusCd = organizationDto.getRecordStatusCd();
        this.recordStatusTime = organizationDto.getRecordStatusTime();
        this.standardIndustryClassCd = organizationDto.getStandardIndustryClassCd();
        this.standardIndustryDescTxt = organizationDto.getStandardIndustryDescTxt();
        this.statusCd = organizationDto.getStatusCd();
        this.statusTime = organizationDto.getStatusTime();
        this.toTime = organizationDto.getToTime();
        this.userAffiliationTxt = organizationDto.getUserAffiliationTxt();
        this.displayNm = organizationDto.getDisplayNm();
        this.streetAddr1 = organizationDto.getStreetAddr1();
        this.streetAddr2 = organizationDto.getStreetAddr2();
        this.cityCd = organizationDto.getCityCd();
        this.cityDescTxt = organizationDto.getCityDescTxt();
        this.stateCd = organizationDto.getStateCd();
        this.cntyCd = organizationDto.getCntyCd();
        this.cntryCd = organizationDto.getCntryCd();
        this.zipCd = organizationDto.getZipCd();
        this.phoneNbr = organizationDto.getPhoneNbr();
        this.phoneCntryCd = organizationDto.getPhoneCntryCd();
        this.versionCtrlNbr = organizationDto.getVersionCtrlNbr();
        this.electronicInd = organizationDto.getElectronicInd();
        this.edxInd = organizationDto.getEdxInd();
    }
}