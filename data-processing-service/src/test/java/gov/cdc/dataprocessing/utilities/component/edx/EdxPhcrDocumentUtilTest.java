package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EdxPhcrDocumentUtilTest {
    @InjectMocks
    private EdxPhcrDocumentUtil edxPhcrDocumentUtil;



    @Mock
    private ILookupService lookupService;

    @Mock
    private ICacheApiService cacheApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testRequiredFieldCheck() {
        Map<Object, Object> requiredQuestionIdentifierMap = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setQuestionLabel("QuestionLabel");
        requiredQuestionIdentifierMap.put("key", metadata);

        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();

        String result = edxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        assertNotNull(result);
        assertTrue(result.contains("QuestionLabel"));
    }

    @Test
    void testRequiredFieldCheckWithAllAnswersPresent() {
        Map<Object, Object> requiredQuestionIdentifierMap = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setQuestionLabel("QuestionLabel");
        requiredQuestionIdentifierMap.put("key", metadata);

        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();
        nbsCaseAnswerMap.put("key", new Object());

        String result = edxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        assertNull(result);
    }

    @Test
    void testSetStandardNBSCaseAnswerVals() {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        publicHealthCaseContainer.getThePublicHealthCaseDto().setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        publicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        publicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserId(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserId(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));

        NbsCaseAnswerDto nbsCaseAnswerDT = new NbsCaseAnswerDto();
        nbsCaseAnswerDT.setSeqNbr(-1);

        NbsCaseAnswerDto result = edxPhcrDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseContainer, nbsCaseAnswerDT);

        assertNotNull(result);
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid(), result.getActUid());
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getAddTime(), result.getAddTime());
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getLastChgTime(), result.getLastChgTime());
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getAddUserId(), result.getAddUserId());
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getLastChgUserId(), result.getLastChgUserId());
        assertEquals(publicHealthCaseContainer.getThePublicHealthCaseDto().getRecordStatusTime(), result.getRecordStatusTime());
        assertEquals(0, result.getSeqNbr());
        assertTrue(result.isItNew());
    }


    @Test
    void loadQuestion_Test() throws DataProcessingException {
        NbsQuestionMetadata ques = new NbsQuestionMetadata();

        var condCode = NBSConstantUtil.INV_FORM_RVCT;

        var tree = new TreeMap<>();
        tree.put(DecisionSupportConstants.CORE_INV_FORM, ques);
        tree.put("TA", ques);

        OdseCache.dmbMap.put(DecisionSupportConstants.CORE_INV_FORM, tree);

        when(cacheApiService.getSrteCacheBool(any(), any())).thenReturn(true);


        var res = edxPhcrDocumentUtil.loadQuestions(condCode);
        assertNotNull(res);


    }

    @Test
    void loadQuestion_Test_2() throws DataProcessingException {
        NbsQuestionMetadata ques = new NbsQuestionMetadata();

        var condCode = NBSConstantUtil.INV_FORM_RVCT;

        var tree = new TreeMap<>();
        tree.put(DecisionSupportConstants.CORE_INV_FORM, ques);
        tree.put("TA", ques);

        OdseCache.dmbMap.put("BLAH", tree);


        var map = new TreeMap<>();
        map.put(DecisionSupportConstants.CORE_INV_FORM, tree);
        when(lookupService.getDMBQuestionMapAfterPublish()).thenReturn(map);


        var res = edxPhcrDocumentUtil.loadQuestions(condCode);
        assertNotNull(res);


    }

    @Test
    void testSetStandardNBSCaseAnswerVals_WhenSeqNbrIsNegative_ShouldSetToZero() {
        // Arrange
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(123L);
        phcDto.setAddTime(new Timestamp(System.currentTimeMillis()));
        phcDto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        phcDto.setAddUserId(999L);
        phcDto.setLastChgUserId(888L);
        phcDto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));

        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        phcContainer.setThePublicHealthCaseDto(phcDto);

        NbsCaseAnswerDto answerDto = new NbsCaseAnswerDto();
        answerDto.setSeqNbr(-5); // <- triggers the if condition

        // Act
        NbsCaseAnswerDto result = edxPhcrDocumentUtil.setStandardNBSCaseAnswerVals(phcContainer, answerDto);

        // Assert
        assertEquals(0, result.getSeqNbr()); // confirms it was reset to 0
        assertTrue(result.isItNew());
        assertEquals("OPEN", result.getRecordStatusCd());
    }

    @Test
    void testRequiredFieldCheck_WhenExceptionThrown_ShouldCatchAndLog() {
        // Arrange
        Map<Object, Object> requiredQuestionIdentifierMap = new HashMap<>();
        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();

        String reqdKey = "REQ1";
        requiredQuestionIdentifierMap.put(reqdKey, "This is not a NbsQuestionMetadata"); // will cause ClassCastException

        // Act
        String result = edxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        // Assert
        assertNull(result); // Should still return null due to error handling
    }

    @Test
    void testRequiredFieldCheck_MultipleMissingFields_TriggersAndClause() {
        // Arrange
        Map<Object, Object> requiredQuestionIdentifierMap = new LinkedHashMap<>();
        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();

        NbsQuestionMetadata meta1 = new NbsQuestionMetadata();
        meta1.setQuestionLabel("First Required Field");
        meta1.setQuestionGroupSeqNbr(null);

        NbsQuestionMetadata meta2 = new NbsQuestionMetadata();
        meta2.setQuestionLabel("Second Required Field");
        meta2.setQuestionGroupSeqNbr(null);

        requiredQuestionIdentifierMap.put("REQ1", meta1);
        requiredQuestionIdentifierMap.put("REQ2", meta2);

        // nbsCaseAnswerMap does NOT contain "REQ1" or "REQ2", so both will be treated as missing

        // Act
        String result = edxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("The following required field(s) are missing:"));
        assertTrue(result.contains("[First Required Field]; "));
        assertTrue(result.contains(" and [Second Required Field]."));
    }


    @Test
    void testRequiredFieldCheck_WhenMultipleFieldsMissing_ShouldBuildPluralErrorMessage() {
        // Arrange
        Map<Object, Object> requiredQuestionIdentifierMap = new LinkedHashMap<>();
        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();

        NbsQuestionMetadata meta1 = new NbsQuestionMetadata();
        meta1.setQuestionLabel("Field A");
        meta1.setQuestionGroupSeqNbr(null);

        NbsQuestionMetadata meta2 = new NbsQuestionMetadata();
        meta2.setQuestionLabel("Field B");
        meta2.setQuestionGroupSeqNbr(null);

        requiredQuestionIdentifierMap.put("Q1", meta1);
        requiredQuestionIdentifierMap.put("Q2", meta2);

        // Act
        String result = edxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("The following required field(s) are missing: "));
        assertTrue(result.contains("[Field A]"));
        assertTrue(result.contains("[Field B]"));
    }


    @Test
    void testGetQuestionMapForFormCode_WhenFormCodeIsNull_ShouldReturnEmptyMap() {
        // Act
        Map<Object, Object> result = edxPhcrDocumentUtil.getQuestionMapForFormCode(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetQuestionMapForFormCode_WhenFormCodeIsRvctAndFoundInLookup_ShouldReturnMap() {
        // Arrange
        String formCode = NBSConstantUtil.INV_FORM_RVCT;

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setQuestionIdentifier("Q1");

        Map<Object, Object> innerMap = new HashMap<>();
        innerMap.put("Q1", meta);

        TreeMap<Object, Object> outerMap = new TreeMap<>();
        outerMap.put(formCode, innerMap);

        when(lookupService.getQuestionMap()).thenReturn(outerMap);

        // Act
        Map<Object, Object> result = edxPhcrDocumentUtil.getQuestionMapForFormCode(formCode);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Q1"));
        assertSame(meta, result.get("Q1"));
    }

    @Test
    void testGetQuestionMapForFormCode_WhenNotInDmbMapButInDmbQuestionMap_ShouldReturnQuestions() {
        // Arrange
        String formCode = "NON_RVCT_FORM";

        // Ensure dmbMap does NOT contain the formCode
        OdseCache.dmbMap.remove(formCode);

        // Create sample metadata
        NbsQuestionMetadata mockMeta = new NbsQuestionMetadata();
        mockMeta.setQuestionIdentifier("Q42");

        // Put entry in DMB_QUESTION_MAP
        Map<Object, Object> mockQuestions = new HashMap<>();
        mockQuestions.put("Q42", mockMeta);
        OdseCache.DMB_QUESTION_MAP.put(formCode, mockQuestions);

        // Act
        Map<Object, Object> result = edxPhcrDocumentUtil.getQuestionMapForFormCode(formCode);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Q42"));
        assertSame(mockMeta, result.get("Q42"));
    }





}
