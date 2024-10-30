package gov.cdc.dataprocessing.model.dto.locator;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class PhysicalLocatorDto extends BaseContainer
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


    public PhysicalLocatorDto(PhysicalLocator physicalLocator) {
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

    public PhysicalLocatorDto() {
        // Default constructor
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

}
