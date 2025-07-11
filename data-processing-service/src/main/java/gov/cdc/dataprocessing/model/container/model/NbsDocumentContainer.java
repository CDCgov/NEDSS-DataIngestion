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

public class NbsDocumentContainer extends BaseContainer implements ReportSummaryInterface, RootDtoInterface {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private NBSDocumentDto nbsDocumentDT =  new NBSDocumentDto();
    private EDXActivityLogDto eDXActivityLogDT=new EDXActivityLogDto();
    private ParticipationDto participationDT = new ParticipationDto();
    private PersonContainer patientVO= new PersonContainer();
    Collection<Object> actRelColl = new ArrayList<>();
    private boolean isFromSecurityQueue =false;
    private Boolean isExistingPatient =false;
    private Boolean isMultiplePatFound =false;
    private boolean conditionFound;
    private String  conditionName;
    private boolean isAssociatedInv=false;
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


    private Map<String, EDXEventProcessCaseSummaryDto> eDXEventProcessCaseSummaryDTMap = new HashMap<String, EDXEventProcessCaseSummaryDto> ();

    @Override
    public boolean getIsTouched() {
        return false;
    }

    @Override
    public void setItTouched(boolean touched) {

    }

    @Override
    public boolean getIsAssociated() {
        return false;
    }

    @Override
    public void setItAssociated(boolean associated) {

    }

    @Override
    public Long getObservationUid() {
        return null;
    }

    @Override
    public void setObservationUid(Long observationUid) {

    }

    @Override
    public Timestamp getActivityFromTime() {
        return null;
    }

    @Override
    public void setActivityFromTime(Timestamp aActivityFromTime) {

    }

    @Override
    public Long getLastChgUserId() {
        return null;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {

    }

    @Override
    public String getJurisdictionCd() {
        return null;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {

    }

    @Override
    public String getProgAreaCd() {
        return null;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {

    }

    @Override
    public Timestamp getLastChgTime() {
        return null;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {

    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public void setLocalId(String aLocalId) {

    }

    @Override
    public Long getAddUserId() {
        return null;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {

    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {

    }

    @Override
    public String getRecordStatusCd() {
        return null;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {

    }

    @Override
    public Timestamp getRecordStatusTime() {
        return null;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {

    }

    @Override
    public String getStatusCd() {
        return null;
    }

    @Override
    public void setStatusCd(String aStatusCd) {

    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {

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

    }

    @Override
    public String getSharedInd() {
        return null;
    }

    @Override
    public void setSharedInd(String aSharedInd) {

    }

    @Override
    public Integer getVersionCtrlNbr() {
        return null;
    }
}
