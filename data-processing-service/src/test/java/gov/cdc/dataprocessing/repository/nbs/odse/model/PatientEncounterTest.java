package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.phc.PatientEncounterDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PatientEncounter;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PatientEncounterTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        PatientEncounter patientEncounter = new PatientEncounter();

        // Assert
        assertNull(patientEncounter.getPatientEncounterUid());
        assertNull(patientEncounter.getActivityDurationAmt());
        assertNull(patientEncounter.getActivityDurationUnitCd());
        assertNull(patientEncounter.getActivityFromTime());
        assertNull(patientEncounter.getActivityToTime());
        assertNull(patientEncounter.getAcuityLevelCd());
        assertNull(patientEncounter.getAcuityLevelDescTxt());
        assertNull(patientEncounter.getAddReasonCd());
        assertNull(patientEncounter.getAddTime());
        assertNull(patientEncounter.getAddUserId());
        assertNull(patientEncounter.getAdmissionSourceCd());
        assertNull(patientEncounter.getAdmissionSourceDescTxt());
        assertNull(patientEncounter.getBirthEncounterInd());
        assertNull(patientEncounter.getCd());
        assertNull(patientEncounter.getCdDescTxt());
        assertNull(patientEncounter.getConfidentialityCd());
        assertNull(patientEncounter.getConfidentialityDescTxt());
        assertNull(patientEncounter.getEffectiveDurationAmt());
        assertNull(patientEncounter.getEffectiveDurationUnitCd());
        assertNull(patientEncounter.getEffectiveFromTime());
        assertNull(patientEncounter.getEffectiveToTime());
        assertNull(patientEncounter.getLastChgReasonCd());
        assertNull(patientEncounter.getLastChgTime());
        assertNull(patientEncounter.getLastChgUserId());
        assertNull(patientEncounter.getLocalId());
        assertNull(patientEncounter.getPriorityCd());
        assertNull(patientEncounter.getPriorityDescTxt());
        assertNull(patientEncounter.getRecordStatusCd());
        assertNull(patientEncounter.getRecordStatusTime());
        assertNull(patientEncounter.getReferralSourceCd());
        assertNull(patientEncounter.getReferralSourceDescTxt());
        assertNull(patientEncounter.getRepeatNbr());
        assertNull(patientEncounter.getStatusCd());
        assertNull(patientEncounter.getStatusTime());
        assertNull(patientEncounter.getTxt());
        assertNull(patientEncounter.getUserAffiliationTxt());
        assertNull(patientEncounter.getProgramJurisdictionOid());
        assertNull(patientEncounter.getSharedInd());
        assertNull(patientEncounter.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long patientEncounterUid = 1L;
        String activityDurationAmt = "durationAmt";
        String activityDurationUnitCd = "unitCd";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String acuityLevelCd = "acuityCd";
        String acuityLevelDescTxt = "acuityDesc";
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String admissionSourceCd = "admissionCd";
        String admissionSourceDescTxt = "admissionDesc";
        String birthEncounterInd = "Y";
        String cd = "code";
        String cdDescTxt = "codeDesc";
        String confidentialityCd = "confCd";
        String confidentialityDescTxt = "confDesc";
        String effectiveDurationAmt = "effDuration";
        String effectiveDurationUnitCd = "effUnitCd";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String priorityCd = "priorityCd";
        String priorityDescTxt = "priorityDesc";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String referralSourceCd = "referralCd";
        String referralSourceDescTxt = "referralDesc";
        Integer repeatNbr = 1;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "text";
        String userAffiliationTxt = "affiliation";
        Long programJurisdictionOid = 4L;
        String sharedInd = "shared";
        Integer versionCtrlNbr = 1;

        PatientEncounterDto dto = new PatientEncounterDto();
        dto.setPatientEncounterUid(patientEncounterUid);
        dto.setActivityDurationAmt(activityDurationAmt);
        dto.setActivityDurationUnitCd(activityDurationUnitCd);
        dto.setActivityFromTime(activityFromTime);
        dto.setActivityToTime(activityToTime);
        dto.setAcuityLevelCd(acuityLevelCd);
        dto.setAcuityLevelDescTxt(acuityLevelDescTxt);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setAdmissionSourceCd(admissionSourceCd);
        dto.setAdmissionSourceDescTxt(admissionSourceDescTxt);
        dto.setBirthEncounterInd(birthEncounterInd);
        dto.setCd(cd);
        dto.setCdDescTxt(cdDescTxt);
        dto.setConfidentialityCd(confidentialityCd);
        dto.setConfidentialityDescTxt(confidentialityDescTxt);
        dto.setEffectiveDurationAmt(effectiveDurationAmt);
        dto.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        dto.setEffectiveFromTime(effectiveFromTime);
        dto.setEffectiveToTime(effectiveToTime);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLocalId(localId);
        dto.setPriorityCd(priorityCd);
        dto.setPriorityDescTxt(priorityDescTxt);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setReferralSourceCd(referralSourceCd);
        dto.setReferralSourceDescTxt(referralSourceDescTxt);
        dto.setRepeatNbr(repeatNbr);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setTxt(txt);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setSharedInd(sharedInd);
        dto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        PatientEncounter patientEncounter = new PatientEncounter(dto);

        // Assert
        assertEquals(patientEncounterUid, patientEncounter.getPatientEncounterUid());
        assertEquals(activityDurationAmt, patientEncounter.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, patientEncounter.getActivityDurationUnitCd());
        assertEquals(activityFromTime, patientEncounter.getActivityFromTime());
        assertEquals(activityToTime, patientEncounter.getActivityToTime());
        assertEquals(acuityLevelCd, patientEncounter.getAcuityLevelCd());
        assertEquals(acuityLevelDescTxt, patientEncounter.getAcuityLevelDescTxt());
        assertEquals(addReasonCd, patientEncounter.getAddReasonCd());
        assertEquals(addTime, patientEncounter.getAddTime());
        assertEquals(addUserId, patientEncounter.getAddUserId());
        assertEquals(admissionSourceCd, patientEncounter.getAdmissionSourceCd());
        assertEquals(admissionSourceDescTxt, patientEncounter.getAdmissionSourceDescTxt());
        assertEquals(birthEncounterInd, patientEncounter.getBirthEncounterInd());
        assertEquals(cd, patientEncounter.getCd());
        assertEquals(cdDescTxt, patientEncounter.getCdDescTxt());
        assertEquals(confidentialityCd, patientEncounter.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, patientEncounter.getConfidentialityDescTxt());
        assertEquals(effectiveDurationAmt, patientEncounter.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, patientEncounter.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, patientEncounter.getEffectiveFromTime());
        assertEquals(effectiveToTime, patientEncounter.getEffectiveToTime());
        assertEquals(lastChgReasonCd, patientEncounter.getLastChgReasonCd());
        assertEquals(lastChgTime, patientEncounter.getLastChgTime());
        assertEquals(lastChgUserId, patientEncounter.getLastChgUserId());
        assertEquals(localId, patientEncounter.getLocalId());
        assertEquals(priorityCd, patientEncounter.getPriorityCd());
        assertEquals(priorityDescTxt, patientEncounter.getPriorityDescTxt());
        assertEquals(recordStatusCd, patientEncounter.getRecordStatusCd());
        assertEquals(recordStatusTime, patientEncounter.getRecordStatusTime());
        assertEquals(referralSourceCd, patientEncounter.getReferralSourceCd());
        assertEquals(referralSourceDescTxt, patientEncounter.getReferralSourceDescTxt());
        assertEquals(repeatNbr, patientEncounter.getRepeatNbr());
        assertEquals(statusCd, patientEncounter.getStatusCd());
        assertEquals(statusTime, patientEncounter.getStatusTime());
        assertEquals(txt, patientEncounter.getTxt());
        assertEquals(userAffiliationTxt, patientEncounter.getUserAffiliationTxt());
        assertEquals(programJurisdictionOid, patientEncounter.getProgramJurisdictionOid());
        assertEquals(sharedInd, patientEncounter.getSharedInd());
        assertEquals(versionCtrlNbr, patientEncounter.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PatientEncounter patientEncounter = new PatientEncounter();

        Long patientEncounterUid = 1L;
        String activityDurationAmt = "durationAmt";
        String activityDurationUnitCd = "unitCd";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String acuityLevelCd = "acuityCd";
        String acuityLevelDescTxt = "acuityDesc";
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String admissionSourceCd = "admissionCd";
        String admissionSourceDescTxt = "admissionDesc";
        String birthEncounterInd = "Y";
        String cd = "code";
        String cdDescTxt = "codeDesc";
        String confidentialityCd = "confCd";
        String confidentialityDescTxt = "confDesc";
        String effectiveDurationAmt = "effDuration";
        String effectiveDurationUnitCd = "effUnitCd";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String priorityCd = "priorityCd";
        String priorityDescTxt = "priorityDesc";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String referralSourceCd = "referralCd";
        String referralSourceDescTxt = "referralDesc";
        Integer repeatNbr = 1;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "text";
        String userAffiliationTxt = "affiliation";
        Long programJurisdictionOid = 4L;
        String sharedInd = "shared";
        Integer versionCtrlNbr = 1;

        // Act
        patientEncounter.setPatientEncounterUid(patientEncounterUid);
        patientEncounter.setActivityDurationAmt(activityDurationAmt);
        patientEncounter.setActivityDurationUnitCd(activityDurationUnitCd);
        patientEncounter.setActivityFromTime(activityFromTime);
        patientEncounter.setActivityToTime(activityToTime);
        patientEncounter.setAcuityLevelCd(acuityLevelCd);
        patientEncounter.setAcuityLevelDescTxt(acuityLevelDescTxt);
        patientEncounter.setAddReasonCd(addReasonCd);
        patientEncounter.setAddTime(addTime);
        patientEncounter.setAddUserId(addUserId);
        patientEncounter.setAdmissionSourceCd(admissionSourceCd);
        patientEncounter.setAdmissionSourceDescTxt(admissionSourceDescTxt);
        patientEncounter.setBirthEncounterInd(birthEncounterInd);
        patientEncounter.setCd(cd);
        patientEncounter.setCdDescTxt(cdDescTxt);
        patientEncounter.setConfidentialityCd(confidentialityCd);
        patientEncounter.setConfidentialityDescTxt(confidentialityDescTxt);
        patientEncounter.setEffectiveDurationAmt(effectiveDurationAmt);
        patientEncounter.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        patientEncounter.setEffectiveFromTime(effectiveFromTime);
        patientEncounter.setEffectiveToTime(effectiveToTime);
        patientEncounter.setLastChgReasonCd(lastChgReasonCd);
        patientEncounter.setLastChgTime(lastChgTime);
        patientEncounter.setLastChgUserId(lastChgUserId);
        patientEncounter.setLocalId(localId);
        patientEncounter.setPriorityCd(priorityCd);
        patientEncounter.setPriorityDescTxt(priorityDescTxt);
        patientEncounter.setRecordStatusCd(recordStatusCd);
        patientEncounter.setRecordStatusTime(recordStatusTime);
        patientEncounter.setReferralSourceCd(referralSourceCd);
        patientEncounter.setReferralSourceDescTxt(referralSourceDescTxt);
        patientEncounter.setRepeatNbr(repeatNbr);
        patientEncounter.setStatusCd(statusCd);
        patientEncounter.setStatusTime(statusTime);
        patientEncounter.setTxt(txt);
        patientEncounter.setUserAffiliationTxt(userAffiliationTxt);
        patientEncounter.setProgramJurisdictionOid(programJurisdictionOid);
        patientEncounter.setSharedInd(sharedInd);
        patientEncounter.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(patientEncounterUid, patientEncounter.getPatientEncounterUid());
        assertEquals(activityDurationAmt, patientEncounter.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, patientEncounter.getActivityDurationUnitCd());
        assertEquals(activityFromTime, patientEncounter.getActivityFromTime());
        assertEquals(activityToTime, patientEncounter.getActivityToTime());
        assertEquals(acuityLevelCd, patientEncounter.getAcuityLevelCd());
        assertEquals(acuityLevelDescTxt, patientEncounter.getAcuityLevelDescTxt());
        assertEquals(addReasonCd, patientEncounter.getAddReasonCd());
        assertEquals(addTime, patientEncounter.getAddTime());
        assertEquals(addUserId, patientEncounter.getAddUserId());
        assertEquals(admissionSourceCd, patientEncounter.getAdmissionSourceCd());
        assertEquals(admissionSourceDescTxt, patientEncounter.getAdmissionSourceDescTxt());
        assertEquals(birthEncounterInd, patientEncounter.getBirthEncounterInd());
        assertEquals(cd, patientEncounter.getCd());
        assertEquals(cdDescTxt, patientEncounter.getCdDescTxt());
        assertEquals(confidentialityCd, patientEncounter.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, patientEncounter.getConfidentialityDescTxt());
        assertEquals(effectiveDurationAmt, patientEncounter.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, patientEncounter.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, patientEncounter.getEffectiveFromTime());
        assertEquals(effectiveToTime, patientEncounter.getEffectiveToTime());
        assertEquals(lastChgReasonCd, patientEncounter.getLastChgReasonCd());
        assertEquals(lastChgTime, patientEncounter.getLastChgTime());
        assertEquals(lastChgUserId, patientEncounter.getLastChgUserId());
        assertEquals(localId, patientEncounter.getLocalId());
        assertEquals(priorityCd, patientEncounter.getPriorityCd());
        assertEquals(priorityDescTxt, patientEncounter.getPriorityDescTxt());
        assertEquals(recordStatusCd, patientEncounter.getRecordStatusCd());
        assertEquals(recordStatusTime, patientEncounter.getRecordStatusTime());
        assertEquals(referralSourceCd, patientEncounter.getReferralSourceCd());
        assertEquals(referralSourceDescTxt, patientEncounter.getReferralSourceDescTxt());
        assertEquals(repeatNbr, patientEncounter.getRepeatNbr());
        assertEquals(statusCd, patientEncounter.getStatusCd());
        assertEquals(statusTime, patientEncounter.getStatusTime());
        assertEquals(txt, patientEncounter.getTxt());
        assertEquals(userAffiliationTxt, patientEncounter.getUserAffiliationTxt());
        assertEquals(programJurisdictionOid, patientEncounter.getProgramJurisdictionOid());
        assertEquals(sharedInd, patientEncounter.getSharedInd());
        assertEquals(versionCtrlNbr, patientEncounter.getVersionCtrlNbr());
    }
}
