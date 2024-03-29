package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

class ElrProcessStatusComponentTest {
    @Mock
    private ReportStatusService reportStatusServiceMock;
    @InjectMocks
    private ElrProcessStatusComponent elrProcessStatusComponent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStatusMessage() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String elrRawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        //String rawId = "test";
        MessageStatus status = new MessageStatus();
        status.getRawInfo().setRawMessageId(elrRawId);
        when(reportStatusServiceMock.getMessageStatus(elrRawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        System.out.println("processStatus:" + processStatus);
        Assertions.assertEquals(body, processStatus);
    }
}