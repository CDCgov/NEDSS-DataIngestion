package gov.cdc.dataprocessing.service.implementation.dead_letter;


import gov.cdc.dataprocessing.model.dto.dead_letter.RtiDltDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RtiDltJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DpDeadLetterServiceTest {

    @Mock
    private RtiDltJdbcRepository rtiDltJdbcRepository;

    @InjectMocks
    private DpDeadLetterService dpDeadLetterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findDltRecords_withNullInterfaceUid_callsFindByUnSuccessStatus() {
        RtiDlt dlt = new RtiDlt();
        when(rtiDltJdbcRepository.findByUnSuccessStatus()).thenReturn(List.of(dlt));

        List<RtiDltDto> result = dpDeadLetterService.findDltRecords(null);

        assertEquals(1, result.size());
        verify(rtiDltJdbcRepository).findByUnSuccessStatus();
    }

    @Test
    void findDltRecords_withInterfaceUid_callsFindByNbsInterfaceId() {
        Long interfaceUid = 123L;
        RtiDlt dlt = new RtiDlt();
        when(rtiDltJdbcRepository.findByNbsInterfaceId(interfaceUid)).thenReturn(List.of(dlt));

        List<RtiDltDto> result = dpDeadLetterService.findDltRecords(interfaceUid);

        assertEquals(1, result.size());
        verify(rtiDltJdbcRepository).findByNbsInterfaceId(interfaceUid);
    }

    @Test
    void findDltRecords_withNoResults_returnsEmptyList() {
        when(rtiDltJdbcRepository.findByUnSuccessStatus()).thenReturn(Collections.emptyList());

        List<RtiDltDto> result = dpDeadLetterService.findDltRecords(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void saveRtiDlt_withAllFieldsIncludingId_setsAllValuesAndCallsUpsert() {
        Exception exception = new RuntimeException("Something went wrong");
        String payload = "samplePayload";
        String step = "validationStep";
        String status = "FAILED";
        String id = "abc123";
        Long interfaceUid = 456L;

        dpDeadLetterService.saveRtiDlt(exception, interfaceUid, payload, step, status, id);

        ArgumentCaptor<RtiDlt> captor = ArgumentCaptor.forClass(RtiDlt.class);
        verify(rtiDltJdbcRepository).upsert(captor.capture());

        RtiDlt saved = captor.getValue();
        assertEquals(id, saved.getId());
        assertEquals(interfaceUid, saved.getNbsInterfaceId());
        assertEquals(payload, saved.getPayload());
        assertEquals(step, saved.getOrigin());
        assertEquals(status, saved.getStatus());
        assertTrue(saved.getStackTrace().contains("Something went wrong"));
    }

    @Test
    void saveRtiDlt_withNullId_stillCallsUpsert() {
        dpDeadLetterService.saveRtiDlt(new Exception("ex"), 1L, "payload", "step", "status", null);

        verify(rtiDltJdbcRepository).upsert(any(RtiDlt.class));
    }
}