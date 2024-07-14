package gov.cdc.dataprocessing.model.dto.phc;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CTContactSummaryDtoTest {

    @Test
    void testGettersAndSetters() {
        CTContactSummaryDto dto = new CTContactSummaryDto();

        // Set values
        dto.setCtContactUid(1L);
        dto.setContactMprUid(2L);
        dto.setSubjectMprUid(3L);
        dto.setNamedOnDate(new Timestamp(System.currentTimeMillis()));
        dto.setLocalId("LocalId");
        dto.setSubjectEntityUid(4L);
        dto.setContactEntityUid(5L);
        dto.setNamedBy("NamedBy");
        dto.setName("Name");
        dto.setContactNamedByPatient(true);
        dto.setPatientNamedByContact(false);
        dto.setOtherNamedByPatient(true);
        dto.setPriorityCd("PriorityCd");
        dto.setDispositionCd("DispositionCd");
        dto.setPriority("Priority");
        dto.setDisposition("Disposition");
        dto.setInvDisposition("InvDisposition");
        dto.setInvDispositionCd("InvDispositionCd");
        dto.setSubjectEntityPhcUid(6L);
        dto.setSubjectPhcLocalId("SubjectPhcLocalId");
        dto.setContactEntityPhcUid(7L);
        dto.setContactPhcLocalId("ContactPhcLocalId");
        dto.setSubjectPhcCd("SubjectPhcCd");
        dto.setAgeReported("AgeReported");
        dto.setAgeReportedUnitCd("AgeReportedUnitCd");
        dto.setBirthTime(new Timestamp(System.currentTimeMillis()));
        dto.setCurrSexCd("CurrSexCd");
        dto.setRelationshipCd("RelationshipCd");
        dto.setConditionCd("ConditionCd");
        dto.setAgeDOBSex("AgeDOBSex");
        dto.setDescription("Description");
        dto.setAssociatedWith("AssociatedWith");
        dto.setCreateDate(new Timestamp(System.currentTimeMillis()));
        dto.setContactReferralBasisCd("ContactReferralBasisCd");
        dto.setNamedDuringInterviewUid(8L);
        dto.setThirdPartyEntityPhcUid(9L);
        dto.setThirdPartyEntityUid(10L);
        dto.setContactProcessingDecisionCd("ContactProcessingDecisionCd");
        dto.setContactProcessingDecision("ContactProcessingDecision");
        dto.setSubjectName("SubjectName");
        dto.setContactName("ContactName");
        dto.setOtherInfectedPatientName("OtherInfectedPatientName");
        dto.setSourceDispositionCd("SourceDispositionCd");
        dto.setSourceCurrentSexCd("SourceCurrentSexCd");
        dto.setSourceInterviewStatusCd("SourceInterviewStatusCd");
        dto.setSourceConditionCd("SourceConditionCd");
        dto.setProgAreaCd("ProgAreaCd");
        dto.setInterviewDate(new Timestamp(System.currentTimeMillis()));
        Map<Object, Object> associatedMap = new HashMap<>();
        associatedMap.put("key", "value");
        dto.setAssociatedMap(associatedMap);

        // Assert values
        assertEquals(1L, dto.getCtContactUid());
        assertEquals(2L, dto.getContactMprUid());
        assertEquals(3L, dto.getSubjectMprUid());
        assertNotNull(dto.getNamedOnDate());
        assertNull(dto.getLocalId());
        assertEquals(4L, dto.getSubjectEntityUid());
        assertEquals(5L, dto.getContactEntityUid());
        assertEquals("NamedBy", dto.getNamedBy());
        assertEquals("Name", dto.getName());
        assertTrue(dto.isContactNamedByPatient());
        assertFalse(dto.isPatientNamedByContact());
        assertTrue(dto.isOtherNamedByPatient());
        assertEquals("PriorityCd", dto.getPriorityCd());
        assertEquals("DispositionCd", dto.getDispositionCd());
        assertEquals("Priority", dto.getPriority());
        assertEquals("Disposition", dto.getDisposition());
        assertEquals("InvDisposition", dto.getInvDisposition());
        assertEquals("InvDispositionCd", dto.getInvDispositionCd());
        assertEquals(6L, dto.getSubjectEntityPhcUid());
        assertEquals("SubjectPhcLocalId", dto.getSubjectPhcLocalId());
        assertEquals(7L, dto.getContactEntityPhcUid());
        assertEquals("ContactPhcLocalId", dto.getContactPhcLocalId());
        assertEquals("SubjectPhcCd", dto.getSubjectPhcCd());
        assertEquals("AgeReported", dto.getAgeReported());
        assertEquals("AgeReportedUnitCd", dto.getAgeReportedUnitCd());
        assertNotNull(dto.getBirthTime());
        assertEquals("CurrSexCd", dto.getCurrSexCd());
        assertEquals("RelationshipCd", dto.getRelationshipCd());
        assertEquals("ConditionCd", dto.getConditionCd());
        assertEquals("AgeDOBSex", dto.getAgeDOBSex());
        assertEquals("Description", dto.getDescription());
        assertEquals("AssociatedWith", dto.getAssociatedWith());
        assertNotNull(dto.getCreateDate());
        assertEquals("ContactReferralBasisCd", dto.getContactReferralBasisCd());
        assertEquals(8L, dto.getNamedDuringInterviewUid());
        assertEquals(9L, dto.getThirdPartyEntityPhcUid());
        assertEquals(10L, dto.getThirdPartyEntityUid());
        assertEquals("ContactProcessingDecisionCd", dto.getContactProcessingDecisionCd());
        assertEquals("ContactProcessingDecision", dto.getContactProcessingDecision());
        assertEquals("SubjectName", dto.getSubjectName());
        assertEquals("ContactName", dto.getContactName());
        assertEquals("OtherInfectedPatientName", dto.getOtherInfectedPatientName());
        assertEquals("SourceDispositionCd", dto.getSourceDispositionCd());
        assertEquals("SourceCurrentSexCd", dto.getSourceCurrentSexCd());
        assertEquals("SourceInterviewStatusCd", dto.getSourceInterviewStatusCd());
        assertEquals("SourceConditionCd", dto.getSourceConditionCd());
        assertNull(dto.getProgAreaCd());
        assertNotNull(dto.getInterviewDate());
        assertEquals(associatedMap, dto.getAssociatedMap());
    }

    @Test
    void testOverriddenMethods() {
        CTContactSummaryDto dto = new CTContactSummaryDto();

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
