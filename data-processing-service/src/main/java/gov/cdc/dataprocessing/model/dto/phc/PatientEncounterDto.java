package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PatientEncounter;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class PatientEncounterDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long patientEncounterUid;
    private String activityDurationAmt;
    private String activityDurationUnitCd;
    private Timestamp activityFromTime;
    private Timestamp activityToTime;
    private String acuityLevelCd;
    private String acuityLevelDescTxt;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String admissionSourceCd;
    private String admissionSourceDescTxt;
    private String birthEncounterInd;
    private String cd;
    private String cdDescTxt;
    private String confidentialityCd;
    private String confidentialityDescTxt;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String priorityCd;
    private String priorityDescTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String referralSourceCd;
    private String referralSourceDescTxt;
    private Integer repeatNbr;
    private String statusCd;
    private Timestamp statusTime;
    private String txt;
    private String userAffiliationTxt;
    private Long programJurisdictionOid;
    private String sharedInd;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public PatientEncounterDto() {

    }

    public PatientEncounterDto(PatientEncounter entity) {
        this.patientEncounterUid = entity.getPatientEncounterUid();
        this.activityDurationAmt = entity.getActivityDurationAmt();
        this.activityDurationUnitCd = entity.getActivityDurationUnitCd();
        this.activityFromTime = entity.getActivityFromTime();
        this.activityToTime = entity.getActivityToTime();
        this.acuityLevelCd = entity.getAcuityLevelCd();
        this.acuityLevelDescTxt = entity.getAcuityLevelDescTxt();
        this.addReasonCd = entity.getAddReasonCd();
        this.addTime = entity.getAddTime();
        this.addUserId = entity.getAddUserId();
        this.admissionSourceCd = entity.getAdmissionSourceCd();
        this.admissionSourceDescTxt = entity.getAdmissionSourceDescTxt();
        this.birthEncounterInd = entity.getBirthEncounterInd();
        this.cd = entity.getCd();
        this.cdDescTxt = entity.getCdDescTxt();
        this.confidentialityCd = entity.getConfidentialityCd();
        this.confidentialityDescTxt = entity.getConfidentialityDescTxt();
        this.effectiveDurationAmt = entity.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = entity.getEffectiveDurationUnitCd();
        this.effectiveFromTime = entity.getEffectiveFromTime();
        this.effectiveToTime = entity.getEffectiveToTime();
        this.lastChgReasonCd = entity.getLastChgReasonCd();
        this.lastChgTime = entity.getLastChgTime();
        this.lastChgUserId = entity.getLastChgUserId();
        this.localId = entity.getLocalId();
        this.priorityCd = entity.getPriorityCd();
        this.priorityDescTxt = entity.getPriorityDescTxt();
        this.recordStatusCd = entity.getRecordStatusCd();
        this.recordStatusTime = entity.getRecordStatusTime();
        this.referralSourceCd = entity.getReferralSourceCd();
        this.referralSourceDescTxt = entity.getReferralSourceDescTxt();
        this.repeatNbr = entity.getRepeatNbr();
        this.statusCd = entity.getStatusCd();
        this.statusTime = entity.getStatusTime();
        this.txt = entity.getTxt();
        this.userAffiliationTxt = entity.getUserAffiliationTxt();
        this.programJurisdictionOid = entity.getProgramJurisdictionOid();
        this.sharedInd = entity.getSharedInd();
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
        jurisdictionCd = aJurisdictionCd;
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
        lastChgTime = aLastChgTime;
    }

    @Override
    public String getLocalId() {
        return localId;
    }

    @Override
    public void setLocalId(String aLocalId) {
        this.localId =aLocalId;
    }

    @Override
    public Long getAddUserId() {
        return addUserId;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        addUserId = aAddUserId;
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
        recordStatusCd = aRecordStatusCd;
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return recordStatusTime;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {
        recordStatusTime = aRecordStatusTime;
    }

    @Override
    public String getStatusCd() {
        return statusCd;
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        statusCd =aStatusCd;
    }

    @Override
    public Timestamp getStatusTime() {
        return statusTime;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        statusTime = aStatusTime;
    }

    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return patientEncounterUid;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        addTime = aAddTime;
    }

    @Override
    public Timestamp getAddTime() {
        return addTime;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return programJurisdictionOid;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        programJurisdictionOid = aProgramJurisdictionOid;
    }

    @Override
    public String getSharedInd() {
        return sharedInd;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        sharedInd = aSharedInd;
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return versionCtrlNbr;
    }
}
