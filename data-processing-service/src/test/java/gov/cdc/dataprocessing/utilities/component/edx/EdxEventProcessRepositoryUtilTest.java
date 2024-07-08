package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxEventProcessRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EdxEventProcessRepositoryUtilTest {
    @InjectMocks
    private EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil;

    @Mock
    private EdxEventProcessRepository edxEventProcessRepository;

    @Mock
    private ActRepositoryUtil actRepositoryUtil;

    @Mock
    private OdseIdGeneratorService odseIdGeneratorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertEventProcess() throws DataProcessingException {
        EDXEventProcessDto edxEventProcessDto = new EDXEventProcessDto();
        edxEventProcessDto.setDocEventTypeCd("DOC_TYPE");
        var uidObj = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.NBS_DOCUMENT)).thenReturn(uidObj);

        edxEventProcessRepositoryUtil.insertEventProcess(edxEventProcessDto);

        verify(actRepositoryUtil, times(1)).insertActivityId(uidObj.getSeedValueNbr(), edxEventProcessDto.getDocEventTypeCd(), NEDSSConstant.EVENT_MOOD_CODE);
        verify(edxEventProcessRepository, times(1)).save(any(EdxEventProcess.class));
    }

    @Test
    void testInsertEventProcessThrowsException() throws DataProcessingException {
        EDXEventProcessDto edxEventProcessDto = new EDXEventProcessDto();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.NBS_DOCUMENT)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> {
            edxEventProcessRepositoryUtil.insertEventProcess(edxEventProcessDto);
        });
    }

}
