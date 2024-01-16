package gov.cdc.dataingestion.email_notification.service;

import gov.cdc.dataingestion.email_notification.service.interfaces.IAwsEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@Service
public class AwsEmailService implements IAwsEmailService {
    private final SesClient sesClient;

    private Region awsRegion;

    @Value("${aws.ses.email.source}")
    private String sourceEmail;
    @Value("${aws.ses.email.recipients}")
    private List<String> recipients;


    public AwsEmailService(@Value("${aws.authentication.access}") String accessKey,
                           @Value("${aws.authentication.secret}") String secretKey,
                           @Value("${aws.authentication.token}") String token,
                           @Value("${aws.authentication.region}") String region) {
        AwsCredentialsProvider awsCredentialsProvider;
        if (token.isEmpty()) {
            awsCredentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            );
        } else {
            awsCredentialsProvider = StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(accessKey, secretKey, token)
            );
        }

        awsRegion = Region.of(region);
        this.sesClient = SesClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(awsRegion)
                .build();
    }

    @SuppressWarnings("java:S6244")
    public void sendEmailNotificationForDlt(
            String textBody,
            String htmlBody,
            String subject) {
        Destination destination = Destination.builder()
                .toAddresses(recipients)
                .build();

        Content subjectContent = Content.builder().data(subject).build();

        Content htmlContent = Content.builder()
                .data(htmlBody)
                .charset("UTF-8")
                .build();

        Content textContent = Content.builder()
                .data(textBody)
                .charset("UTF-8")
                .build();

        Body body = Body.builder()
                .html(htmlContent)
                .text(textContent)
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(body)
                .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .message(message)
                .source(sourceEmail)
                .build();

        sesClient.sendEmail(emailRequest);
    }
}