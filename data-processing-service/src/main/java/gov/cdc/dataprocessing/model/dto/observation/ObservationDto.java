package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import lombok.Getter;
import lombok.Setter;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class ObservationDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long observationUid;

    private String activityDurationAmt;

    private String activityDurationUnitCd;

    private Timestamp activityFromTime;

    private Timestamp activityToTime;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String addUserName; // BB - civil00012298 - add Name field to
    // display now instead of Id

    private String altCd;

    private String altCdDescTxt;

    private String altCdSystemCd;

    private String altCdSystemDescTxt;

    private String cd;

    private String cdDerivedInd;

    private String cdDescTxt;

    private String cdSystemCd;

    private String cdSystemDescTxt;

    private String confidentialityCd;

    private String confidentialityDescTxt;

    private String ctrlCdDisplayForm;

    private String ctrlCdUserDefined1;

    private String ctrlCdUserDefined2;

    private String ctrlCdUserDefined3;

    private String ctrlCdUserDefined4;

    private Integer derivationExp;

    private String effectiveDurationAmt;

    private String effectiveDurationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String electronicInd;

    private String groupLevelCd;

    private String jurisdictionCd;

    private String labConditionCd;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String lastChgUserName; // BB - civil00012298 - add Name field to
    // display now instead of Id

    private String localId;

    private String methodCd;

    private String methodDescTxt;

    private String obsDomainCd;

    private String obsDomainCdSt1;

    private String pnuCd;

    private String priorityCd;

    private String priorityDescTxt;

    private String progAreaCd;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private Integer repeatNbr;

    private Timestamp rptToStateTime;

    private String statusCd;

    private Timestamp statusTime;

    private Long subjectPersonUid;

    private String targetSiteCd;

    private String targetSiteDescTxt;

    private String txt;

    private String userAffiliationTxt;

    private String valueCd;

    private String ynuCd;

    private Long programJurisdictionOid;

    private String sharedInd;

    private Integer versionCtrlNbr;

