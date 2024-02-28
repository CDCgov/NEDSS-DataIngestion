package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd", length = 50)
    private String code;

    @Column(name = "cd_desc_txt", length = 100)
    private String codeDescription;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "duration_amt", length = 20)
    private String durationAmount;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCode;

    @Column(name = "from_time")
    private Date fromTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChangeReasonCode;

    @Column(name = "last_chg_time")
    private Date lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "local_id", length = 50)
    private String localId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCode;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "standard_industry_class_cd", length = 20)
    private String standardIndustryClassCode;

    @Column(name = "standard_industry_desc_txt", length = 100)
    private String standardIndustryDescription;

    @Column(name = "status_cd", length = 1)
    private Character statusCode;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "to_time")
    private Date toTime;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

    @Column(name = "display_nm", length = 100)
    private String displayName;

    @Column(name = "street_addr1", length = 100)
    private String streetAddress1;

    @Column(name = "street_addr2", length = 100)
    private String streetAddress2;

    @Column(name = "city_cd", length = 20)
    private String cityCode;

    @Column(name = "city_desc_txt", length = 100)
    private String cityDescription;

    @Column(name = "state_cd", length = 20)
    private String stateCode;

    @Column(name = "cnty_cd", length = 20)
    private String countyCode;

    @Column(name = "cntry_cd", length = 20)
    private String countryCode;

    @Column(name = "zip_cd", length = 20)
    private String zipCode;

    @Column(name = "phone_nbr", length = 20)
    private String phoneNumber;

    @Column(name = "phone_cntry_cd", length = 20)
    private String phoneCountryCode;

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Short versionControlNumber;

    @Column(name = "electronic_ind", length = 1)
    private Character electronicIndicator;

    @Column(name = "edx_ind", length = 1)
    private String edxIndicator;


}
