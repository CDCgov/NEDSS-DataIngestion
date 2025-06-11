package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.NNDActivityLogRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
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

    @Mock
    private UidPoolManager uidPoolManager;


    @BeforeEach
    public void setUp() throws DataProcessingException {
        MockitoAnnotations.openMocks(this);
        var model = new LocalUidModel();
        LocalUidGeneratorDto dto = new LocalUidGeneratorDto();
        dto.setClassNameCd("TEST");
        dto.setTypeCd("TEST");
        dto.setUidPrefixCd("TEST");
        dto.setUidSuffixCd("TEST");
        dto.setSeedValueNbr(1L);
        dto.setCounter(3);
        dto.setUsedCounter(2);
        model.setClassTypeUid(dto);
        model.setGaTypeUid(dto);
        model.setPrimaryClassName("TEST");
        when(uidPoolManager.getNextUid(any(), anyBoolean())).thenReturn(model);
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



}