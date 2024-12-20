package gov.cdc.dataprocessing.repository.nbs.odse.model.locator;

import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Tele_locator", schema = "dbo")
@Data
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
    public TeleLocator(TeleLocatorDto teleLocatorDto) {
        this.teleLocatorUid = teleLocatorDto.getTeleLocatorUid();
        if (teleLocatorDto.getAddReasonCd() == null) {
            this.addReasonCd = "Add";
        } else {
            this.addReasonCd = teleLocatorDto.getAddReasonCd();
        }
        this.addTime = teleLocatorDto.getAddTime();
        this.addUserId = teleLocatorDto.getAddUserId();
        this.cntryCd = teleLocatorDto.getCntryCd();
        this.emailAddress = teleLocatorDto.getEmailAddress();
        this.extensionTxt = teleLocatorDto.getExtensionTxt();
        this.lastChgReasonCd = teleLocatorDto.getLastChgReasonCd();
        this.lastChgTime = teleLocatorDto.getLastChgTime();
        this.lastChgUserId = teleLocatorDto.getLastChgUserId();
        this.phoneNbrTxt = teleLocatorDto.getPhoneNbrTxt();
        this.recordStatusCd = teleLocatorDto.getRecordStatusCd();
        this.recordStatusTime = teleLocatorDto.getRecordStatusTime();
        this.urlAddress = teleLocatorDto.getUrlAddress();
        this.userAffiliationTxt = teleLocatorDto.getUserAffiliationTxt();
    }

    public TeleLocator() {
        // Default constructor
    }

}
