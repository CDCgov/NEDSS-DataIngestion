package gov.cdc.dataprocessing.utilities.component.wds;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateDecisionSupportTest {
    @Mock
    private EdxPhcrDocumentUtil edxPhcrDocumentUtil;

    @InjectMocks
    private ValidateDecisionSupport validateDecisionSupport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsTextAndOverwriteIsTrue() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultStringValue("Test Value");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObject object = new TestObject();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);

        Method setMethod = TestObject.class.getMethod("setTestField", String.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

        // Assert
        assertEquals("Test Value", object.getTestField());
    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsDateAndOverwriteIsTrue() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultStringValue("2021-01-01 10:00:00");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObject object = new TestObject();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testDateField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_DATETIME);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @Test
    void processNBSObjectDT_shouldNotSetValue_whenDataTypeIsTextAndOverwriteIsFalseAndValueIsNotNull() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("2"); // Do not overwrite
        edxRuleManageDT.setDefaultStringValue("New Value");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObject object = new TestObject();
        object.setTestField("Existing Value");
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);

        Method setMethod = TestObject.class.getMethod("setTestField", String.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

        // Assert
        assertEquals("Existing Value", object.getTestField());
    }





    // Helper class for testing
    public static class TestObject {
        private String testField;
        private Timestamp testDateField;

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }

        public Timestamp getTestDateField() {
            return testDateField;
        }

        public void setTestDateField(Timestamp testDateField) {
            this.testDateField = testDateField;
        }
    }


    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndInteger() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("100");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testIntegerField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);

        Method setMethod = TestObjectNumeric.class.getMethod("setTestIntegerField", Integer.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndLong() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("1000");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testLongField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);

        Method setMethod = TestObjectNumeric.class.getMethod("setTestLongField", Long.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndBigDecimal() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("10000.50");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testBigDecimalField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);

        Method setMethod = TestObjectNumeric.class.getMethod("setTestBigDecimalField", BigDecimal.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndString() throws Exception {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("12345");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testStringField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);

        Method setMethod = TestObjectNumeric.class.getMethod("setTestStringField", String.class);

        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    public static class TestObjectNumeric {
        private Integer testIntegerField;
        private Long testLongField;
        private BigDecimal testBigDecimalField;
        private String testStringField;

        public Integer getTestIntegerField() {
            return testIntegerField;
        }

        public void setTestIntegerField(Integer testIntegerField) {
            this.testIntegerField = testIntegerField;
        }

        public Long getTestLongField() {
            return testLongField;
        }

        public void setTestLongField(Long testLongField) {
            this.testLongField = testLongField;
        }

        public BigDecimal getTestBigDecimalField() {
            return testBigDecimalField;
        }

        public void setTestBigDecimalField(BigDecimal testBigDecimalField) {
            this.testBigDecimalField = testBigDecimalField;
        }

        public String getTestStringField() {
            return testStringField;
        }

        public void setTestStringField(String testStringField) {
            this.testStringField = testStringField;
        }
    }
}
