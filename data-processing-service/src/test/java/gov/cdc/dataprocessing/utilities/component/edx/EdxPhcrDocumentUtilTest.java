package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
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
import static org.mockito.Mockito.when;

public class EdxPhcrDocumentUtilTest {
    @InjectMocks
    private EdxPhcrDocumentUtil edxPhcrDocumentUtil;

    @Mock
    private ILookupService lookupService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SrteCache.investigationFormConditionCode.clear();
    }




    @Test
    public void testRequiredFieldCheck() {
        Map<Object, Object> requiredQuestionIdentifierMap = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setQuestionLabel("QuestionLabel");
        requiredQuestionIdentifierMap.put("key", metadata);

        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();

        String result = EdxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        assertNotNull(result);
        assertTrue(result.contains("QuestionLabel"));
    }

    @Test
    public void testRequiredFieldCheckWithAllAnswersPresent() {
        Map<Object, Object> requiredQuestionIdentifierMap = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setQuestionLabel("QuestionLabel");
        requiredQuestionIdentifierMap.put("key", metadata);

        Map<Object, Object> nbsCaseAnswerMap = new HashMap<>();
        nbsCaseAnswerMap.put("key", new Object());

        String result = EdxPhcrDocumentUtil.requiredFieldCheck(requiredQuestionIdentifierMap, nbsCaseAnswerMap);

        assertNull(result);
    }

    @Test
    public void testSetStandardNBSCaseAnswerVals() {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        publicHealthCaseContainer.getThePublicHealthCaseDto().setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());
        publicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        publicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserId(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserId(1L);
        publicHealthCaseContainer.getThePublicHealthCaseDto().setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());

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
}
