package gov.cdc.dataingestion.kafka.integration.service;


import ca.uhn.hl7v2.HL7Exception;
import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.email_notification.service.interfaces.IDiEmailService;
import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.exception.XmlConversionException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import jakarta.xml.bind.JAXBException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class KafkaAlarmConsumerService {

    @Value("${kafka.dlt-alarm-notification.topic}")
    private String notificationTopic = "dlt_alarm_notification";

    @Value("${kafka.dlt-alarm-notification-email-aws.topic}")
    private String notificationTopicEmailAws = "dlt_alarm_notification_email_aws";

    @Value("${notification-config.email.aws}")
    private boolean emailAwsApplied = false;

    @Value("${notification-config.email.azure}")
    private boolean emailAzureApplied = false;

    @Value("${notification-config.chat.slack}")
    private boolean chatSlackApplied = false;

    private final KafkaProducerService kafkaProducerService;
    private final IDiEmailService diEmailService;

    public KafkaAlarmConsumerService(
            KafkaProducerService kafkaProducerService,
            IDiEmailService diEmailService
    ) {
        this.kafkaProducerService = kafkaProducerService;
        this.diEmailService = diEmailService;
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            autoCreateTopics = "false",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            retryTopicSuffix = "${kafka.retry.suffix}",
            dltTopicSuffix = "${kafka.dlt.suffix}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    DuplicateHL7FileFoundException.class,
                    DiHL7Exception.class,
                    HL7Exception.class,
                    XmlConversionException.class,
                    JAXBException.class
            }

    )
    @KafkaListener(
            topics = "${kafka.dlt-alarm-notification.topic}"
    )
    public void handleMessageForNotification(String message)
    {
        // Handle multiple notification service here
        // There could be multiple service such as email (aws, azure, or native), 3rd party tool (slack, ms team)
        if (emailAwsApplied) {
            this.kafkaProducerService.sendMessageToNotificationEmailTopic(message, notificationTopicEmailAws);
        }

        if (emailAzureApplied) {
            //TODO: azure producer and downstream logic can go here
        }

        if(chatSlackApplied) {
            //TODO: slack producer and downstream logic can go here
        }
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            autoCreateTopics = "false",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            retryTopicSuffix = "${kafka.retry.suffix}",
            dltTopicSuffix = "${kafka.dlt.suffix}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    DuplicateHL7FileFoundException.class,
                    DiHL7Exception.class,
                    HL7Exception.class,
                    XmlConversionException.class,
                    JAXBException.class
            }

    )
    @KafkaListener(
            topics = "${kafka.dlt-alarm-notification-email-aws.topic}"
    )
    public void handleMessageForEmailAws(String message)
    {
        Gson gson = new Gson();
        ElrDeadLetterModel dltObj = gson.fromJson(message, ElrDeadLetterModel.class);
        this.diEmailService.sendDltEmailNotification(dltObj);
    }
}
