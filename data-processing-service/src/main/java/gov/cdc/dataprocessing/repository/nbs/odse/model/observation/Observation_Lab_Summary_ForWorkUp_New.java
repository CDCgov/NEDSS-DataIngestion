package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
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
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class Observation_Lab_Summary_ForWorkUp_New extends ObservationBase {
    private Long uid;
    
    public Observation_Lab_Summary_ForWorkUp_New() {
        
    }
    
    public Observation_Lab_Summary_ForWorkUp_New(Observation base) {
        this.observationUid = base.getObservationUid();
        this.activityDurationAmt = base.getActivityDurationAmt();
        this.activityDurationUnitCd = base.getActivityDurationUnitCd();
        this.activityFromTime = base.getActivityFromTime();
        this.activityToTime = base.getActivityToTime();
        this.addReasonCd = base.getAddReasonCd();
        this.addTime = base.getAddTime();
        this.addUserId = base.getAddUserId();
//        this.addUserName = base.getAddUserName();
        this.altCd = base.getAltCd();
        this.altCdDescTxt = base.getAltCdDescTxt();
        this.altCdSystemCd = base.getAltCdSystemCd();
        this.altCdSystemDescTxt = base.getAltCdSystemDescTxt();
        this.cd = base.getCd();
        this.cdDerivedInd = base.getCdDerivedInd();
        this.cdDescTxt = base.getCdDescTxt();
        this.cdSystemCd = base.getCdSystemCd();
        this.cdSystemDescTxt = base.getCdSystemDescTxt();
        this.confidentialityCd = base.getConfidentialityCd();
        this.confidentialityDescTxt = base.getConfidentialityDescTxt();
        this.ctrlCdDisplayForm = base.getCtrlCdDisplayForm();
        this.ctrlCdUserDefined1 = base.getCtrlCdUserDefined1();
        this.ctrlCdUserDefined2 = base.getCtrlCdUserDefined2();
        this.ctrlCdUserDefined3 = base.getCtrlCdUserDefined3();
        this.ctrlCdUserDefined4 = base.getCtrlCdUserDefined4();
        this.derivationExp = base.getDerivationExp();
        this.effectiveDurationAmt = base.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = base.getEffectiveDurationUnitCd();
        this.effectiveFromTime = base.getEffectiveFromTime();
        this.effectiveToTime = base.getEffectiveToTime();
        this.electronicInd = base.getElectronicInd();
        this.groupLevelCd = base.getGroupLevelCd();
        this.jurisdictionCd = base.getJurisdictionCd();
        this.labConditionCd = base.getLabConditionCd();
        this.lastChgReasonCd = base.getLastChgReasonCd();
        this.lastChgTime = base.getLastChgTime();
        this.lastChgUserId = base.getLastChgUserId();
//        this.lastChgUserName = base.getLastChgUserName();
        this.localId = base.getLocalId();
        this.methodCd = base.getMethodCd();
        this.methodDescTxt = base.getMethodDescTxt();
        this.obsDomainCd = base.getObsDomainCd();
        this.obsDomainCdSt1 = base.getObsDomainCdSt1();
        this.pnuCd = base.getPnuCd();
        this.priorityCd = base.getPriorityCd();
        this.priorityDescTxt = base.getPriorityDescTxt();
        this.progAreaCd = base.getProgAreaCd();
        this.recordStatusCd = base.getRecordStatusCd();
        this.recordStatusTime = base.getRecordStatusTime();
        this.repeatNbr = base.getRepeatNbr();
        this.statusCd = base.getStatusCd();
        this.statusTime = base.getStatusTime();
        this.subjectPersonUid = base.getSubjectPersonUid();
        this.targetSiteCd = base.getTargetSiteCd();
        this.targetSiteDescTxt = base.getTargetSiteDescTxt();
        this.txt = base.getTxt();
        this.userAffiliationTxt = base.getUserAffiliationTxt();
        this.valueCd = base.getValueCd();
        this.ynuCd = base.getYnuCd();
        this.programJurisdictionOid = base.getProgramJurisdictionOid();
        this.sharedInd = base.getSharedInd();
        this.versionCtrlNbr = base.getVersionCtrlNbr();
        this.altCd = base.getAltCd();
        this.altCdDescTxt = base.getAltCdDescTxt();
        this.altCdSystemCd = base.getAltCdSystemCd();
        this.altCdSystemDescTxt = base.getAltCdSystemDescTxt();
        this.cdDerivedInd = base.getCdDerivedInd();
        this.rptToStateTime = base.getRptToStateTime();
        this.cdVersion = base.getCdVersion();
        this.processingDecisionCd = base.getProcessingDecisionCd();
        this.pregnantIndCd = base.getPregnantIndCd();
        this.pregnantWeek = base.getPregnantWeek();
        this.processingDecisionTxt = base.getProcessingDecisionTxt();
    }
}
