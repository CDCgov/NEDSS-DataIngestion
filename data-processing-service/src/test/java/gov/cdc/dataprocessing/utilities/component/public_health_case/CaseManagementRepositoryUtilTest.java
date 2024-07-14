package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.CaseManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CaseManagementRepositoryUtilTest {

    @Mock
    private CaseManagementRepository caseManagementRepository;

    @InjectMocks
    private CaseManagementRepositoryUtil caseManagementRepositoryUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCaseManagementPhc_shouldReturnNull_whenRepositoryReturnsEmpty() {
        Long phcUid = 1L;
        when(caseManagementRepository.findRecordsByPhcUid(phcUid)).thenReturn(Optional.empty());

        CaseManagementDto result = caseManagementRepositoryUtil.getCaseManagementPhc(phcUid);

        assertNull(result);
        verify(caseManagementRepository, times(1)).findRecordsByPhcUid(phcUid);
    }

    @Test
    void getCaseManagementPhc_shouldReturnDto_whenRepositoryReturnsNonEmpty() {
        Long phcUid = 1L;
        CaseManagement dto = new CaseManagement();
        Collection<CaseManagement> lst = List.of(dto);
        when(caseManagementRepository.findRecordsByPhcUid(phcUid)).thenReturn(Optional.of(lst));

        CaseManagementDto result = caseManagementRepositoryUtil.getCaseManagementPhc(phcUid);

        assertNotNull(result);
        verify(caseManagementRepository, times(1)).findRecordsByPhcUid(phcUid);
    }

    @Test
    void getCaseManagementPhc_shouldReturnEmptyDto_whenRepositoryReturnsEmptyDto() {
        Long phcUid = 1L;
        when(caseManagementRepository.findRecordsByPhcUid(phcUid)).thenReturn(Optional.of(List.of()));

        CaseManagementDto result = caseManagementRepositoryUtil.getCaseManagementPhc(phcUid);

        assertNotNull(result);
        assertNull(result.getPublicHealthCaseUid());
        verify(caseManagementRepository, times(1)).findRecordsByPhcUid(phcUid);
    }
}
