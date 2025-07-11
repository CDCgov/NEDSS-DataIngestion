package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
public class ElrProcessStatusComponent {
    private static Logger logger = LoggerFactory.getLogger(ElrProcessStatusComponent.class);
    private ReportStatusService reportStatusService;

    private static final String SUCCESS = "Success";
    private static final String FAILURE = "Failure";
    private static final String MSG_STATUS_FAILED = "FAILED";
    private static final String ELR_ID = "ELR Id";
    private static final String NBS_INTERFACE_ID = "NBS Interface Id";
    private static final String STATUS_VALIDATION_ERROR = "Status: Validation Error";
    private static final String CREATED_ON = "Created On";
    private static final String ERROR_MESSAGE = "Error Message";
    private static final String DOUBLR_LINE_BREAK = " \n\n";
    private static final String EMPTY_LINE = " \n----------------- \n";

    @Autowired
    public ElrProcessStatusComponent(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @SuppressWarnings({"java:S3776","java:S1119"})
    @Handler
    public String process(String body) {
        String elrProcessStatus = "";
        logger.debug("File name and Elr Id:{}", body);
        if (body != null && !body.trim().isEmpty() && body.split(":").length == 2) {
            //body = "HL7file-sftpstatus1.txt:64BC53EC-E263-438E-AC2D-0E399E67510B,38CABB77-AF83-4DFE-AA45-07BFEF932051";//NOSONAR
            String[] fileNameElr = body.split(":");
            String elrIds = fileNameElr[1].trim();
            String[] elrIdArr = elrIds.split(",");
            StringBuilder statusSb = new StringBuilder();
            outerLoop:
            for (String elrId : elrIdArr) {
                List<MessageStatus> msgStatusList = reportStatusService.getMessageStatus(elrId);
                for (MessageStatus messageStatus : msgStatusList) {
                    String status = "";
                    if (messageStatus.getNbsInfo().getNbsInterfaceStatus() != null && messageStatus.getNbsInfo().getNbsInterfaceStatus().equals(SUCCESS)) {
                        status = "Status: Success " +
                                DOUBLR_LINE_BREAK + NBS_INTERFACE_ID + ": " + messageStatus.getNbsInfo().getNbsInterfaceId() +
                                DOUBLR_LINE_BREAK + ELR_ID + ": " + elrId +
                                EMPTY_LINE;
                    } else if (messageStatus.getNbsInfo().getNbsInterfaceStatus() != null && messageStatus.getNbsInfo().getNbsInterfaceStatus().equals(FAILURE)) {
                        StringBuilder activityLogSb = new StringBuilder();
                        activityLogSb.append("Status: Failure ");
                        activityLogSb.append(DOUBLR_LINE_BREAK).append(NBS_INTERFACE_ID).append(": ").append(messageStatus.getNbsInfo().getNbsInterfaceId());
                        activityLogSb.append(DOUBLR_LINE_BREAK).append(ELR_ID).append(": ").append(elrId);
                        List<EdxActivityDetailLog> edxActivityLogList = messageStatus.getEdxLogStatus().getEdxActivityDetailLogList();
                        EdxActivityLog edxActivityLogParent = messageStatus.getEdxLogStatus().getEdxActivityLog();
                        for (EdxActivityDetailLog edxActivityLogStatus : edxActivityLogList) {
                            String logComment = edxActivityLogStatus.getLogComment();
                            if (logComment != null && logComment.length() > 200) {
                                logComment = logComment.substring(0, 200);
                            }
                            String activityLog = "\n\nRecordType: " + edxActivityLogStatus.getRecordType() + " \nLog Type: "
                                    + edxActivityLogStatus.getLogType() + " \nLog Comment: " + logComment
                                    + " \nRecord Status Time: " + edxActivityLogParent.getRecordStatusTime();
                            activityLogSb.append(activityLog);
                            activityLogSb.append(EMPTY_LINE);
                        }
                        status = activityLogSb.toString();
                    } else if (messageStatus.getNbsInfo().getNbsInterfacePipeLineStatus() != null
                            && messageStatus.getNbsInfo().getNbsInterfacePipeLineStatus().equals(MSG_STATUS_FAILED)) {
                        status = STATUS_VALIDATION_ERROR + " \n" + ERROR_MESSAGE + ": " + messageStatus.getValidatedInfo().getDltInfo().getDltShortTrace()
                                + " \n" + CREATED_ON + ": " + messageStatus.getValidatedInfo().getDltInfo().getDltCreatedOn()
                                + " \n" + ELR_ID + ": " + elrId
                                + EMPTY_LINE;
                    } else if (messageStatus.getValidatedInfo().getValidatedPipeLineStatus() != null
                            && messageStatus.getValidatedInfo().getValidatedPipeLineStatus().equals(MSG_STATUS_FAILED)) {
                        status = STATUS_VALIDATION_ERROR + " \n" + ERROR_MESSAGE + ": " + messageStatus.getRawInfo().getDltInfo().getDltShortTrace()
                                + " \n" + CREATED_ON + ": " + messageStatus.getRawInfo().getDltInfo().getDltCreatedOn()
                                + " \n" + ELR_ID + ": " + elrId
                                + EMPTY_LINE;
                    } else {
                        statusSb.setLength(0);
                        statusSb.append(body);
                        break outerLoop;
                    }
                    statusSb.append(status);
                }
            }
             elrProcessStatus=statusSb.toString();
        }
        logger.debug("ElrProcessStatusComponent status:{}", elrProcessStatus);
        return elrProcessStatus;
    }
}