package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.interfaces.ReportSummaryInterface;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.dsm.DSMUpdateAlgorithmDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessCaseSummaryDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("all")
public class NbsDocumentContainer extends BaseContainer implements ReportSummaryInterface, RootDtoInterface {

    private static final long serialVersionUID = 1L;
    private NBSDocumentDto nbsDocumentDT = new NBSDocumentDto();
    private EDXActivityLogDto eDXActivityLogDT = new EDXActivityLogDto();
    private ParticipationDto participationDT = new ParticipationDto();
    private PersonContainer patientVO = new PersonContainer();
    private Collection<Object> actRelColl = new ArrayList<>();
    private boolean isFromSecurityQueue = false;
    private Boolean isExistingPatient = false;
    private Boolean isMultiplePatFound = false;
    private boolean conditionFound;
    private String conditionName;
    private boolean isAssociatedInv = false;
    private String originalPHCRLocalId;
    private Map<String, EDXEventProcessDto> eDXEventProcessDTMap = new HashMap<>();
    private boolean isContactRecordDoc;
    private boolean isLabReportDoc;
    private boolean isCaseReportDoc;
    private boolean isMorbReportDoc;
    private boolean isOngoingCase = true;
    private ArrayList<Object> assoSummaryCaseList = new ArrayList<>();
    private ArrayList<Object> summaryCaseListWithInTimeFrame = new ArrayList<>();
    private DSMUpdateAlgorithmDto dsmUpdateAlgorithmDT;
    private Map<String, EDXEventProcessCaseSummaryDto> eDXEventProcessCaseSummaryDTMap = new HashMap<>();

    private boolean isTouched;
    private boolean isAssociated;
    private Long observationUid;
    private Timestamp activityFromTime;
    private Long lastChgUserId;
    private String jurisdictionCd;
    private String progAreaCd;
    private Timestamp lastChgTime;
    private Long addUserId;
    private String lastChgReasonCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private Long programJurisdictionOid;
    private String sharedInd;
    private Integer versionCtrlNbr;
    private Timestamp addTime;

    @Override
    public boolean getIsTouched() {
        return isTouched;
    }

    @Override
    public void setItTouched(boolean touched) {
        this.isTouched = touched;
    }

    @Override
    public boolean getIsAssociated() {
        return isAssociated;
    }

    @Override
    public void setItAssociated(boolean associated) {
        this.isAssociated = associated;
    }

    @Override
    public Long getObservationUid() {
        return observationUid;
    }

    @Override
    public void setObservationUid(Long observationUid) {
        this.observationUid = observationUid;
    }

    @Override
    public Timestamp getActivityFromTime() {
        return activityFromTime;
    }

    @Override
    public void setActivityFromTime(Timestamp aActivityFromTime) {
        this.activityFromTime = aActivityFromTime;
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
        return originalPHCRLocalId;
    }

    @Override
    public void setLocalId(String aLocalId) {
        this.originalPHCRLocalId = aLocalId;
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
        return statusCd;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        this.statusCd = aRecordStatusCd;
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
        return this.getClass().getSuperclass().getName();
    }

    @Override
    public Long getUid() {
        return null;
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

    public void setVersionCtrlNbr(Integer versionCtrlNbr) {
        this.versionCtrlNbr = versionCtrlNbr;
    }
}
