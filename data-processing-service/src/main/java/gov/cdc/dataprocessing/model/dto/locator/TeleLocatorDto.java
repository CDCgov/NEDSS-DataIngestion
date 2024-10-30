package gov.cdc.dataprocessing.model.dto.locator;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
public class TeleLocatorDto extends BaseContainer {
    private Long teleLocatorUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cntryCd;
    private String emailAddress;
    private String extensionTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String phoneNbrTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String urlAddress;
    private String userAffiliationTxt;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    public TeleLocatorDto(TeleLocator teleLocator) {
        this.teleLocatorUid = teleLocator.getTeleLocatorUid();
        this.addReasonCd = teleLocator.getAddReasonCd();
        this.addTime = teleLocator.getAddTime();
        this.addUserId = teleLocator.getAddUserId();
        this.cntryCd = teleLocator.getCntryCd();
        this.emailAddress = teleLocator.getEmailAddress();
        this.extensionTxt = teleLocator.getExtensionTxt();
        this.lastChgReasonCd = teleLocator.getLastChgReasonCd();
        this.lastChgTime = teleLocator.getLastChgTime();
        this.lastChgUserId = teleLocator.getLastChgUserId();
        this.phoneNbrTxt = teleLocator.getPhoneNbrTxt();
        this.recordStatusCd = teleLocator.getRecordStatusCd();
        this.recordStatusTime = teleLocator.getRecordStatusTime();
        this.urlAddress = teleLocator.getUrlAddress();
        this.userAffiliationTxt = teleLocator.getUserAffiliationTxt();
    }

    public TeleLocatorDto() {
        // Default constructor
        itDirty = false;
        itNew = false;
        itDelete = false;
    }

}
