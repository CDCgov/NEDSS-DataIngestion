package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PublicHealthCase;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class PublicHealthCaseDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;

    private boolean caseStatusDirty = false;

    private boolean isPamCase;
    private boolean isPageCase;
    private boolean isStdHivProgramAreaCode;

    private String caseTypeCd;
    private Long publicHealthCaseUid;
    private String activityDurationAmt;
    private String activityDurationUnitCd;
    private Timestamp activityFromTime;
    private Timestamp activityToTime;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String caseClassCd;
    private String cd;
    private String cdDescTxt;
    private String cdSystemCd;
    private String cdSystemDescTxt;
    private String confidentialityCd;
    private String confidentialityDescTxt;
    private String detectionMethodCd;
    private String detectionMethodDescTxt;
    private Timestamp diagnosisTime;
    private String diseaseImportedCd;
    private String diseaseImportedDescTxt;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private Integer groupCaseCnt;
    private String investigationStatusCd;
    private Timestamp investigatorAssignedTime;
    private String jurisdictionCd;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String mmwrWeek;
    private String mmwrYear;
    private String outbreakInd;
    private Timestamp outbreakFromTime;
    private Timestamp outbreakToTime;
    private String outbreakName;
    private String outcomeCd;
    private String patAgeAtOnset;
    private String patAgeAtOnsetUnitCd;
    private Long patientGroupId;
    private String progAreaCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Integer repeatNbr;
    private String rptCntyCd;
    private Timestamp rptFormCmpltTime;
    private String rptSourceCd;
    private String rptSourceCdDescTxt;
    private Timestamp rptToCountyTime;
    private Timestamp rptToStateTime;
    private String statusCd;
    private Timestamp statusTime;
    private String transmissionModeCd;
    private String transmissionModeDescTxt;
    private String txt;
    private String userAffiliationTxt;
    private Long programJurisdictionOid;
    private String sharedInd;
    private Integer versionCtrlNbr;

    private String addUserName;
    private String lastChgUserName;
    private Long currentInvestigatorUid;
    private Long currentPatientUid;
    //private boolean isReentrant;


    // Added for Extending PHC table for common fields - ODS changes activity
    // changes
    private String hospitalizedIndCd;
    private Timestamp hospitalizedAdminTime;
    private Timestamp hospitalizedDischargeTime;
    private BigDecimal hospitalizedDurationAmt;
    private String pregnantIndCd;
    // private String dieFromIllnessIndCD;
    private String dayCareIndCd;
    private String foodHandlerIndCd;
    private String importedCountryCd;
    private String importedStateCd;
    private String importedCityDescTxt;
    private String importedCountyCd;
    private Timestamp deceasedTime;
    private Timestamp rptSentTime;
    private String countIntervalCd;
    private boolean isSummaryCase;
    private String priorityCd;
    private Timestamp infectiousFromDate;
    private Timestamp infectiousToDate;
    private String contactInvStatus;
    private String contactInvTxt;
    private String referralBasisCd;
    private String currProcessStateCd;
    private String invPriorityCd;
    private String coinfectionId;
    private Timestamp associatedSpecimenCollDate;
    private String confirmationMethodCd;

    private Timestamp confirmationMethodTime;

    public PublicHealthCaseDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return publicHealthCaseUid;
    }


    public PublicHealthCaseDto(PublicHealthCase publicHealthCase) {
        this.publicHealthCaseUid = publicHealthCase.getPublicHealthCaseUid();
        this.activityDurationAmt = publicHealthCase.getActivityDurationAmt();
        this.activityDurationUnitCd = publicHealthCase.getActivityDurationUnitCd();
        this.activityFromTime = publicHealthCase.getActivityFromTime();
        this.activityToTime = publicHealthCase.getActivityToTime();
        this.addReasonCd = publicHealthCase.getAddReasonCd();
        this.addTime = publicHealthCase.getAddTime();
        this.addUserId = publicHealthCase.getAddUserId();
        this.caseClassCd = publicHealthCase.getCaseClassCd();
        this.caseTypeCd = publicHealthCase.getCaseTypeCd();
        this.cd = publicHealthCase.getCd();
        this.cdDescTxt = publicHealthCase.getCdDescTxt();
        this.cdSystemCd = publicHealthCase.getCdSystemCd();
        this.cdSystemDescTxt = publicHealthCase.getCdSystemDescTxt();
        this.confidentialityCd = publicHealthCase.getConfidentialityCd();
        this.confidentialityDescTxt = publicHealthCase.getConfidentialityDescTxt();
        this.detectionMethodCd = publicHealthCase.getDetectionMethodCd();
        this.detectionMethodDescTxt = publicHealthCase.getDetectionMethodDescTxt();
        this.diagnosisTime = publicHealthCase.getDiagnosisTime();
        this.diseaseImportedCd = publicHealthCase.getDiseaseImportedCd();
        this.diseaseImportedDescTxt = publicHealthCase.getDiseaseImportedDescTxt();
        this.effectiveDurationAmt = publicHealthCase.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = publicHealthCase.getEffectiveDurationUnitCd();
        this.effectiveFromTime = publicHealthCase.getEffectiveFromTime();
        this.effectiveToTime = publicHealthCase.getEffectiveToTime();
        this.groupCaseCnt = publicHealthCase.getGroupCaseCnt();
        this.investigationStatusCd = publicHealthCase.getInvestigationStatusCd();
        this.investigatorAssignedTime = publicHealthCase.getInvestigatorAssignedTime();
        this.jurisdictionCd = publicHealthCase.getJurisdictionCd();
        this.lastChgReasonCd = publicHealthCase.getLastChgReasonCd();
        this.lastChgTime = publicHealthCase.getLastChgTime();
        this.lastChgUserId = publicHealthCase.getLastChgUserId();
        this.localId = publicHealthCase.getLocalId();
        this.mmwrWeek = publicHealthCase.getMmwrWeek();
        this.mmwrYear = publicHealthCase.getMmwrYear();
        this.outbreakInd = publicHealthCase.getOutbreakInd();
        this.outbreakFromTime = publicHealthCase.getOutbreakFromTime();
        this.outbreakToTime = publicHealthCase.getOutbreakToTime();
        this.outbreakName = publicHealthCase.getOutbreakName();
        this.outcomeCd = publicHealthCase.getOutcomeCd();
        this.patAgeAtOnset = publicHealthCase.getPatAgeAtOnset();
        this.patAgeAtOnsetUnitCd = publicHealthCase.getPatAgeAtOnsetUnitCd();
        this.patientGroupId = publicHealthCase.getPatientGroupId();
        this.progAreaCd = publicHealthCase.getProgAreaCd();
        this.recordStatusCd = publicHealthCase.getRecordStatusCd();
        this.recordStatusTime = publicHealthCase.getRecordStatusTime();
        this.repeatNbr = publicHealthCase.getRepeatNbr();
        this.rptCntyCd = publicHealthCase.getRptCntyCd();
        this.rptFormCmpltTime = publicHealthCase.getRptFormCmpltTime();
        this.rptSourceCd = publicHealthCase.getRptSourceCd();
        this.rptSourceCdDescTxt = publicHealthCase.getRptSourceCdDescTxt();
        this.rptToCountyTime = publicHealthCase.getRptToCountyTime();
        this.rptToStateTime = publicHealthCase.getRptToStateTime();
        this.statusCd = publicHealthCase.getStatusCd();
        this.statusTime = publicHealthCase.getStatusTime();
        this.transmissionModeCd = publicHealthCase.getTransmissionModeCd();
        this.transmissionModeDescTxt = publicHealthCase.getTransmissionModeDescTxt();
        this.txt = publicHealthCase.getTxt();
        this.userAffiliationTxt = publicHealthCase.getUserAffiliationTxt();
        this.programJurisdictionOid = publicHealthCase.getProgramJurisdictionOid();
        this.sharedInd = publicHealthCase.getSharedInd();
        this.versionCtrlNbr = publicHealthCase.getVersionCtrlNbr() != null ? publicHealthCase.getVersionCtrlNbr().intValue() : null;
        this.hospitalizedIndCd = publicHealthCase.getHospitalizedIndCd();
        this.hospitalizedAdminTime = publicHealthCase.getHospitalizedAdminTime();
        this.hospitalizedDischargeTime = publicHealthCase.getHospitalizedDischargeTime();
        this.hospitalizedDurationAmt = publicHealthCase.getHospitalizedDurationAmt();
        this.pregnantIndCd = publicHealthCase.getPregnantIndCd();
        this.dayCareIndCd = publicHealthCase.getDayCareIndCd();
        this.foodHandlerIndCd = publicHealthCase.getFoodHandlerIndCd();
        this.importedCountryCd = publicHealthCase.getImportedCountryCd();
        this.importedStateCd = publicHealthCase.getImportedStateCd();
        this.importedCityDescTxt = publicHealthCase.getImportedCityDescTxt();
        this.importedCountyCd = publicHealthCase.getImportedCountyCd();
        this.deceasedTime = publicHealthCase.getDeceasedTime();
//        this.rptSentTime = publicHealthCase.getRptSentTime();
        this.countIntervalCd = publicHealthCase.getCountIntervalCd();
//        this.isSummaryCase = publicHealthCase.isSummaryCase();
        this.priorityCd = publicHealthCase.getPriorityCd();
        this.infectiousFromDate = publicHealthCase.getInfectiousFromDate();
        this.infectiousToDate = publicHealthCase.getInfectiousToDate();
//        this.contactInvStatus = publicHealthCase.getContactInvStatus();
        this.contactInvTxt = publicHealthCase.getContactInvTxt();
        this.referralBasisCd = publicHealthCase.getReferralBasisCd();
        this.currProcessStateCd = publicHealthCase.getCurrProcessStateCd();
        this.invPriorityCd = publicHealthCase.getInvPriorityCd();
        this.coinfectionId = publicHealthCase.getCoinfectionId();
//        this.associatedSpecimenCollDate = publicHealthCase.getAssociatedSpecimenCollDate();
//        this.confirmationMethodCd = publicHealthCase.getConfirmationMethodCd();
//        this.confirmationMethodTime = publicHealthCase.getConfirmationMethodTime();
//        this.addUserName = publicHealthCase.getAddUserName();
//        this.lastChgUserName = publicHealthCase.getLastChgUserName();
//        this.currentInvestigatorUid = publicHealthCase.getCurrentInvestigatorUid();
//        this.currentPatientUid = publicHealthCase.getCurrentPatientUid();
    }




}
