package gov.cdc.dataingestion.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.EcrMessagePollService;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

class EcrMessagePollServiceTest {

    @Mock
    private EcrMsgQueryService ecrMsgQueryService;

    @Mock
    private ICdaMapper cdaMapper;

    @Mock
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Mock
    EcrMsgContainerDto ecrMsgContainerDto = mock(EcrMsgContainerDto.class);

    @InjectMocks
    private EcrMessagePollService ecrMessagePollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchMessageContainerDataWithValidResult() throws EcrCdaXmlException {
        EcrSelectedRecord mockRecord = mock(EcrSelectedRecord.class);
        when(mockRecord.getMsgContainer()).thenReturn(ecrMsgContainerDto);
        when(mockRecord.getMsgContainer().nbsInterfaceUid()).thenReturn(123456);
        when(mockRecord.getMsgContainer().dataMigrationStatus()).thenReturn(-2);
        when(ecrMsgQueryService.getSelectedEcrRecord()).thenReturn(List.of(mockRecord));
        when(cdaMapper.tranformSelectedEcrToCDAXml(mockRecord)).thenReturn("<xml>result</xml>");

        ecrMessagePollService.fetchMessageContainerData();

        verify(nbsRepositoryServiceProvider, times(1))
                .saveEcrCdaXmlMessage("123456", -2, "<xml>result</xml>");
    }

    @Test
    void testFetchMessageContainerDataWithNullResult() throws EcrCdaXmlException {
        when(ecrMsgQueryService.getSelectedEcrRecord()).thenReturn(new ArrayList<>());

        ecrMessagePollService.fetchMessageContainerData();

        verify(cdaMapper, never()).tranformSelectedEcrToCDAXml(any());
        verify(nbsRepositoryServiceProvider, never()).saveEcrCdaXmlMessage(anyString(), anyInt(), anyString());
    }

    @Test
    void testFetchMessageContainerDataWithEcrCdaXmlException() throws EcrCdaXmlException {
        EcrSelectedRecord mockRecord = mock(EcrSelectedRecord.class);
        when(mockRecord.getMsgContainer()).thenReturn(ecrMsgContainerDto);
        when(mockRecord.getMsgContainer().nbsInterfaceUid()).thenReturn(123456);
        when(mockRecord.getMsgContainer().dataMigrationStatus()).thenReturn(-2);
        when(ecrMsgQueryService.getSelectedEcrRecord()).thenReturn(List.of(mockRecord));
        when(cdaMapper.tranformSelectedEcrToCDAXml(mockRecord))
                .thenThrow(new EcrCdaXmlException("Error during transformation"));

        assertThrows(EcrCdaXmlException.class, () -> ecrMessagePollService.fetchMessageContainerData());
    }
}
