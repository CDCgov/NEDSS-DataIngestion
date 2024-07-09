package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.NNDActivityLogRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.NND_METADATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NNDActivityLogServiceTest {

    @InjectMocks
    private NNDActivityLogService nndActivityLogService;

    @Mock
    private NNDActivityLogRepository nndActivityLogRepository;

    @Mock
    private OdseIdGeneratorService odseIdGeneratorService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNddActivityLogWithNewUid() throws DataProcessingException {
        NNDActivityLogDto nndActivityLogDto = new NNDActivityLogDto();
        var id = new LocalUidGenerator();
        id.setClassNameCd("CLASS");
        id.setTypeCd("TYPE");
        id.setUidSuffixCd("SUF");
        id.setUidPrefixCd("PRE");
        id.setSeedValueNbr(1L);
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(NND_METADATA)).thenReturn(id);

        nndActivityLogService.saveNddActivityLog(nndActivityLogDto);

        assertNotNull(nndActivityLogDto.getNndActivityLogUid());
        assertEquals("AUTO_RESEND_ERROR", nndActivityLogDto.getRecordStatusCd());
        assertEquals("E", nndActivityLogDto.getStatusCd());
        verify(nndActivityLogRepository, times(1)).save(any(NNDActivityLog.class));
    }

    @Test
    void testSaveNddActivityLogWithExistingUid() throws DataProcessingException {
        NNDActivityLogDto nndActivityLogDto = new NNDActivityLogDto();
        nndActivityLogDto.setNndActivityLogUid(2L);

        nndActivityLogService.saveNddActivityLog(nndActivityLogDto);

        assertEquals(2L, nndActivityLogDto.getNndActivityLogUid());
        assertEquals("AUTO_RESEND_ERROR", nndActivityLogDto.getRecordStatusCd());
        assertEquals("E", nndActivityLogDto.getStatusCd());
        verify(nndActivityLogRepository, times(1)).save(any(NNDActivityLog.class));
    }

    @Test
    void testSaveNddActivityLogException() throws DataProcessingException {
        NNDActivityLogDto nndActivityLogDto = new NNDActivityLogDto();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(NND_METADATA)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> {
            nndActivityLogService.saveNddActivityLog(nndActivityLogDto);
        });
    }


}