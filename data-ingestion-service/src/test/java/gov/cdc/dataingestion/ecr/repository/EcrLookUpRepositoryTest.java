package gov.cdc.dataingestion.ecr.repository;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.implementation.EcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

class EcrLookUpRepositoryTest {
    @InjectMocks
    private EcrLookUpRepository target;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchConstantLookUpByCriteriaWithColumn() throws EcrCdaXmlException {
        // Given
        String column = "sampleColumn";
        String value = "sampleValue";

        Object[] lookUpArray = {
                "id", "subjectArea", "questionIdentifier", "questionDisplayName", "sampleValue", "usage"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("LOOK_UP_VAL", value)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Arrays.asList(new Object[][] { lookUpArray }));

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        ConstantLookUpDto result = target.fetchConstantLookUpByCriteriaWithColumn(column, value);

        // Then
        assertNotNull(result);

        assertEquals("id", result.getId());
        assertEquals("subjectArea", result.getSubjectArea());
        assertEquals("questionIdentifier", result.getQuestionIdentifier());
        assertEquals("questionDisplayName", result.getQuestionDisplayName());
        assertEquals("sampleValue", result.getSampleValue());
        assertEquals("usage", result.getUsage());

        verify(mockQuery).setParameter("LOOK_UP_VAL", value);
        verify(mockQuery).getResultList();
    }

    @Test
    void testFetchPhdcAnswerByCriteriaForTranslationCode() throws EcrCdaXmlException {
        // Given
        String questionIdentifier = "sampleQuestionIdentifier";
        String ansFromCode = "sampleAnsFromCode";

        Object[] phdcAnswerArray = {
                "ansToCode1", "ansToCodeSystemCd1", "ansFromCodeSystemDescTxt1", "ansFromDisplayNm1", "ansToCode2",
                "ansToCodeSystemCd2", "ansToCodeSystemDescTxt2", "ansToDisplayNm2", "codeTranslationRequired1",
                "docTypeCd1", "docTypeVersionTxt1", "quesCodeSystemCd1", "questionIdentifier1", "sendingSystemCd1"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("QUESTION_IDENTIFIER", questionIdentifier)).thenReturn(mockQuery);
        when(mockQuery.setParameter("ANSWER_FROM_CODE", ansFromCode)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Arrays.asList(new Object[][]{ phdcAnswerArray }));

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        PhdcAnswerLookUpDto result = target.fetchPhdcAnswerByCriteriaForTranslationCode(questionIdentifier, ansFromCode);

        // Then
        assertNotNull(result);
        assertEquals("ansToCode2", result.getAnsToCode());
        assertEquals("ansToCodeSystemCd2", result.getAnsToCodeSystemCd());
        assertEquals("ansFromCodeSystemDescTxt1", result.getAnsFromCodeSystemDescTxt());
        assertEquals("ansFromDisplayNm1", result.getAnsFromDisplayNm());
        assertEquals("ansToCodeSystemCd2", result.getAnsToCodeSystemCd());
        assertEquals("ansToCodeSystemDescTxt2", result.getAnsToCodeSystemDescTxt());
        assertEquals("ansToDisplayNm2", result.getAnsToDisplayNm());
        assertEquals("codeTranslationRequired1", result.getCodeTranslationRequired());
        assertEquals("docTypeCd1", result.getDocTypeCd());
        assertEquals("docTypeVersionTxt1", result.getDocTypeVersionTxt());
        assertEquals("quesCodeSystemCd1", result.getQuesCodeSystemCd());
        assertEquals("questionIdentifier1", result.getQuestionIdentifier());
        assertEquals("sendingSystemCd1", result.getSendingSystemCd());

        verify(mockQuery).setParameter("QUESTION_IDENTIFIER", questionIdentifier);
        verify(mockQuery).setParameter("ANSWER_FROM_CODE", ansFromCode);
        verify(mockQuery).getResultList();
    }

