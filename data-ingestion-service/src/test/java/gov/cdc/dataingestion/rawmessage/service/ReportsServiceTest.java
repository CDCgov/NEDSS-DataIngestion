package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportsServiceTest {

    @Mock
    private NbsInterfaceRepository nbsInterfaceRepo;

    @InjectMocks
    private ReportsService reportsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSubmissionsWithReports() {
        List<NbsInterfaceModel> mockReports = new ArrayList<>();
        NbsInterfaceModel mockReport = new NbsInterfaceModel();
        mockReport.setNbsInterfaceUid(12345);
        mockReports.add(mockReport);

        when(nbsInterfaceRepo.findAll()).thenReturn(mockReports);

        List<NbsInterfaceModel> result = reportsService.getAllSubmissions();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(12345, result.getFirst().getNbsInterfaceUid());

        verify(nbsInterfaceRepo, times(1)).findAll();
    }

    @Test
    void testGetAllSubmissionsWithNoReports() {
        List<NbsInterfaceModel> mockReports = Collections.emptyList();

        when(nbsInterfaceRepo.findAll()).thenReturn(mockReports);

        List<NbsInterfaceModel> result = reportsService.getAllSubmissions();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(nbsInterfaceRepo, times(1)).findAll();
    }

    @Test
    void testGetAllSubmissionsWithRepositoryException() {
        when(nbsInterfaceRepo.findAll()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> reportsService.getAllSubmissions());

        verify(nbsInterfaceRepo, times(1)).findAll();
    }
}
