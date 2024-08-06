package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.NNDActivityLogRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
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
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNddActivityLogWithNewUid() throws DataProcessingException {
        NNDActivityLogDto nndActivityLogDto = new NNDActivityLogDto();
        var id = new LocalUidModel();
        id .setGaTypeUid(new LocalUidGeneratorDto());
        id .setClassTypeUid(new LocalUidGeneratorDto());
        id.getClassTypeUid().setClassNameCd("CLASS");
        id.getClassTypeUid().setTypeCd("TYPE");
        id.getClassTypeUid().setUidSuffixCd("SUF");
        id.getClassTypeUid().setUidPrefixCd("PRE");
        id.getClassTypeUid().setSeedValueNbr(1L);

        id.getGaTypeUid().setClassNameCd("CLASS");
        id.getGaTypeUid().setTypeCd("TYPE");
        id.getGaTypeUid().setUidSuffixCd("SUF");
        id.getGaTypeUid().setUidPrefixCd("PRE");
        id.getGaTypeUid().setSeedValueNbr(1L);
        when(odseIdGeneratorService.getValidLocalUid(eq(NND_METADATA), anyBoolean())).thenReturn(id);

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
        when(odseIdGeneratorService.getValidLocalUid(NND_METADATA, true)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> {
            nndActivityLogService.saveNddActivityLog(nndActivityLogDto);
        });
    }


}