    @Test
    void testFetchPhdcQuestionByCriteria() throws EcrCdaXmlException {
        // Given
        String questionIdentifier = "sampleQuestionIdentifier";

        Object[] phdcQuestionArray = {
                "dataType1", "docTypeVersionTxt1", "quesCodeSystemCd1", "quesCodeSystemDescTxt1",
                "dataType2", "questionIdentifier1", "quesDisplayName1", "sectionNm1", "sendingSystemCd1"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("QUES_IDENTIFIER", questionIdentifier)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Arrays.asList(new Object[][] { phdcQuestionArray }));

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        PhdcQuestionLookUpDto result = target.fetchPhdcQuestionByCriteria(questionIdentifier);

        // Then
        assertNotNull(result);

        assertEquals("dataType2", result.getDataType());
        assertEquals("docTypeVersionTxt1", result.getDocTypeVersionTxt());
        assertEquals("quesCodeSystemCd1", result.getQuesCodeSystemCd());
        assertEquals("quesCodeSystemDescTxt1", result.getQuesCodeSystemDescTxt());
        assertEquals("dataType2", result.getDataType());  // Note: You're setting this twice in the original method, so you might want to fix that.
        assertEquals("questionIdentifier1", result.getQuestionIdentifier());
        assertEquals("quesDisplayName1", result.getQuesDisplayName());
        assertEquals("sectionNm1", result.getSectionNm());
        assertEquals("sendingSystemCd1", result.getSendingSystemCd());

        verify(mockQuery).setParameter("QUES_IDENTIFIER", questionIdentifier);
        verify(mockQuery).getResultList();
    }

    @Test
    void testFetchPhdcQuestionByCriteriaWithColumn() throws EcrCdaXmlException {
        // Given
        String column = "sampleColumn";
        String value = "sampleValue";

        Object[] phdcQuestionArray = {
                "dataType1", "docTypeVersionTxt1", "quesCodeSystemCd1", "quesCodeSystemDescTxt1",
                "dataType2", "questionIdentifier1", "quesDisplayName1", "sectionNm1", "sendingSystemCd1"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("COL_VALUE", value)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Arrays.asList(new Object[][] { phdcQuestionArray }));

        when(entityManager.createNativeQuery(anyString().replace("{TAB_COLUMN}", column))).thenReturn(mockQuery);

        // When
        PhdcQuestionLookUpDto result = target.fetchPhdcQuestionByCriteriaWithColumn(column, value);

        // Then
        assertNotNull(result);

        assertEquals("dataType2", result.getDataType());
        assertEquals("docTypeVersionTxt1", result.getDocTypeVersionTxt());
        assertEquals("quesCodeSystemCd1", result.getQuesCodeSystemCd());
        assertEquals("quesCodeSystemDescTxt1", result.getQuesCodeSystemDescTxt());
        assertEquals("dataType2", result.getDataType());  // Note: You're setting this twice in the original method, so you might want to fix that.
        assertEquals("questionIdentifier1", result.getQuestionIdentifier());
        assertEquals("quesDisplayName1", result.getQuesDisplayName());
        assertEquals("sectionNm1", result.getSectionNm());
        assertEquals("sendingSystemCd1", result.getSendingSystemCd());

        verify(mockQuery).setParameter("COL_VALUE", value);
        verify(mockQuery).getResultList();
    }

    @Test
    void testFetchQuestionIdentifierMapByCriteriaByCriteria() throws EcrCdaXmlException {
        // Given
        String columNm = "sampleColumn";
        String value = "sampleValue";

        Object[] questionIdentifierMapArray = {
                "columnNm1", "questionIdentifier1", "dynamicQuestionIdentifier1"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("COLUMN_NM_VALUE", value)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Arrays.asList(new Object[][] { questionIdentifierMapArray }));

        when(entityManager.createNativeQuery(anyString().replace("{COLUMN_NM}", columNm))).thenReturn(mockQuery);

        // When
        QuestionIdentifierMapDto result = target.fetchQuestionIdentifierMapByCriteriaByCriteria(columNm, value);

        // Then
        assertNotNull(result);

        assertEquals("columnNm1", result.getColumnNm());
        assertEquals("questionIdentifier1", result.getQuestionIdentifier());
        assertEquals("dynamicQuestionIdentifier1", result.getDynamicQuestionIdentifier());

        verify(mockQuery).setParameter("COLUMN_NM_VALUE", value);
        verify(mockQuery).getResultList();
    }


}
