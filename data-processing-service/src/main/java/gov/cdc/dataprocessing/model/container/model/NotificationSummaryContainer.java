package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class NotificationSummaryContainer extends BaseContainer implements RootDtoInterface {

    public String isHistory;
    private Long notificationUid;
    private Timestamp addTime;
    private Timestamp rptSentTime;
    private Timestamp recordStatusTime;
    private String cd;
    private String caseClassCd;
    private String localId;
    private String txt;
    private Timestamp lastChgTime;
    private String addUserName;
    private Long addUserId;
    private String jurisdictionCd;
    private Long publicHealthCaseUid;
    private String cdTxt;
    private String jurisdictionCdTxt;
    private String publicHealthCaseLocalId;
    private String caseClassCdTxt;
    private String recordStatusCd;
    private String lastNm;
    private String firstNm;
    private String currSexCd;
    private Timestamp birthTimeCalc;
    private String autoResendInd;
    private String progAreaCd;
    private String sharedInd;
    private String currSexCdDesc;
    private Long MPRUid;
    private String cdNotif;
    private boolean nndAssociated;
    private boolean isCaseReport;
    private Long programJurisdictionOid;
    private boolean shareAssocaited;
    private String patientFullName;
    private String patientFullNameLnk;
    private String conditionCodeTextLnk;
    private String approveLink;
    private String rejectLink;
    private String notificationCd;
    private String notificationSrtDescCd;
    private String recipient;
    private String exportRecFacilityUid;
    private String codeConverterTemp;
    private String codeConverterCommentTemp;
    private boolean isPendingNotification;
    private String nndInd;

    @Override
    public Long getLastChgUserId() {
        return addUserId;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        this.addUserId = aLastChgUserId;
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
        return null; // No corresponding field
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        // No corresponding field
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
        return null; // No corresponding field
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        // No corresponding field
    }

    @Override
    public Timestamp getStatusTime() {
        return null; // No corresponding field
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        // No corresponding field
    }

    @Override
    public String getSuperclass() {
        return this.getClass().getSuperclass().getName();
    }

    @Override
    public Long getUid() {
        return notificationUid;
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
        return null;
    }


}
