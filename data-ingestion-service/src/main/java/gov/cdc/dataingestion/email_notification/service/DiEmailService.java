package gov.cdc.dataingestion.email_notification.service;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.email_notification.service.interfaces.IDiEmailService;
import org.springframework.stereotype.Service;

@Service
public class DiEmailService implements IDiEmailService {
    private static final String EMAIL_DLT_SUBJECT = "Data Ingestion Alarm Notification";
    private final AwsEmailService awsEmailService;

    public DiEmailService(AwsEmailService awsEmailService) {
        this.awsEmailService = awsEmailService;
    }

    public void sendDltEmailNotification(ElrDeadLetterModel dlt) {
        String emailTemp = getEmailTemplate(dlt);
        String textBody = getEmailTextBodyTemplate(dlt.getErrorMessageId());
        this.awsEmailService.sendEmailNotificationForDlt(textBody,emailTemp, EMAIL_DLT_SUBJECT);
    }

    private String getEmailTemplate(ElrDeadLetterModel dlt) {
        StringBuilder emailTemplate = new StringBuilder("<html>");
        emailTemplate.append("<body>");
        emailTemplate.append("<h1>Alarm Detail</h1>");
        emailTemplate.append("<p><b>status</b>: <b style=\"color: red\">ERROR</b></p>");
        emailTemplate.append("<p><b>Id</b>: ").append(dlt.getErrorMessageId()).append("</p>");
        emailTemplate.append("<p><b>Error Origin</b>: ").append(dlt.getErrorMessageSource()).append("</p>");
        emailTemplate.append("<p><b>Stack Trace</b>: ").append(dlt.getErrorStackTraceShort()).append("</p>");
        emailTemplate.append("<p><b>Error Payload</b>: ").append(dlt.getMessage()).append("</p>");
        emailTemplate.append("<p><b>For further investigation please utilize DLT API endpoints for detail inspection and re-processing the error message</b></p>");
        emailTemplate.append("</body>");
        emailTemplate.append("</html>");

        return emailTemplate.toString();
    }

    private String getEmailTextBodyTemplate(String dltId) {
        return  "Alarm Detail. Id " + dltId;
    }
}
