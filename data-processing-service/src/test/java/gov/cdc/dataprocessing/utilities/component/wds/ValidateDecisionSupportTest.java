package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidateDecisionSupportTest {
    @Mock
    private EdxPhcrDocumentUtil edxPhcrDocumentUtil;

    @InjectMocks
    private ValidateDecisionSupport validateDecisionSupport;
    @Mock
    private InvestigationDefaultValuesType investigationDefaultValuesType;
    @Mock
    private EdxRuleManageDto edxRuleManageDTMock;
    @Mock
    private NbsQuestionMetadata metaDataMock;

    @Mock
    private Object objectMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() {
        Mockito.reset(investigationDefaultValuesType);
    }

    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsTextAndOverwriteIsTrue() {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultStringValue("Test Value");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObject object = new TestObject();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);


        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

        // Assert
        assertEquals("Test Value", object.getTestField());
    }

    @SuppressWarnings("java:S2699")
    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsDateAndOverwriteIsTrue() {
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
    void processNBSObjectDT_shouldNotSetValue_whenDataTypeIsTextAndOverwriteIsFalseAndValueIsNotNull() {
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

    @SuppressWarnings({"java:S2699", "java:S5976"})
    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndInteger() {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("100");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testIntegerField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);


        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @SuppressWarnings({"java:S2699", "java:S5976"})
    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndLong() {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("1000");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testLongField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);


        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }

    @SuppressWarnings({"java:S2699", "java:S5976"})
    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndBigDecimal() {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("10000.50");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testBigDecimalField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);


        // Act
        validateDecisionSupport.processNBSObjectDT(edxRuleManageDT, publicHealthCaseContainer, object, metaData);

    }
    @SuppressWarnings({"java:S2699", "java:S5976"})
    @Test
    void processNBSObjectDT_shouldSetValue_whenDataTypeIsNumericAndString()  {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultNumericValue("12345");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObjectNumeric object = new TestObjectNumeric();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testStringField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC);


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



    @Test
    void processNBSCaseAnswerDT_Test() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        BasePamContainer pamVO = new BasePamContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);
        var codedCol = new ArrayList<>();
        codedCol.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedCol);

        Map<Object, Object> pamAnswerDTMap = new HashMap<>();
        pamVO.setPamAnswerDTMap(pamAnswerDTMap);

        metaData.setNbsUiComponentUid(1013L);

        validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseContainer, pamVO, metaData);

        verify(edxPhcrDocumentUtil, times(1)).setStandardNBSCaseAnswerVals(any(), any());

    }

    @Test
    void processNBSCaseAnswerDT_Test_2() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        BasePamContainer pamVO = new BasePamContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);
        var codedCol = new ArrayList<>();
        codedCol.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedCol);
        edxRuleManageDT.setDefaultNumericValue("TEST");
        edxRuleManageDT.setDefaultStringValue("TEST");

        Map<Object, Object> pamAnswerDTMap = new HashMap<>();
        pamVO.setPamAnswerDTMap(pamAnswerDTMap);

        metaData.setNbsUiComponentUid(1L);

        validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseContainer, pamVO, metaData);

        verify(edxPhcrDocumentUtil, times(1)).setStandardNBSCaseAnswerVals(any(), any());

    }

    @Test
    void processNBSCaseAnswerDT_Test_3() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        BasePamContainer pamVO = new BasePamContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);
        var codedCol = new ArrayList<>();
        codedCol.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedCol);

        Map<Object, Object> pamAnswerDTMap = new HashMap<>();
        pamVO.setPamAnswerDTMap(pamAnswerDTMap);

        metaData.setNbsUiComponentUid(1013L);

        validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseContainer, pamVO, metaData);

        verify(edxPhcrDocumentUtil, times(1)).setStandardNBSCaseAnswerVals(any(), any());
    }

    @Test
    void processNBSCaseAnswerDT_Test_4() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        BasePamContainer pamVO = new BasePamContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);
        var codedCol = new ArrayList<>();
        codedCol.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedCol);
        edxRuleManageDT.setDefaultNumericValue("TEST");
        edxRuleManageDT.setDefaultStringValue("TEST");

        Map<Object, Object> pamAnswerDTMap = new HashMap<>();
        pamVO.setPamAnswerDTMap(pamAnswerDTMap);

        metaData.setNbsUiComponentUid(1L);

        validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseContainer, pamVO, metaData);

        verify(edxPhcrDocumentUtil, times(1)).setStandardNBSCaseAnswerVals(any(), any());

    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodCodeDT_Test_1() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        metaData.setNbsUiComponentUid(1013L);

        edxRuleManageDT.setBehavior("1");
        var codedArr = new ArrayList<>();
        codedArr.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedArr);

        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(phcDt);
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        confirmCol.add(confirm);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(confirmCol);

        validateDecisionSupport.processConfirmationMethodCodeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodCodeDT_Test_2() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        metaData.setNbsUiComponentUid(1013L);

        edxRuleManageDT.setBehavior("2");
        var codedArr = new ArrayList<>();
        codedArr.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedArr);

        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(phcDt);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(null);

        validateDecisionSupport.processConfirmationMethodCodeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodCodeDT_Test_3() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        metaData.setNbsUiComponentUid(1013L);

        edxRuleManageDT.setBehavior("2");
        var codedArr = new ArrayList<>();
        codedArr.add("TEST");
        edxRuleManageDT.setDefaultCodedValueColl(codedArr);

        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(phcDt);
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        confirm.setConfirmationMethodCd("TEST");
        confirmCol.add(confirm);
        confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(null);
        confirmCol.add(confirm);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(confirmCol);

        validateDecisionSupport.processConfirmationMethodCodeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodTimeDT_Test_1() throws DataProcessingException {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);

        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        confirm.setConfirmationMethodCd("TEST");
        confirmCol.add(confirm);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(confirmCol);

        validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);


    }

    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodTimeDT_Test_2() throws DataProcessingException {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");
        edxRuleManageDT.setDefaultStringValue(TimeStampUtil.convertTimestampToString("UTC"));

        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(null);

        validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodTimeDT_Test_3() throws DataProcessingException {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");
        edxRuleManageDT.setDefaultStringValue(null);

        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        confirm.setConfirmationMethodCd("TEST");
        confirmCol.add(confirm);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(confirmCol);

        validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodTimeDT_Test_4() throws DataProcessingException {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);

        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(null);

        metaData.setNbsUiComponentUid(1013L);

        validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);


    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodTimeDT_Test_5() throws DataProcessingException {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");
        edxRuleManageDT.setDefaultStringValue(NEDSSConstant.USE_CURRENT_DATE);

        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(null);

        metaData.setNbsUiComponentUid(1014L);

        validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);


    }

    @Test
    void processNBSCaseManagementDT_Test() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        publicHealthCaseContainer.setTheCaseManagementDto(new CaseManagementDto());

        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            validateDecisionSupport.processNBSCaseManagementDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
        });

        assertNotNull(thrown);

    }
    @SuppressWarnings("java:S2699")
    @Test
    void processConfirmationMethodCodeDTRequired_Test() {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirm.setConfirmationMethodTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        confirm.setConfirmationMethodCd(null);
        confirmCol.add(confirm);
        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(confirmCol);

        validateDecisionSupport.processConfirmationMethodCodeDTRequired(publicHealthCaseContainer);

    }



    @Test
    void parseInvestigationDefaultValuesType_Test() {
        Map<Object, Object> map = new HashMap<>();

        var defaultLst = new ArrayList<DefaultValueType>();
        var def = new DefaultValueType();
        var codeType = new CodedType();
        codeType.setCode("TEST");
        def.setDefaultQuestion(codeType);

        def.setDefaultStringValue("TEST");
        defaultLst.add(def);
        when(investigationDefaultValuesType.getDefaultValue()).thenReturn(defaultLst);

        validateDecisionSupport.parseInvestigationDefaultValuesType(map, investigationDefaultValuesType);

        verify(investigationDefaultValuesType, times(1)).getDefaultValue();

    }

    @Test
    void parseInvestigationDefaultValuesType_Test_2() {
        Map<Object, Object> map = new HashMap<>();

        var defaultLst = new ArrayList<DefaultValueType>();
        var def = new DefaultValueType();
        var codeType = new CodedType();
        codeType.setCode("TEST");
        def.setDefaultQuestion(codeType);

        def.setDefaultCommentValue("TEST");
        defaultLst.add(def);
        when(investigationDefaultValuesType.getDefaultValue()).thenReturn(defaultLst);

        validateDecisionSupport.parseInvestigationDefaultValuesType(map, investigationDefaultValuesType);

        verify(investigationDefaultValuesType, times(1)).getDefaultValue();

    }

    @Test
    void parseInvestigationDefaultValuesType_Test_3() {
        Map<Object, Object> map = new HashMap<>();

        var defaultLst = new ArrayList<DefaultValueType>();
        var def = new DefaultValueType();
        var codeType = new CodedType();
        codeType.setCode("TEST");
        def.setDefaultQuestion(codeType);

        var num = new NumericType();
        num.setValue1(1L);
        def.setDefaultNumericValue(num);
        defaultLst.add(def);
        when(investigationDefaultValuesType.getDefaultValue()).thenReturn(defaultLst);

        validateDecisionSupport.parseInvestigationDefaultValuesType(map, investigationDefaultValuesType);

        verify(investigationDefaultValuesType, times(1)).getDefaultValue();

    }

    @Test
    void parseInvestigationDefaultValuesType_Test_4() {
        Map<Object, Object> map = new HashMap<>();

        var defaultLst = new ArrayList<DefaultValueType>();
        var def = new DefaultValueType();
        var codeType = new CodedType();
        codeType.setCode("TEST");
        def.setDefaultQuestion(codeType);

        var num = new DefaultParticipationType();
        var code = new CodedType();
        num.setParticipationType(code);
        def.setDefaultParticipation(num);
        defaultLst.add(def);
        when(investigationDefaultValuesType.getDefaultValue()).thenReturn(defaultLst);

        validateDecisionSupport.parseInvestigationDefaultValuesType(map, investigationDefaultValuesType);

        verify(investigationDefaultValuesType, times(1)).getDefaultValue();

    }

    @SuppressWarnings("java:S2699")
    @Test
    void processActIds_Test() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");

        var actCol = new ArrayList<ActIdDto>();
        var act = new ActIdDto();
        act.setTypeCd(NEDSSConstant.ACT_ID_STATE_TYPE_CD);
        actCol.add(act);
        publicHealthCaseContainer.setTheActIdDTCollection(actCol);

        metaData.setDataCd( NEDSSConstant.ACT_ID_STATE_TYPE_CD);

        validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void processActIds_Test_2() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");

        var actCol = new ArrayList<ActIdDto>();
        var act = new ActIdDto();
        act.setTypeCd(NEDSSConstant.ACT_ID_STATE_TYPE_CD);
        actCol.add(act);
        publicHealthCaseContainer.setTheActIdDTCollection(actCol);

        metaData.setDataCd( NEDSSConstant.ACT_ID_STATE_TYPE_CD);

        validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void processActIds_Test_3() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("2");

        var actCol = new ArrayList<ActIdDto>();
        var act = new ActIdDto();
        act.setTypeCd("CITY");
        actCol.add(act);
        publicHealthCaseContainer.setTheActIdDTCollection(actCol);

        metaData.setDataCd("CITY");

        validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void processActIds_Test_4() {
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();

        edxRuleManageDT.setBehavior("1");

        var actCol = new ArrayList<ActIdDto>();
        var act = new ActIdDto();
        act.setTypeCd("CITY");
        actCol.add(act);
        publicHealthCaseContainer.setTheActIdDTCollection(actCol);

        metaData.setDataCd( "CITY");

        validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseContainer, metaData);
    }


    @Test
    void testGetCurrentDateValue_UseCurrentDate() {
        // Arrange
        when(edxRuleManageDTMock.getDefaultStringValue()).thenReturn(NEDSSConstant.USE_CURRENT_DATE);

        Timestamp fixedTimestamp = Timestamp.valueOf("2024-06-11 12:00:00");
        String expectedDate = StringUtils.formatDate(fixedTimestamp);

        // Use mockStatic if TimeStampUtil is static
        try (MockedStatic<TimeStampUtil> mockedStatic = Mockito.mockStatic(TimeStampUtil.class)) {
            mockedStatic.when(() -> TimeStampUtil.getCurrentTimeStamp(any())).thenReturn(fixedTimestamp);

            // Act
            validateDecisionSupport.getCurrentDateValue(edxRuleManageDTMock);

            // Assert
            verify(edxRuleManageDTMock).setDefaultStringValue(expectedDate);
        }
    }


    @Test
    void testGetCurrentDateValue_NotUseCurrentDate() {
        // Arrange
        when(edxRuleManageDTMock.getDefaultStringValue()).thenReturn("someOtherValue");

        // v
        validateDecisionSupport.getCurrentDateValue(edxRuleManageDTMock);

        // Assert
        verify(edxRuleManageDTMock, never()).setDefaultStringValue(anyString());
    }

    @Test
    void processNbsObject_Test() {
        // Arrange
        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setBehavior("1"); // Overwrite
        edxRuleManageDT.setDefaultStringValue("Test Value");

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        TestObject object = new TestObject();
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataLocation("testField");
        metaData.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT);


        // Act
        validateDecisionSupport.processNbsObject(edxRuleManageDT, publicHealthCaseContainer, metaData);

        // Assert
        assertNull( object.getTestField());
    }

}
