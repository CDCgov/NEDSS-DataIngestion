package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.reportstatus.model.DltMessageStatus;
import gov.cdc.dataingestion.reportstatus.model.EdxActivityLogStatus;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import gov.cdc.dataingestion.share.helper.TimeStampHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class ElrProcessStatusComponentTest {
    @Mock
    private ReportStatusService reportStatusServiceMock;
    @InjectMocks
    private ElrProcessStatusComponent elrProcessStatusComponent;

    private static final String MSG_STATUS_FAILED = "FAILED";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessForInProgress() {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);

        status.getRawInfo().setRawMessageId(rawId);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(msgStatusList);
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertEquals(body, processStatus);
    }
    @Test
    void testProcessForNbsSucsess() {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Success");
        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);

        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(msgStatusList);
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.startsWith("Status: Success"));
    }
    @Test
    void testProcessForNbsFailure(){
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Failure");
        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);

        EdxActivityLogStatus edxActivityLogStatus=new EdxActivityLogStatus();
        edxActivityLogStatus.setRecordType("Test Record Type");
        edxActivityLogStatus.setLogType("Test Log Type");
        edxActivityLogStatus.setLogComment("Test Log Comment");
        edxActivityLogStatus.setRecordStatusTime(TimeStampHelper.convertTimestampToString(TimeStampHelper.getCurrentTimeStamp("UTC")));
//        status.getNbsIngestionInfo().add(edxActivityLogStatus);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                msgStatusList
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForNbsFailureWithLogCommentWithMorethan200(){
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Failure");

        String logComment= "Test Log Comment123 Test Log Comment123 Test Log Comment123 Test Log Comment234 Test Log Comment345 Test Log Comment456 Test Log Comment567 Test Log Comment678 Test Log Comment678 Test Log Comment78901";
        EdxActivityDetailLog edxActivityLogStatus=new EdxActivityDetailLog();
        edxActivityLogStatus.setRecordType("Test Record Type");
        edxActivityLogStatus.setLogType("Test Log Type");
        edxActivityLogStatus.setLogComment(logComment);
        status.getEdxLogStatus().getEdxActivityDetailLogList().add(edxActivityLogStatus);
        var edx = new EdxActivityLog();
        edx.setRecordStatusTime(TimeStampHelper.convertTimestampToString(TimeStampHelper.getCurrentTimeStamp("UTC")));
        status.getEdxLogStatus().setEdxActivityLog(edx);

        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                msgStatusList
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForNbsFailureWithLogComment_null(){
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getNbsInfo().setNbsInterfaceStatus("Failure");
         EdxActivityDetailLog edxActivityLogStatus=new EdxActivityDetailLog();
        edxActivityLogStatus.setRecordType("Test Record Type");
        edxActivityLogStatus.setLogType("Test Log Type");
        edxActivityLogStatus.setLogComment(null);
        status.getEdxLogStatus().getEdxActivityDetailLogList().add(edxActivityLogStatus);
        var edx = new EdxActivityLog();
        edx.setRecordStatusTime(TimeStampHelper.convertTimestampToString(TimeStampHelper.getCurrentTimeStamp("UTC")));
        status.getEdxLogStatus().setEdxActivityLog(edx);

        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);

        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                msgStatusList
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForNbsValidationFailed() {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getValidatedInfo().setDltInfo(new DltMessageStatus());
        status.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_FAILED);

        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                msgStatusList
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
    @Test
    void testProcessForDIValidationFailed() {
        String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
        String rawId = "7DAC34BD-B011-469A-BF27-25904370E9E3";

        MessageStatus status = new MessageStatus();
        status.getRawInfo().setDltInfo(new DltMessageStatus());
        status.getValidatedInfo().setValidatedPipeLineStatus(MSG_STATUS_FAILED);

        List<MessageStatus> msgStatusList= new ArrayList<>();
        msgStatusList.add(status);

        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                msgStatusList
        );
        String processStatus = elrProcessStatusComponent.process(body);
        Assertions.assertTrue(processStatus.contains("Status:"));
    }
}