package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.reportstatus.model.EdxActivityLogStatus;
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
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class ElrProcessStatusComponent {
    private static Logger logger = LoggerFactory.getLogger(ElrProcessStatusComponent.class);
    private ReportStatusService reportStatusService;

    private static final String SUCCESS = "Success";
    private static final String FAILURE = "Failure";
    private static final String MSG_STATUS_FAILED = "FAILED";
    private static final String ELR_ID = "ELR Id";
    private static final String STATUS_VALIDATION_ERROR ="Status: Validation Error";
    private static final String CREATED_ON ="Created On";
    private static final String ERROR_MESSAGE ="Error Message";

    @Autowired
    public ElrProcessStatusComponent(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @SuppressWarnings({"java:S3776"})
    @Handler
    public String process(String body){
        String status = "";
            logger.debug("File name and Elr Id:{}", body);
            if (body != null && !body.trim().isEmpty()) {
                //body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";//NOSONAR
                String[] fileNameElr = body.split(":");
                String elrId = fileNameElr[1].trim();
                MessageStatus messageStatus = reportStatusService.getMessageStatus(elrId);
                if (messageStatus.getNbsInfo().getNbsInterfaceStatus() != null && messageStatus.getNbsInfo().getNbsInterfaceStatus().equals(SUCCESS)) {
                    status = SUCCESS;
                } else if (messageStatus.getNbsInfo().getNbsInterfaceStatus() != null && messageStatus.getNbsInfo().getNbsInterfaceStatus().equals(FAILURE)) {
                    StringBuilder activityLogSb = new StringBuilder();
                    activityLogSb.append("Status: Failure ");
                    List<EdxActivityLogStatus> edxActivityLogList= messageStatus.getNbsIngestionInfo();
                    for(EdxActivityLogStatus edxActivityLogStatus:edxActivityLogList){
                        String logComment=edxActivityLogStatus.getLogComment();
                        if(logComment!=null && logComment.length()>200){
                            logComment=logComment.substring(0,200);
                        }
                        String activityLog = "\n\nRecordType: " + edxActivityLogStatus.getRecordType() + " \nLog Type: "
                                + edxActivityLogStatus.getLogType() + " \nLog Comment: " + logComment
                                + " \nRecord Status Time: " + edxActivityLogStatus.getRecordStatusTime();
                        activityLogSb.append(activityLog);
                    }
                    activityLogSb.append(" \n\n"+ELR_ID+": " + elrId+" \n");
                    status=activityLogSb.toString();
                } else if (messageStatus.getNbsInfo().getNbsInterfacePipeLineStatus() != null
                        && messageStatus.getNbsInfo().getNbsInterfacePipeLineStatus().equals(MSG_STATUS_FAILED)) {
                    status = STATUS_VALIDATION_ERROR+" \n"+ERROR_MESSAGE+": " + messageStatus.getValidatedInfo().getDltInfo().getDltShortTrace()
                            + " \n"+CREATED_ON+": " + messageStatus.getValidatedInfo().getDltInfo().getDltCreatedOn()
                            + " \n"+ELR_ID+": " + elrId;
                } else if (messageStatus.getValidatedInfo().getValidatedPipeLineStatus() != null
                        && messageStatus.getValidatedInfo().getValidatedPipeLineStatus().equals(MSG_STATUS_FAILED)) {
                    status = STATUS_VALIDATION_ERROR+" \n"+ERROR_MESSAGE+": " + messageStatus.getRawInfo().getDltInfo().getDltShortTrace()
                            + " \n"+CREATED_ON+": " + messageStatus.getRawInfo().getDltInfo().getDltCreatedOn()
                            + " \n"+ELR_ID+": " + elrId;
                } else {
                    status = body;
                }
            }
        logger.info("ElrProcessStatusComponent status:{}", status);
        return status;
    }
}