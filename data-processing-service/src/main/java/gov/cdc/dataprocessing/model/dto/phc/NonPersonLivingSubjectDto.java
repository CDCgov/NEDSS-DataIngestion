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
        return null;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        // No operation needed for setLastChgUserId()
    }

    @Override
    public String getJurisdictionCd() {
        return null;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {
        // No operation needed for setJurisdictionCd()
    }

    @Override
    public String getProgAreaCd() {
        return null;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {
        // No operation needed for setProgAreaCd()
    }

    @Override
    public Timestamp getLastChgTime() {
        return null;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {
        // No operation needed for setLastChgTime()
    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public void setLocalId(String aLocalId) {
        // No operation needed for setLocalId()
    }

    @Override
    public Long getAddUserId() {
        return null;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        // No operation needed for setAddUserId()
    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        // No operation needed for setLastChgReasonCd()
    }

    @Override
    public String getRecordStatusCd() {
        return null;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        // No operation needed for setRecordStatusCd()
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return null;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {
        // No operation needed for setRecordStatusTime()
    }

    @Override
    public String getStatusCd() {
        return null;
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        // No operation needed for setStatusCd()
    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        // No operation needed for setStatusTime()
    }

    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return null;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        // No operation needed for setAddTime()
    }

    @Override
    public Timestamp getAddTime() {
        return null;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return null;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        // No operation needed for setProgramJurisdictionOid()
    }

    @Override
    public String getSharedInd() {
        return null;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        // No operation needed for setSharedInd()
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return null;
    }
}
