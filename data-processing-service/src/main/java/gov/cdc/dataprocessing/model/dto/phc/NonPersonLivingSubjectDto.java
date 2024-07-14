package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.NonPersonLivingSubject;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class NonPersonLivingSubjectDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long nonPersonUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String birthSexCd;
    private Integer birthOrderNbr;
    private Timestamp birthTime;
    private String breedCd;
    private String breedDescTxt;
    private String cd;
    private String cdDescTxt;
    private String deceasedIndCd;
    private Timestamp deceasedTime;
    private String description;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String multipleBirthInd;
    private String nm;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private String taxonomicClassificationCd;
    private String taxonomicClassificationDesc;
    private String userAffiliationTxt;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public NonPersonLivingSubjectDto() {

    }

    public NonPersonLivingSubjectDto(NonPersonLivingSubject entity) {
        this.nonPersonUid = entity.getNonPersonUid();
        this.addReasonCd = entity.getAddReasonCd();
        this.addTime = entity.getAddTime();
        this.addUserId = entity.getAddUserId();
        this.birthSexCd = entity.getBirthSexCd();
        this.birthOrderNbr = entity.getBirthOrderNbr();
        this.birthTime = entity.getBirthTime();
        this.breedCd = entity.getBreedCd();
        this.breedDescTxt = entity.getBreedDescTxt();
        this.cd = entity.getCd();
        this.cdDescTxt = entity.getCdDescTxt();
        this.deceasedIndCd = entity.getDeceasedIndCd();
        this.deceasedTime = entity.getDeceasedTime();
        this.description = entity.getDescription();
        this.lastChgReasonCd = entity.getLastChgReasonCd();
        this.lastChgTime = entity.getLastChgTime();
        this.lastChgUserId = entity.getLastChgUserId();
        this.localId = entity.getLocalId();
        this.multipleBirthInd = entity.getMultipleBirthInd();
        this.nm = entity.getNm();
        this.recordStatusCd = entity.getRecordStatusCd();
        this.recordStatusTime = entity.getRecordStatusTime();
        this.statusCd = entity.getStatusCd();
        this.statusTime = entity.getStatusTime();
        this.taxonomicClassificationCd = entity.getTaxonomicClassificationCd();
        this.taxonomicClassificationDesc = entity.getTaxonomicClassificationDesc();
        this.userAffiliationTxt = entity.getUserAffiliationTxt();
        this.versionCtrlNbr = entity.getVersionCtrlNbr();
    }

    @Override
    public Long getLastChgUserId() {
        return lastChgUserId;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        this.lastChgUserId = aLastChgUserId;
    }

    @Override
    public String getJurisdictionCd() {
        return jurisdictionCd;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {
        this.jurisdictionCd = aJurisdictionCd;
    }

    @Override
    public String getProgAreaCd() {
        return progAreaCd;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {
        this.progAreaCd = aProgAreaCd;
    }

    @Override
    public Timestamp getLastChgTime() {
        return lastChgTime;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {
        this.lastChgTime = aLastChgTime;
    }

    @Override
    public String getLocalId() {
        return localId;
    }

    @Override
    public void setLocalId(String aLocalId) {
        this.localId = aLocalId;
    }

    @Override
    public Long getAddUserId() {
        return addUserId;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        this.addUserId = aAddUserId;
    }

    @Override
    public String getLastChgReasonCd() {
        return lastChgReasonCd;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        this.lastChgReasonCd = aLastChgReasonCd;
    }

    @Override
    public String getRecordStatusCd() {
        return recordStatusCd;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        this.recordStatusCd = aRecordStatusCd;
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return recordStatusTime;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {
        this.recordStatusTime = aRecordStatusTime;
    }

    @Override
    public String getStatusCd() {
        return statusCd;
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        this.statusCd = aStatusCd;
    }

    @Override
    public Timestamp getStatusTime() {
        return statusTime;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        this.statusTime = aStatusTime;
    }

    @Override
    public String getSuperclass() {
        return NonPersonLivingSubjectDto.class.getSuperclass().getSimpleName();
    }

    @Override
    public Long getUid() {
        return nonPersonUid;
    }

    @Override
    public Timestamp getAddTime() {
        return addTime;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        this.addTime = aAddTime;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return programJurisdictionOid;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        this.programJurisdictionOid = aProgramJurisdictionOid;
    }

    @Override
    public String getSharedInd() {
        return sharedInd;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        this.sharedInd = aSharedInd;
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return versionCtrlNbr;
    }
}
