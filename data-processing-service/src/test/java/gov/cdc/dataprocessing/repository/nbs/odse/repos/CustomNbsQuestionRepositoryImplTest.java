package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
class CustomNbsQuestionRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private CustomNbsQuestionRepositoryImpl customNbsQuestionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveQuestionRequiredNnd() {
        String formCd = "testFormCode";

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{
                1L, "questionIdentifier", "questionLabel", "dataLocation"
        });

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResultList);

        var result = customNbsQuestionRepository.retrieveQuestionRequiredNnd(formCd);

        assertEquals(1, result.size());

        QuestionRequiredNnd questionRequiredNnd = result.iterator().next();
        assertEquals(1L, questionRequiredNnd.getNbsQuestionUid());
        assertEquals("questionIdentifier", questionRequiredNnd.getQuestionIdentifier());
        assertEquals("questionLabel", questionRequiredNnd.getQuestionLabel());
        assertEquals("dataLocation", questionRequiredNnd.getDataLocation());
    }
}