//    private boolean itDirty = false;
//
//    private boolean itNew = true;
//
//    private boolean itDelete = false;

    private String cdVersion;

    private String searchResultOT;

    private String searchResultRT;

    private String cdSystemCdOT;

    private String cdSystemCdRT;

    private String hiddenCd;

    private String codedResultCd;
    private String organismCd;
    private String susceptabilityVal;
    private String resultedMethodCd;
    private String drugNameCd;
    private String interpretiveFlagCd;

    private String processingDecisionCd;
    private String processingDecisionTxt;

    // Task: #2567, #2566
    private String pregnantIndCd;
    private Integer pregnantWeek;

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return observationUid;
    }

    public ObservationDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    // Constructor for converting Observation to ObservationDto
    public ObservationDto(Observation observation) {
        this.observationUid = observation.getObservationUid();
        this.activityDurationAmt = observation.getActivityDurationAmt();
        this.activityDurationUnitCd = observation.getActivityDurationUnitCd();
        this.activityFromTime = observation.getActivityFromTime();
        this.activityToTime = observation.getActivityToTime();
        this.addReasonCd = observation.getAddReasonCd();
        this.addTime = observation.getAddTime();
        this.addUserId = observation.getAddUserId();
//        this.addUserName = observation.getAddUserName();
        this.altCd = observation.getAltCd();
        this.altCdDescTxt = observation.getAltCdDescTxt();
        this.altCdSystemCd = observation.getAltCdSystemCd();
        this.altCdSystemDescTxt = observation.getAltCdSystemDescTxt();
        this.cd = observation.getCd();
        this.cdDerivedInd = observation.getCdDerivedInd();
        this.cdDescTxt = observation.getCdDescTxt();
        this.cdSystemCd = observation.getCdSystemCd();
        this.cdSystemDescTxt = observation.getCdSystemDescTxt();
        this.confidentialityCd = observation.getConfidentialityCd();
        this.confidentialityDescTxt = observation.getConfidentialityDescTxt();
        this.ctrlCdDisplayForm = observation.getCtrlCdDisplayForm();
        this.ctrlCdUserDefined1 = observation.getCtrlCdUserDefined1();
        this.ctrlCdUserDefined2 = observation.getCtrlCdUserDefined2();
        this.ctrlCdUserDefined3 = observation.getCtrlCdUserDefined3();
        this.ctrlCdUserDefined4 = observation.getCtrlCdUserDefined4();
        this.derivationExp = observation.getDerivationExp();
        this.effectiveDurationAmt = observation.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = observation.getEffectiveDurationUnitCd();
        this.effectiveFromTime = observation.getEffectiveFromTime();
        this.effectiveToTime = observation.getEffectiveToTime();
        this.electronicInd = observation.getElectronicInd();
        this.groupLevelCd = observation.getGroupLevelCd();
        this.jurisdictionCd = observation.getJurisdictionCd();
        this.labConditionCd = observation.getLabConditionCd();
        this.lastChgReasonCd = observation.getLastChgReasonCd();
        this.lastChgTime = observation.getLastChgTime();
        this.lastChgUserId = observation.getLastChgUserId();
//        this.lastChgUserName = observation.getLastChgUserName();
        this.localId = observation.getLocalId();
        this.methodCd = observation.getMethodCd();
        this.methodDescTxt = observation.getMethodDescTxt();
        this.obsDomainCd = observation.getObsDomainCd();
        this.obsDomainCdSt1 = observation.getObsDomainCdSt1();
        this.pnuCd = observation.getPnuCd();
        this.priorityCd = observation.getPriorityCd();
        this.priorityDescTxt = observation.getPriorityDescTxt();
        this.progAreaCd = observation.getProgAreaCd();
        this.recordStatusCd = observation.getRecordStatusCd();
        this.recordStatusTime = observation.getRecordStatusTime();
        this.repeatNbr = observation.getRepeatNbr();
        this.statusCd = observation.getStatusCd();
        this.statusTime = observation.getStatusTime();
        this.subjectPersonUid = observation.getSubjectPersonUid();
        this.targetSiteCd = observation.getTargetSiteCd();
        this.targetSiteDescTxt = observation.getTargetSiteDescTxt();
        this.txt = observation.getTxt();
        this.userAffiliationTxt = observation.getUserAffiliationTxt();
        this.valueCd = observation.getValueCd();
        this.ynuCd = observation.getYnuCd();
        this.programJurisdictionOid = observation.getProgramJurisdictionOid();
        this.sharedInd = observation.getSharedInd();
        this.versionCtrlNbr = observation.getVersionCtrlNbr();
        this.altCd = observation.getAltCd();
        this.altCdDescTxt = observation.getAltCdDescTxt();
        this.altCdSystemCd = observation.getAltCdSystemCd();
        this.altCdSystemDescTxt = observation.getAltCdSystemDescTxt();
        this.cdDerivedInd = observation.getCdDerivedInd();
        this.rptToStateTime = observation.getRptToStateTime();
        this.cdVersion = observation.getCdVersion();
        this.processingDecisionCd = observation.getProcessingDecisionCd();
        this.pregnantIndCd = observation.getPregnantIndCd();
        this.pregnantWeek = observation.getPregnantWeek();
        this.processingDecisionTxt = observation.getProcessingDecisionTxt();
    }

