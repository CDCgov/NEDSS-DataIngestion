package gov.cdc.dataprocessing.model.dto.locator;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
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
