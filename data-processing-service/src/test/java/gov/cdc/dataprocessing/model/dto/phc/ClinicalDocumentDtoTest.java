package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ClinicalDocument;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ClinicalDocumentDtoTest {

    @Test
    void testGettersAndSetters() {
        ClinicalDocumentDto dto = new ClinicalDocumentDto();

        // Set values
        dto.setClinicalDocumentUid(1L);
        dto.setActivityDurationAmt("ActivityDurationAmt");
        dto.setActivityDurationUnitCd("ActivityDurationUnitCd");
        dto.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setConfidentialityCd("ConfidentialityCd");
        dto.setConfidentialityDescTxt("ConfidentialityDescTxt");
        dto.setCopyFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setCopyToTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveDurationAmt("EffectiveDurationAmt");
        dto.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        dto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setPracticeSettingCd("PracticeSettingCd");
        dto.setPracticeSettingDescTxt("PracticeSettingDescTxt");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTxt("Txt");
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setVersionNbr(4);
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(6);
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setItDirty(false);
        dto.setItNew(true);
        dto.setItDelete(false);

        // Assert values
        assertEquals(1L, dto.getClinicalDocumentUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertNotNull(dto.getCopyFromTime());
        assertNotNull(dto.getCopyToTime());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("PracticeSettingCd", dto.getPracticeSettingCd());
        assertEquals("PracticeSettingDescTxt", dto.getPracticeSettingDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(4, dto.getVersionNbr());
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
        ClinicalDocument clinicalDocument = new ClinicalDocument();
        clinicalDocument.setClinicalDocumentUid(1L);
        clinicalDocument.setActivityDurationAmt("ActivityDurationAmt");
        clinicalDocument.setActivityDurationUnitCd("ActivityDurationUnitCd");
        clinicalDocument.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setAddReasonCd("AddReasonCd");
        clinicalDocument.setAddTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setAddUserId(2L);
        clinicalDocument.setCd("Cd");
        clinicalDocument.setCdDescTxt("CdDescTxt");
        clinicalDocument.setConfidentialityCd("ConfidentialityCd");
        clinicalDocument.setConfidentialityDescTxt("ConfidentialityDescTxt");
        clinicalDocument.setCopyFromTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setCopyToTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setEffectiveDurationAmt("EffectiveDurationAmt");
        clinicalDocument.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        clinicalDocument.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setLastChgReasonCd("LastChgReasonCd");
        clinicalDocument.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setLastChgUserId(3L);
        clinicalDocument.setLocalId("LocalId");
        clinicalDocument.setPracticeSettingCd("PracticeSettingCd");
        clinicalDocument.setPracticeSettingDescTxt("PracticeSettingDescTxt");
        clinicalDocument.setRecordStatusCd("RecordStatusCd");
        clinicalDocument.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setStatusCd("StatusCd");
        clinicalDocument.setStatusTime(new Timestamp(System.currentTimeMillis()));
        clinicalDocument.setTxt("Txt");
        clinicalDocument.setUserAffiliationTxt("UserAffiliationTxt");
        clinicalDocument.setVersionNbr(4);
        clinicalDocument.setProgramJurisdictionOid(5L);
        clinicalDocument.setSharedInd("SharedInd");
        clinicalDocument.setVersionCtrlNbr(6);

        ClinicalDocumentDto dto = new ClinicalDocumentDto(clinicalDocument);

        // Assert values
        assertEquals(1L, dto.getClinicalDocumentUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertNotNull(dto.getCopyFromTime());
        assertNotNull(dto.getCopyToTime());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("PracticeSettingCd", dto.getPracticeSettingCd());
        assertEquals("PracticeSettingDescTxt", dto.getPracticeSettingDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(4, dto.getVersionNbr());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
    }

    @Test
    void testOverriddenMethods() {
        ClinicalDocumentDto dto = new ClinicalDocumentDto();

        // Test overridden methods
        assertNull(dto.getSuperclass());
        assertNull(dto.getUid());
    }
}
