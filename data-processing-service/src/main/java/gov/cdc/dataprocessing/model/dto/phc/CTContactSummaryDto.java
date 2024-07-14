package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("all")
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
    public Timestamp getAddTime() {
        return null;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        // No operation needed for setAddTime()
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
