package gov.cdc.dataprocessing.repository.nbs.srte.repository.custom;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class SrteCustomRepositoryImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private SrteCustomRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() {
        String codeSql =
                "Select  Lab_result.LAB_RESULT_CD , lab_result_desc_txt  FROM "
                        + " Lab_result Lab_result, "
                        + " Lab_coding_system Lab_coding_system WHERE "+
                        " Lab_coding_system.laboratory_id = 'DEFAULT' and "+
                        " Lab_result.organism_name_ind = 'Y'";

        when(entityManager.createNativeQuery(codeSql)).thenReturn(query);

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{"labResultCd1", "labResultDescTxt1"});
        mockResultList.add(new Object[]{"labResultCd2", "labResultDescTxt2"});

        when(query.getResultList()).thenReturn(mockResultList);

        List<LabResult> result = repository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();

        assertEquals(2, result.size());
        assertEquals("labResultCd1", result.get(0).getLabResultCd());
        assertEquals("labResultDescTxt1", result.get(0).getLabResultDescTxt());
        assertEquals("labResultCd2", result.get(1).getLabResultCd());
        assertEquals("labResultDescTxt2", result.get(1).getLabResultDescTxt());

        verify(entityManager).createNativeQuery(codeSql);
        verify(query).getResultList();
    }



    @Test
    void testGetAllLabResultJoinWithLabCodingSystemWithOrganismNameInd_NoResults() {
        String codeSql =
                "Select  Lab_result.LAB_RESULT_CD , lab_result_desc_txt  FROM "
                        + " Lab_result Lab_result, "
                        + " Lab_coding_system Lab_coding_system WHERE "+
                        " Lab_coding_system.laboratory_id = 'DEFAULT' and "+
                        " Lab_result.organism_name_ind = 'Y'";

        when(entityManager.createNativeQuery(codeSql)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        List<LabResult> result = repository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();

        assertTrue(result.isEmpty());

        verify(entityManager).createNativeQuery(codeSql);
        verify(query).getResultList();
    }
}
