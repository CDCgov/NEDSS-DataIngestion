package gov.cdc.dataprocessing.model.dto.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter

public class PersonRaceDto extends BaseContainer implements RootDtoInterface {

    private Long personUid;
    private String raceCd;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp asOfDate;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String raceCategoryCd;
    private String raceDescTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String userAffiliationTxt;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    private Integer versionCtrlNbr;
    private Timestamp statusTime;
    private String statusCd;
    private String localId;

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return personUid;
    }

    public PersonRaceDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public PersonRaceDto(PersonRace personRace) {
        this.personUid = personRace.getPersonUid();
        this.raceCd = personRace.getRaceCd();
        this.addReasonCd = personRace.getAddReasonCd();
        this.addTime = personRace.getAddTime();
        this.addUserId = personRace.getAddUserId();
        this.lastChgReasonCd = personRace.getLastChgReasonCd();
        this.lastChgTime = personRace.getLastChgTime();
        this.lastChgUserId = personRace.getLastChgUserId();
        this.raceCategoryCd = personRace.getRaceCategoryCd();
        this.raceDescTxt = personRace.getRaceDescTxt();
        this.recordStatusCd = personRace.getRecordStatusCd();
        this.recordStatusTime = personRace.getRecordStatusTime();
        this.userAffiliationTxt = personRace.getUserAffiliationTxt();
        this.asOfDate = personRace.getAsOfDate();
    }




}
