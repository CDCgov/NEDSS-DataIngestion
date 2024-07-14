package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Referral;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class ReferralDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long referralUid;
    private String activityDurationAmt;
    private String activityDurationUnitCd;
    private Timestamp activityFromTime;
    private Timestamp activityToTime;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
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
    private String reasonTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String referralDescTxt;
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

    public ReferralDto() {

    }

    public ReferralDto(Referral referral) {
        this.referralUid = referral.getReferralUid();
        this.activityDurationAmt = referral.getActivityDurationAmt();
        this.activityDurationUnitCd = referral.getActivityDurationUnitCd();
        this.activityFromTime = referral.getActivityFromTime();
        this.activityToTime = referral.getActivityToTime();
        this.addReasonCd = referral.getAddReasonCd();
        this.addTime = referral.getAddTime();
        this.addUserId = referral.getAddUserId();
        this.cd = referral.getCd();
        this.cdDescTxt = referral.getCdDescTxt();
        this.confidentialityCd = referral.getConfidentialityCd();
        this.confidentialityDescTxt = referral.getConfidentialityDescTxt();
        this.effectiveDurationAmt = referral.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = referral.getEffectiveDurationUnitCd();
        this.effectiveFromTime = referral.getEffectiveFromTime();
        this.effectiveToTime = referral.getEffectiveToTime();
        this.lastChgReasonCd = referral.getLastChgReasonCd();
        this.lastChgTime = referral.getLastChgTime();
        this.lastChgUserId = referral.getLastChgUserId();
        this.localId = referral.getLocalId();
        this.reasonTxt = referral.getReasonTxt();
        this.recordStatusCd = referral.getRecordStatusCd();
        this.recordStatusTime = referral.getRecordStatusTime();
        this.referralDescTxt = referral.getReferralDescTxt();
        this.repeatNbr = referral.getRepeatNbr();
        this.statusCd = referral.getStatusCd();
        this.statusTime = referral.getStatusTime();
        this.txt = referral.getTxt();
        this.userAffiliationTxt = referral.getUserAffiliationTxt();
        this.programJurisdictionOid = referral.getProgramJurisdictionOid();
        this.sharedInd = referral.getSharedInd();
        this.versionCtrlNbr = referral.getVersionCtrlNbr();
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
        return superClassType;
    }

    @Override
    public Long getUid() {
        return referralUid;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        this.addTime = aAddTime;
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
