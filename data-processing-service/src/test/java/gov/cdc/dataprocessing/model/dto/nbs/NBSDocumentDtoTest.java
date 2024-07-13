package gov.cdc.dataprocessing.model.dto.nbs;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NBSDocumentDtoTest {

    @Test
    void testGettersAndSetters() {
        NBSDocumentDto dto = new NBSDocumentDto();

        // Set values
        dto.setNbsquestionuid(1L);
        dto.setInvFormCode("InvFormCode");
        dto.setQuestionIdentifier("QuestionIdentifier");
        dto.setQuestionLabel("QuestionLabel");
        dto.setCodeSetName("CodeSetName");
        dto.setDataType("DataType");
        dto.setNbsDocumentUid(2L);
        dto.setDocPayload("DocPayload");
        dto.setPhdcDocDerived("PhdcDocDerived");
        dto.setPayloadViewIndCd("PayloadViewIndCd");
        dto.setDocTypeCd("DocTypeCd");
        dto.setNbsDocumentMetadataUid(3L);
        dto.setLocalId("LocalId");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(4L);
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setTxt("Txt");
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(1);
        dto.setCd("Cd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(6L);
        dto.setDocPurposeCd("DocPurposeCd");
        dto.setDocStatusCd("DocStatusCd");
        dto.setPayLoadTxt("PayLoadTxt");
        dto.setPhdcDocDerivedTxt("PhdcDocDerivedTxt");
        dto.setCdDescTxt("CdDescTxt");
        dto.setSendingFacilityNm("SendingFacilityNm");
        dto.setSendingFacilityOID("SendingFacilityOID");
        dto.setNbsInterfaceUid(7L);
        dto.setSendingAppPatientId("SendingAppPatientId");
        dto.setSendingAppEventId("SendingAppEventId");
        dto.setSuperclass("Superclass");
        dto.setXmldocPayload("XmldocPayload");
        dto.setExternalVersionCtrlNbr(2);
        Map<Object, Object> eventIdMap = new HashMap<>();
        eventIdMap.put("key", "value");
        dto.setEventIdMap(eventIdMap);
        dto.setDocumentObject(new Object());
        dto.setDocEventTypeCd("DocEventTypeCd");
        dto.setProcessingDecisionCd("ProcessingDecisionCd");
        dto.setProcessingDecisiontxt("ProcessingDecisiontxt");
        dto.setEffectiveTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals(1L, dto.getNbsquestionuid());
        assertEquals("InvFormCode", dto.getInvFormCode());
        assertEquals("QuestionIdentifier", dto.getQuestionIdentifier());
        assertEquals("QuestionLabel", dto.getQuestionLabel());
        assertEquals("CodeSetName", dto.getCodeSetName());
        assertEquals("DataType", dto.getDataType());
        assertEquals(2L, dto.getNbsDocumentUid());
        assertEquals("DocPayload", dto.getDocPayload());
        assertEquals("PhdcDocDerived", dto.getPhdcDocDerived());
        assertEquals("PayloadViewIndCd", dto.getPayloadViewIndCd());
        assertEquals("DocTypeCd", dto.getDocTypeCd());
        assertEquals(3L, dto.getNbsDocumentMetadataUid());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals(4L, dto.getAddUserId());
        assertNotNull(dto.getAddTime());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals("Txt", dto.getTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(1, dto.getVersionCtrlNbr());
        assertEquals("Cd", dto.getCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(6L, dto.getLastChgUserId());
        assertEquals("DocPurposeCd", dto.getDocPurposeCd());
        assertEquals("DocStatusCd", dto.getDocStatusCd());
        assertEquals("PayLoadTxt", dto.getPayLoadTxt());
        assertEquals("PhdcDocDerivedTxt", dto.getPhdcDocDerivedTxt());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("SendingFacilityNm", dto.getSendingFacilityNm());
        assertEquals("SendingFacilityOID", dto.getSendingFacilityOID());
        assertEquals(7L, dto.getNbsInterfaceUid());
        assertEquals("SendingAppPatientId", dto.getSendingAppPatientId());
        assertEquals("SendingAppEventId", dto.getSendingAppEventId());
        assertEquals("Superclass", dto.getSuperclass());
        assertEquals("XmldocPayload", dto.getXmldocPayload());
        assertEquals(2, dto.getExternalVersionCtrlNbr());
        assertEquals(eventIdMap, dto.getEventIdMap());
        assertNotNull(dto.getDocumentObject());
        assertEquals("DocEventTypeCd", dto.getDocEventTypeCd());
        assertEquals("ProcessingDecisionCd", dto.getProcessingDecisionCd());
        assertEquals("ProcessingDecisiontxt", dto.getProcessingDecisiontxt());
        assertNotNull(dto.getEffectiveTime());
    }

    @Test
    void testOverriddenMethods() {
        NBSDocumentDto dto = new NBSDocumentDto();
        dto.setLastChgReasonCd("TEST");
        dto.setStatusTime(null);
        // Test overridden methods that return null
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getStatusCd());
        assertNull(dto.getStatusTime());
        assertNull(dto.getUid());

        // Test setting and getting statusCd
        dto.setStatusCd("StatusCd");
        assertNull( dto.getStatusCd());
    }
}
