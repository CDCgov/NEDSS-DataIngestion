package gov.cdc.dataprocessing.model.dto.person;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class PersonEthnicGroupDto extends BaseContainer {

    private Long personUid;
    private String ethnicGroupCd;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String ethnicGroupDescTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String userAffiliationTxt;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;


    public PersonEthnicGroupDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public PersonEthnicGroupDto(PersonEthnicGroup personEthnicGroup) {
        this.personUid = personEthnicGroup.getPersonUid();
        this.ethnicGroupCd = personEthnicGroup.getEthnicGroupCd();
        this.addReasonCd = personEthnicGroup.getAddReasonCd();
        this.addTime = personEthnicGroup.getAddTime();
        this.addUserId = personEthnicGroup.getAddUserId();
        this.ethnicGroupDescTxt = personEthnicGroup.getEthnicGroupDescTxt();
        this.lastChgReasonCd = personEthnicGroup.getLastChgReasonCd();
        this.lastChgTime = personEthnicGroup.getLastChgTime();
        this.lastChgUserId = personEthnicGroup.getLastChgUserId();
        this.recordStatusCd = personEthnicGroup.getRecordStatusCd();
        this.recordStatusTime = personEthnicGroup.getRecordStatusTime();
        this.userAffiliationTxt = personEthnicGroup.getUserAffiliationTxt();
    }

}
