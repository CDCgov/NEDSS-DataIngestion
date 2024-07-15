package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.NonPersonLivingSubject;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NonPersonLivingSubjectDtoTest {

    @Test
    void testGettersAndSetters() {
        NonPersonLivingSubjectDto dto = new NonPersonLivingSubjectDto();

        // Set values
        dto.setNonPersonUid(1L);
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setBirthSexCd("BirthSexCd");
        dto.setBirthOrderNbr(3);
        dto.setBirthTime(new Timestamp(System.currentTimeMillis()));
        dto.setBreedCd("BreedCd");
        dto.setBreedDescTxt("BreedDescTxt");
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setDeceasedIndCd("DeceasedIndCd");
        dto.setDeceasedTime(new Timestamp(System.currentTimeMillis()));
        dto.setDescription("Description");
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(4L);
        dto.setLocalId("LocalId");
        dto.setMultipleBirthInd("MultipleBirthInd");
        dto.setNm("Nm");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTaxonomicClassificationCd("TaxonomicClassificationCd");
        dto.setTaxonomicClassificationDesc("TaxonomicClassificationDesc");
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setVersionCtrlNbr(5);
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setProgramJurisdictionOid(6L);
        dto.setSharedInd("SharedInd");
        dto.setItDirty(false);
        dto.setItNew(true);
        dto.setItDelete(false);

        // Assert values
        assertEquals(1L, dto.getNonPersonUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertNotNull(dto.getAddUserId());
        assertEquals("BirthSexCd", dto.getBirthSexCd());
        assertEquals(3, dto.getBirthOrderNbr());
        assertNotNull(dto.getBirthTime());
        assertEquals("BreedCd", dto.getBreedCd());
        assertEquals("BreedDescTxt", dto.getBreedDescTxt());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("DeceasedIndCd", dto.getDeceasedIndCd());
        assertNotNull(dto.getDeceasedTime());
        assertEquals("Description", dto.getDescription());
        assertNotNull(dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertNotNull(dto.getLastChgUserId());
        assertNotNull(dto.getLocalId());
        assertEquals("MultipleBirthInd", dto.getMultipleBirthInd());
        assertEquals("Nm", dto.getNm());
        assertNotNull(dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertNotNull(dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("TaxonomicClassificationCd", dto.getTaxonomicClassificationCd());
        assertEquals("TaxonomicClassificationDesc", dto.getTaxonomicClassificationDesc());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertNotNull(dto.getVersionCtrlNbr());
        assertNotNull(dto.getProgAreaCd());
        assertNotNull(dto.getJurisdictionCd());
        assertNotNull(dto.getProgramJurisdictionOid());
        assertNotNull(dto.getSharedInd());
        assertFalse(dto.isItDirty());
        assertTrue(dto.isItNew());
        assertFalse(dto.isItDelete());
    }

    @Test
    void testSpecialConstructor() {
        NonPersonLivingSubject entity = new NonPersonLivingSubject();
        entity.setNonPersonUid(1L);
        entity.setAddReasonCd("AddReasonCd");
        entity.setAddTime(new Timestamp(System.currentTimeMillis()));
        entity.setAddUserId(2L);
        entity.setBirthSexCd("BirthSexCd");
        entity.setBirthOrderNbr(3);
        entity.setBirthTime(new Timestamp(System.currentTimeMillis()));
        entity.setBreedCd("BreedCd");
        entity.setBreedDescTxt("BreedDescTxt");
        entity.setCd("Cd");
        entity.setCdDescTxt("CdDescTxt");
        entity.setDeceasedIndCd("DeceasedIndCd");
        entity.setDeceasedTime(new Timestamp(System.currentTimeMillis()));
        entity.setDescription("Description");
        entity.setLastChgReasonCd("LastChgReasonCd");
        entity.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entity.setLastChgUserId(4L);
        entity.setLocalId("LocalId");
        entity.setMultipleBirthInd("MultipleBirthInd");
        entity.setNm("Nm");
        entity.setRecordStatusCd("RecordStatusCd");
        entity.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        entity.setStatusCd("StatusCd");
        entity.setStatusTime(new Timestamp(System.currentTimeMillis()));
        entity.setTaxonomicClassificationCd("TaxonomicClassificationCd");
        entity.setTaxonomicClassificationDesc("TaxonomicClassificationDesc");
        entity.setUserAffiliationTxt("UserAffiliationTxt");
        entity.setVersionCtrlNbr(5);

        NonPersonLivingSubjectDto dto = new NonPersonLivingSubjectDto(entity);

        // Assert values
        assertEquals(1L, dto.getNonPersonUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertNotNull(dto.getAddUserId());
        assertEquals("BirthSexCd", dto.getBirthSexCd());
        assertEquals(3, dto.getBirthOrderNbr());
        assertNotNull(dto.getBirthTime());
        assertEquals("BreedCd", dto.getBreedCd());
        assertEquals("BreedDescTxt", dto.getBreedDescTxt());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("DeceasedIndCd", dto.getDeceasedIndCd());
        assertNotNull(dto.getDeceasedTime());
        assertEquals("Description", dto.getDescription());
        assertNotNull(dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertNotNull(dto.getLastChgUserId());
        assertEquals("MultipleBirthInd", dto.getMultipleBirthInd());
        assertEquals("Nm", dto.getNm());

    }

    @Test
    void testOverriddenMethods() {
        NonPersonLivingSubjectDto dto = new NonPersonLivingSubjectDto();

        // Test overridden methods that return null
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
        assertNotNull(dto.getSuperclass());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
