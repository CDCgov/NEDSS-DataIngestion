package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Observation")
public class Observation extends ObservationBase{

    public Observation() {

    }

    // Constructors, getters, and setters
    public Observation(ObservationDto observationDto) {
        this.observationUid = observationDto.getObservationUid();
        this.activityDurationAmt = observationDto.getActivityDurationAmt();
        this.activityDurationUnitCd = observationDto.getActivityDurationUnitCd();
        this.activityFromTime = observationDto.getActivityFromTime();
        this.activityToTime = observationDto.getActivityToTime();
        this.addReasonCd = observationDto.getAddReasonCd();
        this.addTime = observationDto.getAddTime();
        this.addUserId = observationDto.getAddUserId();
//        this.addUserName = observationDto.getAddUserName();
        this.altCd = observationDto.getAltCd();
        this.altCdDescTxt = observationDto.getAltCdDescTxt();
        this.altCdSystemCd = observationDto.getAltCdSystemCd();
        this.altCdSystemDescTxt = observationDto.getAltCdSystemDescTxt();
        this.cd = observationDto.getCd();
        this.cdDerivedInd = observationDto.getCdDerivedInd();
        this.cdDescTxt = observationDto.getCdDescTxt();
        this.cdSystemCd = observationDto.getCdSystemCd();
        this.cdSystemDescTxt = observationDto.getCdSystemDescTxt();
        this.confidentialityCd = observationDto.getConfidentialityCd();
        this.confidentialityDescTxt = observationDto.getConfidentialityDescTxt();
        this.ctrlCdDisplayForm = observationDto.getCtrlCdDisplayForm();
        this.ctrlCdUserDefined1 = observationDto.getCtrlCdUserDefined1();
        this.ctrlCdUserDefined2 = observationDto.getCtrlCdUserDefined2();
        this.ctrlCdUserDefined3 = observationDto.getCtrlCdUserDefined3();
        this.ctrlCdUserDefined4 = observationDto.getCtrlCdUserDefined4();
        this.derivationExp = observationDto.getDerivationExp();
        this.effectiveDurationAmt = observationDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = observationDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = observationDto.getEffectiveFromTime();
        this.effectiveToTime = observationDto.getEffectiveToTime();
        this.electronicInd = observationDto.getElectronicInd();
        this.groupLevelCd = observationDto.getGroupLevelCd();
        this.jurisdictionCd = observationDto.getJurisdictionCd();
        this.labConditionCd = observationDto.getLabConditionCd();
        this.lastChgReasonCd = observationDto.getLastChgReasonCd();
        this.lastChgTime = observationDto.getLastChgTime();
        this.lastChgUserId = observationDto.getLastChgUserId();
//        this.lastChgUserName = observationDto.getLastChgUserName();
        this.localId = observationDto.getLocalId();
        this.methodCd = observationDto.getMethodCd();
        this.methodDescTxt = observationDto.getMethodDescTxt();
        this.obsDomainCd = observationDto.getObsDomainCd();
        this.obsDomainCdSt1 = observationDto.getObsDomainCdSt1();
        this.pnuCd = observationDto.getPnuCd();
        this.priorityCd = observationDto.getPriorityCd();
        this.priorityDescTxt = observationDto.getPriorityDescTxt();
        this.progAreaCd = observationDto.getProgAreaCd();
        this.recordStatusCd = observationDto.getRecordStatusCd();
        this.recordStatusTime = observationDto.getRecordStatusTime();
        this.repeatNbr = observationDto.getRepeatNbr();
        this.statusCd = observationDto.getStatusCd();
        this.statusTime = observationDto.getStatusTime();
        this.subjectPersonUid = observationDto.getSubjectPersonUid();
        this.targetSiteCd = observationDto.getTargetSiteCd();
        this.targetSiteDescTxt = observationDto.getTargetSiteDescTxt();
        this.txt = observationDto.getTxt();
        this.userAffiliationTxt = observationDto.getUserAffiliationTxt();
        this.valueCd = observationDto.getValueCd();
        this.ynuCd = observationDto.getYnuCd();
        this.programJurisdictionOid = observationDto.getProgramJurisdictionOid();
        this.sharedInd = observationDto.getSharedInd();
        this.versionCtrlNbr = observationDto.getVersionCtrlNbr();
        this.altCd = observationDto.getAltCd();
        this.altCdDescTxt = observationDto.getAltCdDescTxt();
        this.altCdSystemCd = observationDto.getAltCdSystemCd();
        this.altCdSystemDescTxt = observationDto.getAltCdSystemDescTxt();
        this.cdDerivedInd = observationDto.getCdDerivedInd();
        this.rptToStateTime = observationDto.getRptToStateTime();
        this.cdVersion = observationDto.getCdVersion();
        this.processingDecisionCd = observationDto.getProcessingDecisionCd();
        this.pregnantIndCd = observationDto.getPregnantIndCd();
        this.pregnantWeek = observationDto.getPregnantWeek();
        this.processingDecisionTxt = observationDto.getProcessingDecisionTxt();
    }

// Other constructors, getters, and setters

}