//    // Constructor for converting Observation to ObservationDto
//    public ObservationDto(Observation observation) {
//        this.observationUid = observation.getObservationUid();
//        this.activityDurationAmt = observation.getActivityDurationAmt();
//        this.activityDurationUnitCd = observation.getActivityDurationUnitCd();
//        this.activityFromTime = observation.getActivityFromTime();
//        this.activityToTime = observation.getActivityToTime();
//        this.addReasonCd = observation.getAddReasonCd();
//        this.addTime = observation.getAddTime();
//        this.addUserId = observation.getAddUserId();
////        this.addUserName = observation.getAddUserName();
//        this.altCd = observation.getAltCd();
//        this.altCdDescTxt = observation.getAltCdDescTxt();
//        this.altCdSystemCd = observation.getAltCdSystemCd();
//        this.altCdSystemDescTxt = observation.getAltCdSystemDescTxt();
//        this.cd = observation.getCd();
//        this.cdDerivedInd = observation.getCdDerivedInd();
//        this.cdDescTxt = observation.getCdDescTxt();
//        this.cdSystemCd = observation.getCdSystemCd();
//        this.cdSystemDescTxt = observation.getCdSystemDescTxt();
//        this.confidentialityCd = observation.getConfidentialityCd();
//        this.confidentialityDescTxt = observation.getConfidentialityDescTxt();
//        this.ctrlCdDisplayForm = observation.getCtrlCdDisplayForm();
//        this.ctrlCdUserDefined1 = observation.getCtrlCdUserDefined1();
//        this.ctrlCdUserDefined2 = observation.getCtrlCdUserDefined2();
//        this.ctrlCdUserDefined3 = observation.getCtrlCdUserDefined3();
//        this.ctrlCdUserDefined4 = observation.getCtrlCdUserDefined4();
//        this.derivationExp = observation.getDerivationExp();
//        this.effectiveDurationAmt = observation.getEffectiveDurationAmt();
//        this.effectiveDurationUnitCd = observation.getEffectiveDurationUnitCd();
//        this.effectiveFromTime = observation.getEffectiveFromTime();
//        this.effectiveToTime = observation.getEffectiveToTime();
//        this.electronicInd = observation.getElectronicInd();
//        this.groupLevelCd = observation.getGroupLevelCd();
//        this.jurisdictionCd = observation.getJurisdictionCd();
//        this.labConditionCd = observation.getLabConditionCd();
//        this.lastChgReasonCd = observation.getLastChgReasonCd();
//        this.lastChgTime = observation.getLastChgTime();
//        this.lastChgUserId = observation.getLastChgUserId();
////        this.lastChgUserName = observation.getLastChgUserName();
//        this.localId = observation.getLocalId();
//        this.methodCd = observation.getMethodCd();
//        this.methodDescTxt = observation.getMethodDescTxt();
//        this.obsDomainCd = observation.getObsDomainCd();
//        this.obsDomainCdSt1 = observation.getObsDomainCdSt1();
//        this.pnuCd = observation.getPnuCd();
//        this.priorityCd = observation.getPriorityCd();
//        this.priorityDescTxt = observation.getPriorityDescTxt();
//        this.progAreaCd = observation.getProgAreaCd();
//        this.recordStatusCd = observation.getRecordStatusCd();
//        this.recordStatusTime = observation.getRecordStatusTime();
//        this.repeatNbr = observation.getRepeatNbr();
//        this.statusCd = observation.getStatusCd();
//        this.statusTime = observation.getStatusTime();
//        this.subjectPersonUid = observation.getSubjectPersonUid();
//        this.targetSiteCd = observation.getTargetSiteCd();
//        this.targetSiteDescTxt = observation.getTargetSiteDescTxt();
//        this.txt = observation.getTxt();
//        this.userAffiliationTxt = observation.getUserAffiliationTxt();
//        this.valueCd = observation.getValueCd();
//        this.ynuCd = observation.getYnuCd();
//        this.programJurisdictionOid = observation.getProgramJurisdictionOid();
//        this.sharedInd = observation.getSharedInd();
//        this.versionCtrlNbr = observation.getVersionCtrlNbr();
//        this.altCd = observation.getAltCd();
//        this.altCdDescTxt = observation.getAltCdDescTxt();
//        this.altCdSystemCd = observation.getAltCdSystemCd();
//        this.altCdSystemDescTxt = observation.getAltCdSystemDescTxt();
//        this.cdDerivedInd = observation.getCdDerivedInd();
//        this.rptToStateTime = observation.getRptToStateTime();
//        this.cdVersion = observation.getCdVersion();
//        this.processingDecisionCd = observation.getProcessingDecisionCd();
//        this.pregnantIndCd = observation.getPregnantIndCd();
//        this.pregnantWeek = observation.getPregnantWeek();
//        this.processingDecisionTxt = observation.getProcessingDecisionTxt();
//    }


}
