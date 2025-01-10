package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
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
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        SrteCache.investigationFormConditionCode.clear();
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
    void loadQuestion_Test() {
        NbsQuestionMetadata ques = new NbsQuestionMetadata();

        var condCode = NBSConstantUtil.INV_FORM_RVCT;
       // SrteCache.investigationFormConditionCode.put(condCode, condCode);

        var tree = new TreeMap<>();
        tree.put(DecisionSupportConstants.CORE_INV_FORM, ques);
        tree.put("TA", ques);

        OdseCache.dmbMap.put(DecisionSupportConstants.CORE_INV_FORM, tree);

        when(cacheApiService.getSrteCacheBool(any(), any())).thenReturn(true);


        var res = edxPhcrDocumentUtil.loadQuestions(condCode);
        assertNotNull(res);


    }

    @Test
    void loadQuestion_Test_2() {
        NbsQuestionMetadata ques = new NbsQuestionMetadata();

        var condCode = NBSConstantUtil.INV_FORM_RVCT;
       // SrteCache.investigationFormConditionCode.put(condCode, condCode);

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
}
