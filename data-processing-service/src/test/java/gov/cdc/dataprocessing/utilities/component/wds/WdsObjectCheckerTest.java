package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WdsObjectCheckerTest {

    @InjectMocks
    private WdsObjectChecker wdsObjectChecker;

    private EdxRuleManageDto edxRuleManageDto;
    private NbsQuestionMetadata metaData;
    private TestObject testObject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        edxRuleManageDto = mock(EdxRuleManageDto.class);
        metaData = mock(NbsQuestionMetadata.class);
        testObject = new TestObject();
    }

    @Test
    void testCheckNbsObject_TextEquals() {
        testObject.setSampleText("testValue");
        when(metaData.getDataLocation()).thenReturn("testObject.sampleText");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);
        when(edxRuleManageDto.getLogic()).thenReturn("=");
        when(edxRuleManageDto.getValue()).thenReturn("testValue");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_TextNotEquals() {
        testObject.setSampleText("testValue");
        when(metaData.getDataLocation()).thenReturn("testObject.sampleText");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);
        when(edxRuleManageDto.getLogic()).thenReturn("!=");
        when(edxRuleManageDto.getValue()).thenReturn("differentValue");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_DateEquals() throws Exception {
        Timestamp timestamp = Timestamp.valueOf("2022-12-31 00:00:00.0");
        testObject.setSampleTimestamp(timestamp);
        when(metaData.getDataLocation()).thenReturn("testObject.sampleTimestamp");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_DATE);
        when(edxRuleManageDto.getLogic()).thenReturn("=");
        when(edxRuleManageDto.getValue()).thenReturn("12/31/2022 00:00:00");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_DateNotEquals() throws Exception {
        Timestamp timestamp = Timestamp.valueOf("2022-12-31 00:00:00.0");
        testObject.setSampleTimestamp(timestamp);
        when(metaData.getDataLocation()).thenReturn("testObject.sampleTimestamp");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_DATE);
        when(edxRuleManageDto.getLogic()).thenReturn("!=");
        when(edxRuleManageDto.getValue()).thenReturn("01/01/2023 00:00:00");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_NumericGreaterThan() {
        testObject.setSampleNumber(10L);
        when(metaData.getDataLocation()).thenReturn("testObject.sampleNumber");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);
        when(edxRuleManageDto.getLogic()).thenReturn(">");
        when(edxRuleManageDto.getValue()).thenReturn("5");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_NumericLessThan() {
        testObject.setSampleNumber(3L);
        when(metaData.getDataLocation()).thenReturn("testObject.sampleNumber");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);
        when(edxRuleManageDto.getLogic()).thenReturn("<");
        when(edxRuleManageDto.getValue()).thenReturn("5");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertTrue(result);
    }

    @Test
    void testCheckNbsObject_InvalidMethod() {
        when(metaData.getDataLocation()).thenReturn("testObject.invalidField");
        when(metaData.getDataType()).thenReturn(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);
        when(edxRuleManageDto.getLogic()).thenReturn("=");
        when(edxRuleManageDto.getValue()).thenReturn("value");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDto, testObject, metaData);
        assertFalse(result);
    }

    // Test object for method invocation
    private static class TestObject {
        private String sampleText;
        private Timestamp sampleTimestamp;
        private Long sampleNumber;

        public String getSampleText() {
            return sampleText;
        }

        public void setSampleText(String sampleText) {
            this.sampleText = sampleText;
        }

        public Timestamp getSampleTimestamp() {
            return sampleTimestamp;
        }

        public void setSampleTimestamp(Timestamp sampleTimestamp) {
            this.sampleTimestamp = sampleTimestamp;
        }

        public Long getSampleNumber() {
            return sampleNumber;
        }

        public void setSampleNumber(Long sampleNumber) {
            this.sampleNumber = sampleNumber;
        }
    }
}
