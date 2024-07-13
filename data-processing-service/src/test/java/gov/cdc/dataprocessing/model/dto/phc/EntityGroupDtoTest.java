package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.EntityGroup;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EntityGroupDtoTest {

    @Test
    void testGettersAndSetters() {
        EntityGroupDto dto = new EntityGroupDto();

        // Set values
        dto.setEntityGroupUid(1L);
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setDescription("Description");
        dto.setDurationAmt("DurationAmt");
        dto.setDurationUnitCd("DurationUnitCd");
        dto.setFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setGroupCnt(3);
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(4L);
        dto.setLocalId("LocalId");
        dto.setNm("Nm");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setToTime(new Timestamp(System.currentTimeMillis()));
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
        assertEquals(1L, dto.getEntityGroupUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNull(dto.getAddTime());
        assertNull( dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("Description", dto.getDescription());
        assertEquals("DurationAmt", dto.getDurationAmt());
        assertEquals("DurationUnitCd", dto.getDurationUnitCd());
        assertNotNull(dto.getFromTime());
        assertEquals(3, dto.getGroupCnt());
        assertNull( dto.getLastChgReasonCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLastChgUserId());
        assertNull( dto.getLocalId());
        assertEquals("Nm", dto.getNm());
        assertNull( dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNotNull(dto.getToTime());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertNull( dto.getVersionCtrlNbr());
        assertNull( dto.getProgAreaCd());
        assertNull( dto.getJurisdictionCd());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertFalse(dto.isItDirty());
        assertTrue(dto.isItNew());
        assertFalse(dto.isItDelete());
    }

    @Test
    void testSpecialConstructor() {
        EntityGroup entityGroup = new EntityGroup();
        entityGroup.setEntityGroupUid(1L);
        entityGroup.setAddReasonCd("AddReasonCd");
        entityGroup.setAddTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setAddUserId(2L);
        entityGroup.setCd("Cd");
        entityGroup.setCdDescTxt("CdDescTxt");
        entityGroup.setDescription("Description");
        entityGroup.setDurationAmt("DurationAmt");
        entityGroup.setDurationUnitCd("DurationUnitCd");
        entityGroup.setFromTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setGroupCnt(3);
        entityGroup.setLastChgReasonCd("LastChgReasonCd");
        entityGroup.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setLastChgUserId(4L);
        entityGroup.setLocalId("LocalId");
        entityGroup.setNm("Nm");
        entityGroup.setRecordStatusCd("RecordStatusCd");
        entityGroup.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setStatusCd("StatusCd");
        entityGroup.setStatusTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setToTime(new Timestamp(System.currentTimeMillis()));
        entityGroup.setUserAffiliationTxt("UserAffiliationTxt");
        entityGroup.setVersionCtrlNbr(5);

        EntityGroupDto dto = new EntityGroupDto(entityGroup);

        // Assert values
        assertEquals(1L, dto.getEntityGroupUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNull(dto.getAddTime());
        assertNull( dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("Description", dto.getDescription());
        assertEquals("DurationAmt", dto.getDurationAmt());
        assertEquals("DurationUnitCd", dto.getDurationUnitCd());
        assertNotNull(dto.getFromTime());
        assertEquals(3, dto.getGroupCnt());
        assertNull( dto.getLastChgReasonCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLastChgUserId());
        assertNull( dto.getLocalId());
        assertEquals("Nm", dto.getNm());
        assertNull( dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNotNull( dto.getStatusCd());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertNull(dto.getVersionCtrlNbr());
    }

    @Test
    void testOverriddenMethods() {
        EntityGroupDto dto = new EntityGroupDto();

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
        assertNull(dto.getSuperclass());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
