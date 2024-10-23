package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationHistId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@IdClass(OrganizationHistId.class)
@Table(name = "Organization_hist")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class OrganizationHist {

    @Id
    @Column(name = "organization_uid", nullable = false)
    private Long organizationUid;

    @Id
    @Column(name = "version_ctrl_nbr", nullable = false)
    private int versionCtrlNbr;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd", length = 50)
    private String cd;

    @Column(name = "cd_desc_txt", length = 100)
    private String cdDescTxt;

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

    @Column(name = "status_cd")
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

    @Column(name = "electronic_ind")
    private String electronicInd;

    @Column(name = "edx_ind", length = 1)
    private String edxInd;
}