package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PatientEncounter;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PatientEncounterDtoTest {

    @Test
    void testGettersAndSetters() {
        PatientEncounterDto dto = new PatientEncounterDto();

        // Set values
        dto.setPatientEncounterUid(1L);
        dto.setActivityDurationAmt("ActivityDurationAmt");
        dto.setActivityDurationUnitCd("ActivityDurationUnitCd");
        dto.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        dto.setAcuityLevelCd("AcuityLevelCd");
        dto.setAcuityLevelDescTxt("AcuityLevelDescTxt");
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setAdmissionSourceCd("AdmissionSourceCd");
        dto.setAdmissionSourceDescTxt("AdmissionSourceDescTxt");
        dto.setBirthEncounterInd("BirthEncounterInd");
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setConfidentialityCd("ConfidentialityCd");
        dto.setConfidentialityDescTxt("ConfidentialityDescTxt");
        dto.setEffectiveDurationAmt("EffectiveDurationAmt");
        dto.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        dto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setPriorityCd("PriorityCd");
        dto.setPriorityDescTxt("PriorityDescTxt");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setReferralSourceCd("ReferralSourceCd");
        dto.setReferralSourceDescTxt("ReferralSourceDescTxt");
        dto.setRepeatNbr(4);
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTxt("Txt");
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(6);
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setItDirty(false);
        dto.setItNew(true);
        dto.setItDelete(false);

        // Assert values
        assertEquals(1L, dto.getPatientEncounterUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AcuityLevelCd", dto.getAcuityLevelCd());
        assertEquals("AcuityLevelDescTxt", dto.getAcuityLevelDescTxt());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("AdmissionSourceCd", dto.getAdmissionSourceCd());
        assertEquals("AdmissionSourceDescTxt", dto.getAdmissionSourceDescTxt());
        assertEquals("BirthEncounterInd", dto.getBirthEncounterInd());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("PriorityCd", dto.getPriorityCd());
        assertEquals("PriorityDescTxt", dto.getPriorityDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("ReferralSourceCd", dto.getReferralSourceCd());
        assertEquals("ReferralSourceDescTxt", dto.getReferralSourceDescTxt());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertFalse(dto.isItDirty());
        assertTrue(dto.isItNew());
        assertFalse(dto.isItDelete());
    }

    @Test
    void testSpecialConstructor() {
        PatientEncounter entity = new PatientEncounter();
        entity.setPatientEncounterUid(1L);
        entity.setActivityDurationAmt("ActivityDurationAmt");
        entity.setActivityDurationUnitCd("ActivityDurationUnitCd");
        entity.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        entity.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        entity.setAcuityLevelCd("AcuityLevelCd");
        entity.setAcuityLevelDescTxt("AcuityLevelDescTxt");
        entity.setAddReasonCd("AddReasonCd");
        entity.setAddTime(new Timestamp(System.currentTimeMillis()));
        entity.setAddUserId(2L);
        entity.setAdmissionSourceCd("AdmissionSourceCd");
        entity.setAdmissionSourceDescTxt("AdmissionSourceDescTxt");
        entity.setBirthEncounterInd("BirthEncounterInd");
        entity.setCd("Cd");
        entity.setCdDescTxt("CdDescTxt");
        entity.setConfidentialityCd("ConfidentialityCd");
        entity.setConfidentialityDescTxt("ConfidentialityDescTxt");
        entity.setEffectiveDurationAmt("EffectiveDurationAmt");
        entity.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        entity.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        entity.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        entity.setLastChgReasonCd("LastChgReasonCd");
        entity.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entity.setLastChgUserId(3L);
        entity.setLocalId("LocalId");
        entity.setPriorityCd("PriorityCd");
        entity.setPriorityDescTxt("PriorityDescTxt");
        entity.setRecordStatusCd("RecordStatusCd");
        entity.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        entity.setReferralSourceCd("ReferralSourceCd");
        entity.setReferralSourceDescTxt("ReferralSourceDescTxt");
        entity.setRepeatNbr(4);
        entity.setStatusCd("StatusCd");
        entity.setStatusTime(new Timestamp(System.currentTimeMillis()));
        entity.setTxt("Txt");
        entity.setUserAffiliationTxt("UserAffiliationTxt");
        entity.setProgramJurisdictionOid(5L);
        entity.setSharedInd("SharedInd");
        entity.setVersionCtrlNbr(6);

        PatientEncounterDto dto = new PatientEncounterDto(entity);

        // Assert values
        assertEquals(1L, dto.getPatientEncounterUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AcuityLevelCd", dto.getAcuityLevelCd());
        assertEquals("AcuityLevelDescTxt", dto.getAcuityLevelDescTxt());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("AdmissionSourceCd", dto.getAdmissionSourceCd());
        assertEquals("AdmissionSourceDescTxt", dto.getAdmissionSourceDescTxt());
        assertEquals("BirthEncounterInd", dto.getBirthEncounterInd());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("PriorityCd", dto.getPriorityCd());
        assertEquals("PriorityDescTxt", dto.getPriorityDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("ReferralSourceCd", dto.getReferralSourceCd());
        assertEquals("ReferralSourceDescTxt", dto.getReferralSourceDescTxt());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
    }

    @Test
    void testOverriddenMethods() {
        PatientEncounterDto dto = new PatientEncounterDto();

        // Test overridden methods
        assertEquals(dto.getPatientEncounterUid(), dto.getUid());
        assertNull( dto.getSuperclass());
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getJurisdictionCd());
        assertNull(dto.getProgAreaCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLocalId());
        assertNull(dto.getAddUserId());
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getStatusCd());
        assertNull(dto.getStatusTime());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
