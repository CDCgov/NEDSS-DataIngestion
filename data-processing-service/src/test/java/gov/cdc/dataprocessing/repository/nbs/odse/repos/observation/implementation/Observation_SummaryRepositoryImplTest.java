package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.implementation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Summary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
class Observation_SummaryRepositoryImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private Query nativeQuery;

    @Mock
    private TypedQuery<Observation_Lab_Summary_ForWorkUp_New> typedQuery;

    @InjectMocks
    private Observation_SummaryRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testFindAllActiveLabReportUidListForManage_NoResults() {
        Long investigationUid = 1L;
        String whereClause = "AND ar.some_column = 'some_value'";

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(new ArrayList<>());

        Collection<Observation_Summary> result = repository.findAllActiveLabReportUidListForManage(investigationUid, whereClause);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindLabSummaryForWorkupNew() {
        Long personParentUid = 1L;
        String whereClause = "AND obs.some_column = 'some_value'";

        when(entityManager.createQuery(anyString(), eq(Observation_Lab_Summary_ForWorkUp_New.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);

        List<Observation_Lab_Summary_ForWorkUp_New> mockResultList = new ArrayList<>();
        Observation_Lab_Summary_ForWorkUp_New labSummary = new Observation_Lab_Summary_ForWorkUp_New();
        mockResultList.add(labSummary);

        when(typedQuery.getResultList()).thenReturn(mockResultList);

        Optional<Collection<Observation_Lab_Summary_ForWorkUp_New>> result = repository.findLabSummaryForWorkupNew(personParentUid, whereClause);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(labSummary, result.get().iterator().next());
    }


    @Test
    void testFindAllActiveLabReportUidListForManage() {
        Long investigationUid = 10L;
        String whereClause = "AND some_condition = 'value'";
        String expectedSql = repository.findAllActiveLabReportUidListForManage_SQL + whereClause;

        when(entityManager.createNativeQuery(expectedSql)).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("targetActUid", investigationUid)).thenReturn(nativeQuery);

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{1L, new Timestamp(System.currentTimeMillis()), "addReasonCd1"});
        mockResultList.add(new Object[]{2L, new Timestamp(System.currentTimeMillis()), "addReasonCd2"});

        when(nativeQuery.getResultList()).thenReturn(mockResultList);

        repository.findAllActiveLabReportUidListForManage(investigationUid, whereClause);


        verify(entityManager).createNativeQuery(expectedSql);
        verify(nativeQuery).setParameter("targetActUid", investigationUid);
        verify(nativeQuery).getResultList();
    }

}
