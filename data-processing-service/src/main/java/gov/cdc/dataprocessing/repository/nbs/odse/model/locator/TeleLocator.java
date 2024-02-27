package gov.cdc.dataprocessing.repository.nbs.odse.model.locator;

import gov.cdc.dataprocessing.model.classic_model.dto.TeleLocatorDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "Tele_locator", schema = "dbo")
@Data
public class TeleLocator {

    @Id
    @Column(name = "tele_locator_uid", nullable = false)
    private Long teleLocatorUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cntry_cd")
    private String cntryCd;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "extension_txt")
    private String extensionTxt;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "phone_nbr_txt")
    private String phoneNbrTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "url_address")
    private String urlAddress;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    // Add getters and setters as needed
    public TeleLocator(TeleLocatorDT teleLocatorDT) {
        this.teleLocatorUid = teleLocatorDT.getTeleLocatorUid();
        this.addReasonCd = teleLocatorDT.getAddReasonCd();
        this.addTime = teleLocatorDT.getAddTime();
        this.addUserId = teleLocatorDT.getAddUserId();
        this.cntryCd = teleLocatorDT.getCntryCd();
        this.emailAddress = teleLocatorDT.getEmailAddress();
        this.extensionTxt = teleLocatorDT.getExtensionTxt();
        this.lastChgReasonCd = teleLocatorDT.getLastChgReasonCd();
        this.lastChgTime = teleLocatorDT.getLastChgTime();
        this.lastChgUserId = teleLocatorDT.getLastChgUserId();
        this.phoneNbrTxt = teleLocatorDT.getPhoneNbrTxt();
        this.recordStatusCd = teleLocatorDT.getRecordStatusCd();
        this.recordStatusTime = teleLocatorDT.getRecordStatusTime();
        this.urlAddress = teleLocatorDT.getUrlAddress();
        this.userAffiliationTxt = teleLocatorDT.getUserAffiliationTxt();
    }

    public TeleLocator() {
        // Default constructor
    }

}
