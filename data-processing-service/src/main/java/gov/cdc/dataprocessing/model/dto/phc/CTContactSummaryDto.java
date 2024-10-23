package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
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
public class CTContactSummaryDto extends BaseContainer implements RootDtoInterface {

    private static final long serialVersionUID = 1L;
    private Long ctContactUid;
    private Long contactMprUid;
    private Long subjectMprUid;
    private Timestamp namedOnDate;
    private String localId;
    private Long subjectEntityUid;
    private Long contactEntityUid;
    private String namedBy;
    private String name;
    private boolean contactNamedByPatient;
    private boolean patientNamedByContact;
    private boolean otherNamedByPatient;
    private String priorityCd;
    private String dispositionCd;
    private String priority;
    private String disposition;
    private String invDisposition;
    private String invDispositionCd;
    private Long subjectEntityPhcUid;
    private String subjectPhcLocalId;
    private Long contactEntityPhcUid;
    private String contactPhcLocalId;
    private String subjectPhcCd;

    private String ageReported;
    private String ageReportedUnitCd;
    private Timestamp birthTime;
    private String currSexCd;
    private String relationshipCd;
    private String conditionCd;

    private String ageDOBSex;
    private String description;
    private String associatedWith;
    private Timestamp createDate;


    private String contactReferralBasisCd;
    private Long namedDuringInterviewUid;
    private Long thirdPartyEntityPhcUid; //this is really Other Infected Investigation
    private Long thirdPartyEntityUid; //this is really Other Infected Patient
    private String contactProcessingDecisionCd;
    private String contactProcessingDecision;
    private String subjectName;
    private String contactName;
    private String otherInfectedPatientName;
    private String sourceDispositionCd;
    private String sourceCurrentSexCd;
    private String sourceInterviewStatusCd;
    private String sourceConditionCd; //this was needed because for Syphillis the code could be Congenital
    private String progAreaCd;
    private Timestamp interviewDate;

    private Map<Object, Object> associatedMap;

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
