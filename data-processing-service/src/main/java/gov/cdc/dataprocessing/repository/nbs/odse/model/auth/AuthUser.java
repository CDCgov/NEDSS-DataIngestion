package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;



@Data
@Entity
@Table(name = "Auth_user")

public class AuthUser {

    @Id
    @Column(name = "auth_user_uid")
    private Long authUserUid;

    @Column(name = "user_id", length = 256)
    private String userId;

    @Column(name = "user_type", length = 100)
    private String userType;

    @Column(name = "user_title", length = 100)
    private String userTitle;

    @Column(name = "user_department", length = 100)
    private String userDepartment;

    @Column(name = "user_first_nm", length = 100)
    private String userFirstNm;

    @Column(name = "user_last_nm", length = 100)
    private String userLastNm;

    @Column(name = "user_work_email", length = 100)
    private String userWorkEmail;

    @Column(name = "user_work_phone", length = 100)
    private String userWorkPhone;

    @Column(name = "user_mobile_phone", length = 100)
    private String userMobilePhone;

    @Column(name = "master_sec_admin_ind", length = 1)
    private String masterSecAdminInd;

    @Column(name = "prog_area_admin_ind", length = 1)
    private String progAreaAdminInd;

    @Column(name = "nedss_entry_id", nullable = false)
    private Long nedssEntryId;

    @Column(name = "external_org_uid")
    private Long externalOrgUid;

    @Column(name = "user_password", length = 100)
    private String userPassword;

    @Column(name = "user_comments", length = 100)
    private String userComments;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "add_user_id", nullable = false)
    private Long addUserId;

    @Column(name = "last_chg_time", nullable = false)
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id", nullable = false)
    private Long lastChgUserId;

    @Column(name = "record_status_cd", nullable = false, length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    private Timestamp recordStatusTime;

    @Column(name = "jurisdiction_derivation_ind", length = 1)
    private String jurisdictionDerivationInd;

    @Column(name = "provider_uid")
    private Long providerUid;

}
