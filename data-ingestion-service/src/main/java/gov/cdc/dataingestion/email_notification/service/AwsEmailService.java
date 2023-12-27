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
import java.util.function.Consumer;

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

    public void sendEmailNotificationForDlt(
            String textBody,
            String htmlBody,
            String subject) {
        Destination destination = Destination.builder()
                .toAddresses(recipients)
                .build();

        Content subjectContent = Content.builder().data(subject).build();

        Consumer<Content.Builder> htmlContentBuilder = builder -> builder
                .data(htmlBody)
                .charset("UTF-8");

        Consumer<Content.Builder> textContentBuilder = builder -> builder
                .data(textBody)
                .charset("UTF-8");

        Body body = Body.builder()
                .html(htmlContentBuilder)
                .text(textContentBuilder)
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(body)
                .build();

        Consumer<SendEmailRequest.Builder> requestBuilder = builder -> builder
                .destination(destination)
                .message(message)
                .source(sourceEmail);

        SendEmailRequest emailRequest = buildRequest(requestBuilder);

        sesClient.sendEmail(emailRequest);
    }

    private SendEmailRequest buildRequest(Consumer<SendEmailRequest.Builder> requestBuilder) {
        SendEmailRequest.Builder builder = SendEmailRequest.builder();
        requestBuilder.accept(builder);
        return builder.build();
    }
}