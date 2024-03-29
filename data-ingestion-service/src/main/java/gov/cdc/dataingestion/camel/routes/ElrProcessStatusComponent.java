package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityLogStatus;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.model.NbsMessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElrProcessStatusComponent {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessComponent.class);
    private ReportStatusService reportStatusService;

    private String SUCCESS = "Success";
    private String FAILURE = "Failure";
    private static final String IN_PROGRESS = "InProgress";

    @Autowired
    public ElrProcessStatusComponent(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @Handler
    public String process(String body) throws Exception {
        String status = "";
        try {
            logger.debug("Calling ElrProcessStatusComponent");
            logger.debug("File name and Elr Id:{}", body);
            if (body != null && !body.trim().isEmpty()) {
                //String body = "HL7file-sftpstatus1.txt:7DAC34BD-B011-469A-BF27-25904370E9E3";
                String[] fileNameElr = body.split(":");
                System.out.println("file name:" + fileNameElr[0] + " " + "Elr id:" + fileNameElr[1]);
                String fileName = fileNameElr[0];
                String elrId = fileNameElr[1].trim();
                MessageStatus messageStatus = reportStatusService.getMessageStatus(elrId);
                NbsMessageStatus nbsMessageStatus = messageStatus.getNbsInfo();
                if (nbsMessageStatus.getNbsInterfaceStatus() != null && nbsMessageStatus.getNbsInterfaceStatus().equals(SUCCESS)) {
                    status = SUCCESS;
                    System.out.println("--1111 nbs success ElrProcessStatusComponent status:" + status);
                } else if (nbsMessageStatus.getNbsInterfaceStatus() != null && nbsMessageStatus.getNbsInterfaceStatus().equals(FAILURE)) {
                    EdxActivityLogStatus edxActivityLogStatus = messageStatus.getOdseActivityLogStatus();
                    status = "Status: Failure " + "\nRecord Id: " + edxActivityLogStatus.getRecordId()
                            + " \nRecordType: " + edxActivityLogStatus.getRecordType() + " \nLog Type: "
                            + edxActivityLogStatus.getLogType() + " \nLog Comment: " + edxActivityLogStatus.getLogComment() + " \nELR Id:" + elrId;
                    System.out.println("--22222 nbs failure ElrProcessStatusComponent status:" + status);
                } else if (nbsMessageStatus.getDltInfo() != null) {
                    status = "Status: Validation Error \nError Message: " + nbsMessageStatus.getDltInfo().getDltShortTrace()
                            + " \nCreated On: " + nbsMessageStatus.getDltInfo().getDltCreatedOn() + " \nELR Id:" + elrId;
                    System.out.println("--33333 dlt nbs ElrProcessStatusComponent status:" + status);
                } else if (messageStatus.getRawInfo().getDltInfo() != null) {
                    status = "Status: Validation Error \nError Message: " + messageStatus.getRawInfo().getDltInfo().getDltShortTrace()
                            + " \nCreated On: " + messageStatus.getRawInfo().getDltInfo().getDltCreatedOn() + " \nELR Id:" + elrId;
                    System.out.println("--44444 dlt raw ElrProcessStatusComponent status:" + status);
                } else {
                    status = body;
                    System.out.println("--55555 in progress ElrProcessStatusComponent status:" + status);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        System.out.println("ElrProcessStatusComponent status:" + status);
        return status;
    }
}