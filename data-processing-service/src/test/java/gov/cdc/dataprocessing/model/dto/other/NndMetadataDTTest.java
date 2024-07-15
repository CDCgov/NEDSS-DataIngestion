package gov.cdc.dataprocessing.model.dto.other;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NndMetadataDTTest {

    private NndMetadataDT nndMetadataDT;

    @BeforeEach
    void setUp() {
        nndMetadataDT = new NndMetadataDT();
    }

    @Test
    void testSettersAndGetters() {
        Long id = 12345L;
        String formCd = "INV_FORM";
        String questionId = "Q123";
        String label = "Question Label";
        String required = "Yes";
        String dataType = "String";
        String segmentField = "SEG1";
        String orderGroupId = "ORD1";
        String translationTableNm = "Table1";
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        nndMetadataDT.setNndMetadataUid(id);
        nndMetadataDT.setInvestigationFormCd(formCd);
        nndMetadataDT.setQuestionIdentifierNnd(questionId);
        nndMetadataDT.setQuestionLabelNnd(label);
        nndMetadataDT.setQuestionRequiredNnd(required);
        nndMetadataDT.setQuestionDataTypeNnd(dataType);
        nndMetadataDT.setHL7SegmentField(segmentField);
        nndMetadataDT.setOrderGroupId(orderGroupId);
        nndMetadataDT.setTranslationTableNm(translationTableNm);
        nndMetadataDT.setAddTime(currentTime);
        nndMetadataDT.setAddUserId(id);
        nndMetadataDT.setLastChgTime(currentTime);
        nndMetadataDT.setLastChgUserId(id);
        nndMetadataDT.setRecordStatusCd("Active");
        nndMetadataDT.setRecordStatusTime(currentTime);
        nndMetadataDT.setQuestionIdentifier(questionId);
        nndMetadataDT.setMsgTriggerIndCd("Trigger");
        nndMetadataDT.setXmlPath("Path");
        nndMetadataDT.setXmlTag("Tag");
        nndMetadataDT.setXmlDataType(dataType);
        nndMetadataDT.setPartTypeCd("PartType");
        nndMetadataDT.setRepeatGroupSeqNbr(1);
        nndMetadataDT.setQuestionOrderNnd(1);
        nndMetadataDT.setNbsPageUid(id);
        nndMetadataDT.setNbsUiMetadataUid(id);
        nndMetadataDT.setQuestionMap("Map");
        nndMetadataDT.setIndicatorCd("Indicator");

        assertEquals(id, nndMetadataDT.getNndMetadataUid());
        assertEquals(formCd, nndMetadataDT.getInvestigationFormCd());
        assertEquals(questionId, nndMetadataDT.getQuestionIdentifierNnd());
        assertEquals(label, nndMetadataDT.getQuestionLabelNnd());
        assertEquals(required, nndMetadataDT.getQuestionRequiredNnd());
        assertEquals(dataType, nndMetadataDT.getQuestionDataTypeNnd());
        assertEquals(segmentField, nndMetadataDT.getHL7SegmentField());
        assertEquals(orderGroupId, nndMetadataDT.getOrderGroupId());
        assertEquals(translationTableNm, nndMetadataDT.getTranslationTableNm());
        assertEquals(currentTime, nndMetadataDT.getAddTime());
        assertEquals(id, nndMetadataDT.getAddUserId());
        assertEquals(currentTime, nndMetadataDT.getLastChgTime());
        assertEquals(id, nndMetadataDT.getLastChgUserId());
        assertEquals("Active", nndMetadataDT.getRecordStatusCd());
        assertEquals(currentTime, nndMetadataDT.getRecordStatusTime());
        assertEquals(questionId, nndMetadataDT.getQuestionIdentifier());
        assertEquals("Trigger", nndMetadataDT.getMsgTriggerIndCd());
        assertEquals("Path", nndMetadataDT.getXmlPath());
        assertEquals("Tag", nndMetadataDT.getXmlTag());
        assertEquals(dataType, nndMetadataDT.getXmlDataType());
        assertEquals("PartType", nndMetadataDT.getPartTypeCd());
        assertEquals(1, nndMetadataDT.getRepeatGroupSeqNbr());
        assertEquals(1, nndMetadataDT.getQuestionOrderNnd());
        assertEquals(id, nndMetadataDT.getNbsPageUid());
        assertEquals(id, nndMetadataDT.getNbsUiMetadataUid());
        assertEquals("Map", nndMetadataDT.getQuestionMap());
        assertEquals("Indicator", nndMetadataDT.getIndicatorCd());
    }

    @Test
    void testUnimplementedMethods() {
        assertNull(nndMetadataDT.getJurisdictionCd());
        assertNull(nndMetadataDT.getLastChgReasonCd());
        assertNull(nndMetadataDT.getLocalId());
        assertNull(nndMetadataDT.getProgAreaCd());
        assertNull(nndMetadataDT.getProgramJurisdictionOid());
        assertNull(nndMetadataDT.getSharedInd());
        assertNull(nndMetadataDT.getStatusCd());
        assertNull(nndMetadataDT.getStatusTime());
        assertNull(nndMetadataDT.getSuperclass());
        assertNull(nndMetadataDT.getUid());
        assertNull(nndMetadataDT.getVersionCtrlNbr());
        assertFalse(nndMetadataDT.isItDelete());
        assertFalse(nndMetadataDT.isItDirty());
        assertFalse(nndMetadataDT.isItNew());

        nndMetadataDT.setItDelete(true);
        nndMetadataDT.setItDirty(true);
        nndMetadataDT.setItNew(true);
        nndMetadataDT.setJurisdictionCd("Jurisdiction");
        nndMetadataDT.setLastChgReasonCd("Reason");
        nndMetadataDT.setLocalId("LocalId");
        nndMetadataDT.setProgAreaCd("ProgArea");
        nndMetadataDT.setProgramJurisdictionOid(123L);
        nndMetadataDT.setSharedInd("Shared");
        nndMetadataDT.setStatusCd("Status");
        nndMetadataDT.setStatusTime(new Timestamp(System.currentTimeMillis()));

        assertFalse(nndMetadataDT.isItDelete());
        assertFalse(nndMetadataDT.isItDirty());
        assertFalse(nndMetadataDT.isItNew());
    }
}
