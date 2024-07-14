package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.question.QuestionMetadata;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMetadataTest {

    @Test
    void testConstructorWithObjectArray() {
        // Arrange
        Object[] data = new Object[44];
        data[0] = 1.0;
        data[1] = "2024-07-14 10:10:10.0";
        data[2] = 2.0;
        data[3] = "codeSetGroupId";
        data[4] = "dataType";
        data[5] = "mask";
        data[6] = "investigationFormCd";
        data[7] = "2024-07-14 10:10:10.0";
        data[8] = 3.0;
        data[9] = "questionLabel";
        data[10] = "questionToolTip";
        data[11] = 1;
        data[12] = "tabId";
        data[13] = true;
        data[14] = 10;
        data[15] = "defaultValue";
        data[16] = true;
        data[17] = true;
        data[18] = "coinfectionIndCd";
        data[19] = "nndMetadataUid";
        data[20] = "questionIdentifier";
        data[21] = "questionIdentifierNnd";
        data[22] = "questionRequiredNnd";
        data[23] = "questionOid";
        data[24] = "questionOidSystemTxt";
        data[25] = "codeSetNm";
        data[26] = "codeSetClassCd";
        data[27] = "dataLocation";
        data[28] = "dataCd";
        data[29] = "dataUseCd";
        data[30] = 100;
        data[31] = "parentUid";
        data[32] = "ldfPageId";
        data[33] = 4L;
        data[34] = 5L;
        data[35] = "unitTypeCd";
        data[36] = "unitValue";
        data[37] = "nbsTableUid";
        data[38] = "partTypeCd";
        data[39] = "standardNndIndCd";
        data[40] = "subGroupNm";
        data[41] = "hl7SegmentField";
        data[42] = 20;
        data[43] = "questionUnitIdentifier";

        // Act
        QuestionMetadata questionMetadata = new QuestionMetadata(data);

        // Assert
        assertEquals(1L, questionMetadata.getNbsQuestionUid());
        assertEquals(Timestamp.valueOf("2024-07-14 10:10:10.0"), questionMetadata.getAddTime());
        assertEquals(2L, questionMetadata.getAddUserId());
        assertEquals("codeSetGroupId", questionMetadata.getCodeSetGroupId());
        assertEquals("dataType", questionMetadata.getDataType());
        assertEquals("mask", questionMetadata.getMask());
        assertEquals("investigationFormCd", questionMetadata.getInvestigationFormCd());
        assertEquals(Timestamp.valueOf("2024-07-14 10:10:10.0"), questionMetadata.getLastChgTime());
        assertEquals(3L, questionMetadata.getLastChgUserId());
        assertEquals("questionLabel", questionMetadata.getQuestionLabel());
        assertEquals("questionToolTip", questionMetadata.getQuestionToolTip());
        assertEquals(1, questionMetadata.getQuestionVersionNbr());
        assertEquals("tabId", questionMetadata.getTabId());
        assertTrue(questionMetadata.isEnableInd());
        assertEquals(10, questionMetadata.getOrderNbr());
        assertEquals("defaultValue", questionMetadata.getDefaultValue());
        assertTrue(questionMetadata.isRequiredInd());
        assertTrue(questionMetadata.isDisplayInd());
        assertEquals("coinfectionIndCd", questionMetadata.getCoinfectionIndCd());
        assertEquals("nndMetadataUid", questionMetadata.getNndMetadataUid());
        assertEquals("questionIdentifier", questionMetadata.getQuestionIdentifier());
        assertEquals("questionIdentifierNnd", questionMetadata.getQuestionIdentifierNnd());
        assertEquals("questionRequiredNnd", questionMetadata.getQuestionRequiredNnd());
        assertEquals("questionOid", questionMetadata.getQuestionOid());
        assertEquals("questionOidSystemTxt", questionMetadata.getQuestionOidSystemTxt());
        assertEquals("codeSetNm", questionMetadata.getCodeSetNm());
        assertEquals("codeSetClassCd", questionMetadata.getCodeSetClassCd());
        assertEquals("dataLocation", questionMetadata.getDataLocation());
        assertEquals("dataCd", questionMetadata.getDataCd());
        assertEquals("dataUseCd", questionMetadata.getDataUseCd());
        assertEquals(100, questionMetadata.getFieldSize());
        assertEquals("parentUid", questionMetadata.getParentUid());
        assertEquals("ldfPageId", questionMetadata.getLdfPageId());
        assertEquals(4L, questionMetadata.getNbsUiMetadataUid());
        assertEquals(5L, questionMetadata.getNbsUiComponentUid());
        assertEquals("unitTypeCd", questionMetadata.getUnitTypeCd());
        assertEquals("unitValue", questionMetadata.getUnitValue());
        assertEquals("nbsTableUid", questionMetadata.getNbsTableUid());
        assertEquals("partTypeCd", questionMetadata.getPartTypeCd());
        assertEquals("standardNndIndCd", questionMetadata.getStandardNndIndCd());
        assertEquals("subGroupNm", questionMetadata.getSubGroupNm());
        assertEquals("hl7SegmentField", questionMetadata.getHl7SegmentField());
        assertEquals(20, questionMetadata.getQuestionGroupSeqNbr());
        assertEquals("questionUnitIdentifier", questionMetadata.getQuestionUnitIdentifier());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        QuestionMetadata questionMetadata = new QuestionMetadata();

        // Assert
        assertNull(questionMetadata.getNbsQuestionUid());
        assertNull(questionMetadata.getAddTime());
        assertNull(questionMetadata.getAddUserId());
        assertNull(questionMetadata.getCodeSetGroupId());
        assertNull(questionMetadata.getDataType());
        assertNull(questionMetadata.getMask());
        assertNull(questionMetadata.getInvestigationFormCd());
        assertNull(questionMetadata.getLastChgTime());
        assertNull(questionMetadata.getLastChgUserId());
        assertNull(questionMetadata.getQuestionLabel());
        assertNull(questionMetadata.getQuestionToolTip());
        assertNull(questionMetadata.getQuestionVersionNbr());
        assertNull(questionMetadata.getTabId());
        assertFalse(questionMetadata.isEnableInd());
        assertNull(questionMetadata.getOrderNbr());
        assertNull(questionMetadata.getDefaultValue());
        assertFalse(questionMetadata.isRequiredInd());
        assertFalse(questionMetadata.isDisplayInd());
        assertNull(questionMetadata.getCoinfectionIndCd());
        assertNull(questionMetadata.getNndMetadataUid());
        assertNull(questionMetadata.getQuestionIdentifier());
        assertNull(questionMetadata.getQuestionIdentifierNnd());
        assertNull(questionMetadata.getQuestionRequiredNnd());
        assertNull(questionMetadata.getQuestionOid());
        assertNull(questionMetadata.getQuestionOidSystemTxt());
        assertNull(questionMetadata.getCodeSetNm());
        assertNull(questionMetadata.getCodeSetClassCd());
        assertNull(questionMetadata.getDataLocation());
        assertNull(questionMetadata.getDataCd());
        assertNull(questionMetadata.getDataUseCd());
        assertNull(questionMetadata.getFieldSize());
        assertNull(questionMetadata.getParentUid());
        assertNull(questionMetadata.getLdfPageId());
        assertNull(questionMetadata.getNbsUiMetadataUid());
        assertNull(questionMetadata.getNbsUiComponentUid());
        assertNull(questionMetadata.getUnitTypeCd());
        assertNull(questionMetadata.getUnitValue());
        assertNull(questionMetadata.getNbsTableUid());
        assertNull(questionMetadata.getPartTypeCd());
        assertNull(questionMetadata.getStandardNndIndCd());
        assertNull(questionMetadata.getSubGroupNm());
        assertNull(questionMetadata.getHl7SegmentField());
        assertNull(questionMetadata.getQuestionGroupSeqNbr());
        assertNull(questionMetadata.getQuestionUnitIdentifier());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        QuestionMetadata questionMetadata = new QuestionMetadata();
        Long nbsQuestionUid = 1L;
        Timestamp addTime = Timestamp.valueOf("2024-07-14 10:10:10.0");
        Long addUserId = 2L;
        String codeSetGroupId = "codeSetGroupId";
        String dataType = "dataType";
        String mask = "mask";
        String investigationFormCd = "investigationFormCd";
        Timestamp lastChgTime = Timestamp.valueOf("2024-07-14 10:10:10.0");
        Long lastChgUserId = 3L;
        String questionLabel = "questionLabel";
        String questionToolTip = "questionToolTip";
        Integer questionVersionNbr = 1;
        String tabId = "tabId";
        boolean enableInd = true;
        Integer orderNbr = 10;
        String defaultValue = "defaultValue";
        boolean requiredInd = true;
        boolean displayInd = true;
        String coinfectionIndCd = "coinfectionIndCd";
        String nndMetadataUid = "nndMetadataUid";
        String questionIdentifier = "questionIdentifier";
        String questionIdentifierNnd = "questionIdentifierNnd";
        String questionRequiredNnd = "questionRequiredNnd";
        String questionOid = "questionOid";
        String questionOidSystemTxt = "questionOidSystemTxt";
        String codeSetNm = "codeSetNm";
        String codeSetClassCd = "codeSetClassCd";
        String dataLocation = "dataLocation";
        String dataCd = "dataCd";
        String dataUseCd = "dataUseCd";
        Integer fieldSize = 100;
        String parentUid = "parentUid";
        String ldfPageId = "ldfPageId";
        Long nbsUiMetadataUid = 4L;
        Long nbsUiComponentUid = 5L;
        String unitTypeCd = "unitTypeCd";
        String unitValue = "unitValue";
        String nbsTableUid = "nbsTableUid";
        String partTypeCd = "partTypeCd";
        String standardNndIndCd = "standardNndIndCd";
        String subGroupNm = "subGroupNm";
        String hl7SegmentField = "hl7SegmentField";
        Integer questionGroupSeqNbr = 20;
        String questionUnitIdentifier = "questionUnitIdentifier";

        // Act
        questionMetadata.setNbsQuestionUid(nbsQuestionUid);
        questionMetadata.setAddTime(addTime);
        questionMetadata.setAddUserId(addUserId);
        questionMetadata.setCodeSetGroupId(codeSetGroupId);
        questionMetadata.setDataType(dataType);
        questionMetadata.setMask(mask);
        questionMetadata.setInvestigationFormCd(investigationFormCd);
        questionMetadata.setLastChgTime(lastChgTime);
        questionMetadata.setLastChgUserId(lastChgUserId);
        questionMetadata.setQuestionLabel(questionLabel);
        questionMetadata.setQuestionToolTip(questionToolTip);
        questionMetadata.setQuestionVersionNbr(questionVersionNbr);
        questionMetadata.setTabId(tabId);
        questionMetadata.setEnableInd(enableInd);
        questionMetadata.setOrderNbr(orderNbr);
        questionMetadata.setDefaultValue(defaultValue);
        questionMetadata.setRequiredInd(requiredInd);
        questionMetadata.setDisplayInd(displayInd);
        questionMetadata.setCoinfectionIndCd(coinfectionIndCd);
        questionMetadata.setNndMetadataUid(nndMetadataUid);
        questionMetadata.setQuestionIdentifier(questionIdentifier);
        questionMetadata.setQuestionIdentifierNnd(questionIdentifierNnd);
        questionMetadata.setQuestionRequiredNnd(questionRequiredNnd);
        questionMetadata.setQuestionOid(questionOid);
        questionMetadata.setQuestionOidSystemTxt(questionOidSystemTxt);
        questionMetadata.setCodeSetNm(codeSetNm);
        questionMetadata.setCodeSetClassCd(codeSetClassCd);
        questionMetadata.setDataLocation(dataLocation);
        questionMetadata.setDataCd(dataCd);
        questionMetadata.setDataUseCd(dataUseCd);
        questionMetadata.setFieldSize(fieldSize);
        questionMetadata.setParentUid(parentUid);
        questionMetadata.setLdfPageId(ldfPageId);
        questionMetadata.setNbsUiMetadataUid(nbsUiMetadataUid);
        questionMetadata.setNbsUiComponentUid(nbsUiComponentUid);
        questionMetadata.setUnitTypeCd(unitTypeCd);
        questionMetadata.setUnitValue(unitValue);
        questionMetadata.setNbsTableUid(nbsTableUid);
        questionMetadata.setPartTypeCd(partTypeCd);
        questionMetadata.setStandardNndIndCd(standardNndIndCd);
        questionMetadata.setSubGroupNm(subGroupNm);
        questionMetadata.setHl7SegmentField(hl7SegmentField);
        questionMetadata.setQuestionGroupSeqNbr(questionGroupSeqNbr);
        questionMetadata.setQuestionUnitIdentifier(questionUnitIdentifier);

        // Assert
        assertEquals(nbsQuestionUid, questionMetadata.getNbsQuestionUid());
        assertEquals(addTime, questionMetadata.getAddTime());
        assertEquals(addUserId, questionMetadata.getAddUserId());
        assertEquals(codeSetGroupId, questionMetadata.getCodeSetGroupId());
        assertEquals(dataType, questionMetadata.getDataType());
        assertEquals(mask, questionMetadata.getMask());
        assertEquals(investigationFormCd, questionMetadata.getInvestigationFormCd());
        assertEquals(lastChgTime, questionMetadata.getLastChgTime());
        assertEquals(lastChgUserId, questionMetadata.getLastChgUserId());
        assertEquals(questionLabel, questionMetadata.getQuestionLabel());
        assertEquals(questionToolTip, questionMetadata.getQuestionToolTip());
        assertEquals(questionVersionNbr, questionMetadata.getQuestionVersionNbr());
        assertEquals(tabId, questionMetadata.getTabId());
        assertEquals(enableInd, questionMetadata.isEnableInd());
        assertEquals(orderNbr, questionMetadata.getOrderNbr());
        assertEquals(defaultValue, questionMetadata.getDefaultValue());
        assertEquals(requiredInd, questionMetadata.isRequiredInd());
        assertEquals(displayInd, questionMetadata.isDisplayInd());
        assertEquals(coinfectionIndCd, questionMetadata.getCoinfectionIndCd());
        assertEquals(nndMetadataUid, questionMetadata.getNndMetadataUid());
        assertEquals(questionIdentifier, questionMetadata.getQuestionIdentifier());
        assertEquals(questionIdentifierNnd, questionMetadata.getQuestionIdentifierNnd());
        assertEquals(questionRequiredNnd, questionMetadata.getQuestionRequiredNnd());
        assertEquals(questionOid, questionMetadata.getQuestionOid());
        assertEquals(questionOidSystemTxt, questionMetadata.getQuestionOidSystemTxt());
        assertEquals(codeSetNm, questionMetadata.getCodeSetNm());
        assertEquals(codeSetClassCd, questionMetadata.getCodeSetClassCd());
        assertEquals(dataLocation, questionMetadata.getDataLocation());
        assertEquals(dataCd, questionMetadata.getDataCd());
        assertEquals(dataUseCd, questionMetadata.getDataUseCd());
        assertEquals(fieldSize, questionMetadata.getFieldSize());
        assertEquals(parentUid, questionMetadata.getParentUid());
        assertEquals(ldfPageId, questionMetadata.getLdfPageId());
        assertEquals(nbsUiMetadataUid, questionMetadata.getNbsUiMetadataUid());
        assertEquals(nbsUiComponentUid, questionMetadata.getNbsUiComponentUid());
        assertEquals(unitTypeCd, questionMetadata.getUnitTypeCd());
        assertEquals(unitValue, questionMetadata.getUnitValue());
        assertEquals(nbsTableUid, questionMetadata.getNbsTableUid());
        assertEquals(partTypeCd, questionMetadata.getPartTypeCd());
        assertEquals(standardNndIndCd, questionMetadata.getStandardNndIndCd());
        assertEquals(subGroupNm, questionMetadata.getSubGroupNm());
        assertEquals(hl7SegmentField, questionMetadata.getHl7SegmentField());
        assertEquals(questionGroupSeqNbr, questionMetadata.getQuestionGroupSeqNbr());
        assertEquals(questionUnitIdentifier, questionMetadata.getQuestionUnitIdentifier());
    }
}
