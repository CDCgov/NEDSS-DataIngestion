package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PhysicalLocatorDT extends AbstractVO
{
    private Long physicalLocatorUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private byte[] imageTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String locatorTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String userAffiliationTxt;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public PhysicalLocatorDT(PhysicalLocator physicalLocator) {
        this.physicalLocatorUid = physicalLocator.getPhysicalLocatorUid();
        this.addReasonCd = physicalLocator.getAddReasonCd();
        this.addTime = physicalLocator.getAddTime();
        this.addUserId = physicalLocator.getAddUserId();
        this.imageTxt = physicalLocator.getImageTxt().getBytes();
        this.lastChgReasonCd = physicalLocator.getLastChgReasonCd();
        this.lastChgTime = physicalLocator.getLastChgTime();
        this.lastChgUserId = physicalLocator.getLastChgUserId();
        this.locatorTxt = physicalLocator.getLocatorTxt();
        this.recordStatusCd = physicalLocator.getRecordStatusCd();
        this.recordStatusTime = physicalLocator.getRecordStatusTime();
        this.userAffiliationTxt = physicalLocator.getUserAffiliationTxt();
    }

    public PhysicalLocatorDT() {
        // Default constructor
    }

}
