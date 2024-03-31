package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.reportstatus.model.DltMessageStatus;
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

    private static final String MSG_STATUS_FAILED = "FAILED";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessForInProgress() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getRawInfo().setRawMessageId(rawId);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertEquals(body, processStatus);
    }
    @Test
    void testProcessForNbsSucsess() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Success");
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertEquals("Success", processStatus);
    }
    @Test
    void testProcessForNbsFailure() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Failure");
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForNbsValidationFailed() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getValidatedInfo().setDltInfo(new DltMessageStatus());
        status.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_FAILED);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForDIValidationFailed() throws Exception {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getRawInfo().setDltInfo(new DltMessageStatus());
        status.getValidatedInfo().setValidatedPipeLineStatus(MSG_STATUS_FAILED);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
}