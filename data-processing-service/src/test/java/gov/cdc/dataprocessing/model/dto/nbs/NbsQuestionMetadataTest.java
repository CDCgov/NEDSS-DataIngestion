package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import gov.cdc.dataprocessing.service.model.lookup_data.MetaAndWaCommonAttribute;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsQuestionMetadataTest {

    @Test
    void testGettersAndSetters() {
        NbsQuestionMetadata dto = new NbsQuestionMetadata();

        // Set values
        dto.setNbsQuestionUid(1L);
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCodeSetGroupId(3L);
        dto.setDataType("DataType");
        dto.setInvestigationFormCd("InvestigationFormCd");
        dto.setTemplateType("TemplateType");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(4L);
        dto.setOrderNbr(5);
        dto.setQuestionLabel("QuestionLabel");
        dto.setQuestionToolTip("QuestionToolTip");
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTabId(6);
        dto.setQuestionVersionNbr(7);
        dto.setNndMetadataUid(8L);
        dto.setQuestionIdentifier("QuestionIdentifier");
        dto.setQuestionIdentifierNnd("QuestionIdentifierNnd");
        dto.setQuestionRequiredNnd("QuestionRequiredNnd");
        dto.setQuestionOid("QuestionOid");
        dto.setQuestionOidSystemTxt("QuestionOidSystemTxt");
        dto.setCodeSetNm("CodeSetNm");
        dto.setCodeSetClassCd("CodeSetClassCd");
        dto.setDataLocation("DataLocation");
        dto.setDataCd("DataCd");
        dto.setDataUseCd("DataUseCd");
        dto.setEnableInd("EnableInd");
        dto.setDefaultValue("DefaultValue");
        dto.setRequiredInd("RequiredInd");
        dto.setParentUid(9L);
        dto.setLdfPageId("LdfPageId");
        dto.setNbsUiMetadataUid(10L);
        dto.setNbsUiComponentUid(11L);
        dto.setNbsTableUid(12L);
        dto.setFieldSize("FieldSize");
        dto.setFutureDateInd("FutureDateInd");
        dto.setDisplayInd("DisplayInd");
        dto.setJspSnippetCreateEdit("JspSnippetCreateEdit");
        dto.setJspSnippetView("JspSnippetView");
        dto.setUnitTypeCd("UnitTypeCd");
        dto.setUnitValue("UnitValue");
        dto.setStandardNndIndCd("StandardNndIndCd");
        dto.setHl7SegmentField("Hl7SegmentField");
        dto.setQuestionGroupSeqNbr(13);
        dto.setPartTypeCd("PartTypeCd");
        dto.setQuestionUnitIdentifier("QuestionUnitIdentifier");
        dto.setMask("Mask");
        dto.setSubGroupNm("SubGroupNm");
        dto.setCoinfectionIndCd("CoinfectionIndCd");

        // Assert values
        assertEquals(1L, dto.getNbsQuestionUid());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals(3L, dto.getCodeSetGroupId());
        assertEquals("DataType", dto.getDataType());
        assertEquals("InvestigationFormCd", dto.getInvestigationFormCd());
        assertEquals("TemplateType", dto.getTemplateType());
        assertNotNull(dto.getLastChgTime());
        assertEquals(4L, dto.getLastChgUserId());
        assertEquals(5, dto.getOrderNbr());
        assertEquals("QuestionLabel", dto.getQuestionLabel());
        assertEquals("QuestionToolTip", dto.getQuestionToolTip());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals(6, dto.getTabId());
        assertEquals(7, dto.getQuestionVersionNbr());
        assertEquals(8L, dto.getNndMetadataUid());
        assertEquals("QuestionIdentifier", dto.getQuestionIdentifier());
        assertEquals("QuestionIdentifierNnd", dto.getQuestionIdentifierNnd());
        assertEquals("QuestionRequiredNnd", dto.getQuestionRequiredNnd());
        assertEquals("QuestionOid", dto.getQuestionOid());
        assertEquals("QuestionOidSystemTxt", dto.getQuestionOidSystemTxt());
        assertEquals("CodeSetNm", dto.getCodeSetNm());
        assertEquals("CodeSetClassCd", dto.getCodeSetClassCd());
        assertEquals("DataLocation", dto.getDataLocation());
        assertEquals("DataCd", dto.getDataCd());
        assertEquals("DataUseCd", dto.getDataUseCd());
        assertEquals("EnableInd", dto.getEnableInd());
        assertEquals("DefaultValue", dto.getDefaultValue());
        assertEquals("RequiredInd", dto.getRequiredInd());
        assertEquals(9L, dto.getParentUid());
        assertEquals("LdfPageId", dto.getLdfPageId());
        assertEquals(10L, dto.getNbsUiMetadataUid());
        assertEquals(11L, dto.getNbsUiComponentUid());
        assertEquals(12L, dto.getNbsTableUid());
        assertEquals("FieldSize", dto.getFieldSize());
        assertEquals("FutureDateInd", dto.getFutureDateInd());
        assertEquals("DisplayInd", dto.getDisplayInd());
        assertEquals("JspSnippetCreateEdit", dto.getJspSnippetCreateEdit());
        assertEquals("JspSnippetView", dto.getJspSnippetView());
        assertEquals("UnitTypeCd", dto.getUnitTypeCd());
        assertEquals("UnitValue", dto.getUnitValue());
        assertEquals("StandardNndIndCd", dto.getStandardNndIndCd());
        assertEquals("Hl7SegmentField", dto.getHl7SegmentField());
        assertEquals(13, dto.getQuestionGroupSeqNbr());
        assertEquals("PartTypeCd", dto.getPartTypeCd());
        assertEquals("QuestionUnitIdentifier", dto.getQuestionUnitIdentifier());
        assertEquals("Mask", dto.getMask());
        assertEquals("SubGroupNm", dto.getSubGroupNm());
        assertEquals("CoinfectionIndCd", dto.getCoinfectionIndCd());
    }

    @Test
    void testSpecialConstructorMetaAndWaCommonAttribute() {
        MetaAndWaCommonAttribute commonAttributes = new MetaAndWaCommonAttribute();
        commonAttributes.setDataLocation("DataLocation");
        commonAttributes.setQuestionUid(1L);
        commonAttributes.setAddTime(new Timestamp(System.currentTimeMillis()));
        commonAttributes.setAddUserId(2L);
        commonAttributes.setCodeSetGroupId(3L);
        commonAttributes.setDataType("DataType");
        commonAttributes.setInvestigationFormCd("InvestigationFormCd");
        commonAttributes.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        commonAttributes.setLastChgUserId(4L);
        commonAttributes.setOrderNbr(5);
        commonAttributes.setQuestionLabel("QuestionLabel");
        commonAttributes.setQuestionToolTip("QuestionToolTip");
        commonAttributes.setQuestionIdentifier("QuestionIdentifier");
        commonAttributes.setQuestionIdentifierNnd("QuestionIdentifierNnd");
        commonAttributes.setQuestionRequiredNnd("QuestionRequiredNnd");
        commonAttributes.setQuestionOid("QuestionOid");
        commonAttributes.setQuestionOidSystemTxt("QuestionOidSystemTxt");
        commonAttributes.setCodeSetNm("CodeSetNm");
        commonAttributes.setCodeSetClassCd("CodeSetClassCd");
        commonAttributes.setDataCd("DataCd");
        commonAttributes.setDataUseCd("DataUseCd");
        commonAttributes.setEnableInd("EnableInd");
        commonAttributes.setDefaultValue("DefaultValue");
        commonAttributes.setRequiredInd("RequiredInd");
        commonAttributes.setParentUid(6L);
        commonAttributes.setLdfPageId("LdfPageId");
        commonAttributes.setNbsUiComponentUid(7L);
        commonAttributes.setFieldSize("FieldSize");
        commonAttributes.setDisplayInd("DisplayInd");
        commonAttributes.setUnitTypeCd("UnitTypeCd");
        commonAttributes.setUnitValue("UnitValue");
        commonAttributes.setStandardNndIndCd("StandardNndIndCd");
        commonAttributes.setHl7SegmentField("Hl7SegmentField");
        commonAttributes.setQuestionGroupSeqNbr(8);
        commonAttributes.setPartTypeCd("PartTypeCd");
        commonAttributes.setQuestionUnitIdentifier("QuestionUnitIdentifier");
        commonAttributes.setMask("Mask");
        commonAttributes.setSubGroupNm("SubGroupNm");
        commonAttributes.setCoinfectionIndCd("CoinfectionIndCd");

        NbsQuestionMetadata dto = new NbsQuestionMetadata(commonAttributes);

        // Assert values
        assertEquals(1L, dto.getNbsQuestionUid());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals(3L, dto.getCodeSetGroupId());
        assertEquals("DataType", dto.getDataType());
        assertEquals("InvestigationFormCd", dto.getInvestigationFormCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(4L, dto.getLastChgUserId());
        assertEquals(5, dto.getOrderNbr());
        assertEquals("QuestionLabel", dto.getQuestionLabel());
        assertEquals("QuestionToolTip", dto.getQuestionToolTip());
        assertEquals("QuestionIdentifier", dto.getQuestionIdentifier());
        assertEquals("QuestionIdentifierNnd", dto.getQuestionIdentifierNnd());
        assertEquals("QuestionRequiredNnd", dto.getQuestionRequiredNnd());
        assertEquals("QuestionOid", dto.getQuestionOid());
        assertEquals("QuestionOidSystemTxt", dto.getQuestionOidSystemTxt());
        assertEquals("CodeSetNm", dto.getCodeSetNm());
        assertEquals("CodeSetClassCd", dto.getCodeSetClassCd());
        assertEquals("DataLocation", dto.getDataLocation());
        assertEquals("DataCd", dto.getDataCd());
        assertEquals("DataUseCd", dto.getDataUseCd());
        assertEquals("EnableInd", dto.getEnableInd());
        assertEquals("DefaultValue", dto.getDefaultValue());
        assertEquals("RequiredInd", dto.getRequiredInd());
        assertEquals(6L, dto.getParentUid());
        assertEquals("LdfPageId", dto.getLdfPageId());
        assertEquals(7L, dto.getNbsUiComponentUid());
        assertEquals("FieldSize", dto.getFieldSize());
        assertEquals("DisplayInd", dto.getDisplayInd());
        assertEquals("UnitTypeCd", dto.getUnitTypeCd());
        assertEquals("UnitValue", dto.getUnitValue());
        assertEquals("StandardNndIndCd", dto.getStandardNndIndCd());
        assertEquals("Hl7SegmentField", dto.getHl7SegmentField());
        assertEquals(8, dto.getQuestionGroupSeqNbr());
        assertEquals("PartTypeCd", dto.getPartTypeCd());
        assertEquals("QuestionUnitIdentifier", dto.getQuestionUnitIdentifier());
        assertEquals("Mask", dto.getMask());
        assertEquals("SubGroupNm", dto.getSubGroupNm());
        assertEquals("CoinfectionIndCd", dto.getCoinfectionIndCd());
    }

    @Test
    void testSpecialConstructorQuestionRequiredNnd() {
        QuestionRequiredNnd data = new QuestionRequiredNnd();
        data.setNbsQuestionUid(1L);
        data.setQuestionIdentifier("QuestionIdentifier");
        data.setQuestionLabel("QuestionLabel");
        data.setDataLocation("DataLocation");

        NbsQuestionMetadata dto = new NbsQuestionMetadata(data);

        // Assert values
        assertEquals(1L, dto.getNbsQuestionUid());
        assertEquals("QuestionIdentifier", dto.getQuestionIdentifier());
        assertEquals("QuestionLabel", dto.getQuestionLabel());
        assertEquals("DataLocation", dto.getDataLocation());
    }
}
