package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EdxEventProcessJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class EdxEventProcessRepositoryUtilTest {
    @InjectMocks
    private EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil;

    @Mock
    private EdxEventProcessJdbcRepository edxEventProcessRepository;

    @Mock
    private ActRepositoryUtil actRepositoryUtil;

    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;

    @Mock
    UidPoolManager uidPoolManager;

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
    void testInsertEventProcess() throws DataProcessingException {
        EDXEventProcessDto edxEventProcessDto = new EDXEventProcessDto();
        edxEventProcessDto.setDocEventTypeCd("DOC_TYPE");
        var uidObj = new LocalUidModel();
        uidObj.setGaTypeUid(new LocalUidGeneratorDto());
        uidObj.setClassTypeUid(new LocalUidGeneratorDto());
        when(odseIdGeneratorService.getValidLocalUid(eq(LocalIdClass.NBS_DOCUMENT), anyBoolean())).thenReturn(uidObj);

        edxEventProcessRepositoryUtil.insertEventProcess(edxEventProcessDto);

        verify(actRepositoryUtil, times(1)).insertActivityId(any(), eq(edxEventProcessDto.getDocEventTypeCd()), eq(NEDSSConstant.EVENT_MOOD_CODE));
        verify(edxEventProcessRepository, times(1)).mergeEdxEventProcess(any(EdxEventProcess.class));
    }


}
