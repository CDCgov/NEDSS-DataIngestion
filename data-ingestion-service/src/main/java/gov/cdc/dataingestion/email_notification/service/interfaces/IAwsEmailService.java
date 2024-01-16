package gov.cdc.dataingestion.email_notification.service.interfaces;

public interface IAwsEmailService {
    void sendEmailNotificationForDlt(String textBody,
                                     String htmlBody,
                                     String subject);

}
