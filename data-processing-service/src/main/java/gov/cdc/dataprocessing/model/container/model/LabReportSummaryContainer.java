package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.interfaces.ReportSummaryInterface;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class LabReportSummaryContainer extends BaseContainer implements RootDtoInterface,Comparable, ReportSummaryInterface {
    private static final long serialVersionUID = 1L;
    private boolean isTouched;
    private boolean isAssociated;
    private Timestamp dateReceived;
    private String dateReceivedS;
    private Integer versionCtrlNbr;
    private Timestamp dateCollected;
    private Timestamp activityFromTime;
    private String type;
    private String programArea;
    private String jurisdiction;
    private String jurisdictionCd;
    private String status;
    private String recordStatusCd;
    private Long observationUid;
    private String patientFirstName;
    private String patientLastName;
    private String personLocalId;
    private Collection<ResultedTestSummaryContainer> theResultedTestSummaryVOCollection;
    private Collection<Object> invSummaryVOs;
    private String orderedTest;
    private Long MPRUid;
    private String cdSystemCd;
    private String actionLink;
    private String resultedTestString;
    private String reportingFacility;
    private String specimenSource;
    private String[] selectedcheckboxIds;
    private String checkBoxId;
    // Added these fields for ER 16368 - Start
    private String providerFirstName = "";
    private String providerLastName = "";
    private String providerSuffix = "";
    private String providerPrefix = "";
    private String providerDegree = "";
    private String providerUid = "";
    private String degree ;
    private String accessionNumber ;
    private boolean isLabFromMorb = false;
    private boolean isReactor = false;
    private String electronicInd;
    private Map<Object,Object> associationsMap;
    private String processingDecisionCd;
    private String disabled = "";
    private ProviderDataForPrintContainer providerDataForPrintVO;
    private boolean isLabFromDoc;
    private Long uid;
    private String sharedInd;
    private String progAreaCd;
    private String localId;
    private Long personUid;
    private String lastNm;
    private String firstNm;
    private Long personParentUid;
    private String currSexCd;
    private String orderingFacility;

    public LabReportSummaryContainer() {

    }

    public LabReportSummaryContainer(Observation_Lab_Summary_ForWorkUp_New observationLabSummaryForWorkUpNew) {
        uid = observationLabSummaryForWorkUpNew.getUid();
        localId = observationLabSummaryForWorkUpNew.getLocalId();
        jurisdictionCd = observationLabSummaryForWorkUpNew.getJurisdictionCd();
        status = observationLabSummaryForWorkUpNew.getStatusCd();
        recordStatusCd = observationLabSummaryForWorkUpNew.getRecordStatusCd();
        orderedTest = observationLabSummaryForWorkUpNew.getCdDescTxt();
        observationUid = observationLabSummaryForWorkUpNew.getObservationUid();
        this.programArea = observationLabSummaryForWorkUpNew.getProgAreaCd();
        recordStatusCd = observationLabSummaryForWorkUpNew.getRecordStatusCd();
        dateReceived = observationLabSummaryForWorkUpNew.getRptToStateTime();
        activityFromTime = observationLabSummaryForWorkUpNew.getActivityFromTime();
        cdSystemCd = observationLabSummaryForWorkUpNew.getCdSystemCd();
        dateCollected = observationLabSummaryForWorkUpNew.getEffectiveFromTime();
        processingDecisionCd = observationLabSummaryForWorkUpNew.getProcessingDecisionCd();
        electronicInd = observationLabSummaryForWorkUpNew.getElectronicInd();
    }

    @Override
    public Long getLastChgUserId() {
        return null;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {

    }

    @Override
    public Timestamp getLastChgTime() {
        return null;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {

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
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean getIsTouched() {
        return isTouched;
    }

    @Override
    public void setItTouched(boolean touched) {
        isTouched = touched;
    }

    @Override
    public boolean getIsAssociated() {
        return isAssociated;
    }

    @Override
    public void setItAssociated(boolean associated) {
        isAssociated  = associated;
    }
}
