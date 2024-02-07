package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "Tele_locator", schema = "dbo")
@Data
public class TeleLocator {

    @Id
    @Column(name = "tele_locator_uid", nullable = false)
    private BigInteger teleLocatorUid;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private BigInteger addUserId;

    @Column(name = "cntry_cd", length = 20)
    private String cntryCd;

    @Column(name = "email_address", length = 100)
    private String emailAddress;

    @Column(name = "extension_txt", length = 20)
    private String extensionTxt;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Date lastChgTime;

    @Column(name = "last_chg_user_id")
    private BigInteger lastChgUserId;

    @Column(name = "phone_nbr_txt", length = 20)
    private String phoneNbrTxt;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "url_address", length = 100)
    private String urlAddress;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    // Add getters and setters as needed
}
