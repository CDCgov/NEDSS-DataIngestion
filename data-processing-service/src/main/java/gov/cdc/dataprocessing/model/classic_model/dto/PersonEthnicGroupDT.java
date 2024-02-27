package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PersonEthnicGroupDT extends AbstractVO {

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
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public PersonEthnicGroupDT() {

    }

    public PersonEthnicGroupDT(PersonEthnicGroup personEthnicGroup) {
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
