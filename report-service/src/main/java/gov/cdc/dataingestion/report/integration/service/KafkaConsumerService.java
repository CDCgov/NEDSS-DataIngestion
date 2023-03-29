package gov.cdc.dataingestion.report.integration.service;
import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.report.repository.IConvertedFhirRepository;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.IReportRepository;
import gov.cdc.dataingestion.report.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.report.repository.model.HL7toFhirModel;
import gov.cdc.dataingestion.report.repository.model.ValidatedELRModel;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumerService {

    private IConvertedFhirRepository iConvertedFhirRepository;
    private IRawElrRepository iRawElrRepository;
    private IValidatedELRRepository iValidatedELRRepository;
    private IHL7ToFHIRConversion hl7ToFHIRConversion;

    private KafkaProducerService kafkaProducerService;
    private String HL7v2 = "HL7";
    @Value("${kafka.fhir-conversion.producer.topic}")
    private String convertedToFhirTopic = "";
    public KafkaConsumerService(IHL7ToFHIRConversion hl7ToFHIRConversion,
                                IConvertedFhirRepository iConvertedFhirRepository,
                                IRawElrRepository iRawElrRepository,
                                IValidatedELRRepository iValidatedELRRepository,
                                KafkaProducerService kafkaProducerService) {
        this.hl7ToFHIRConversion = hl7ToFHIRConversion;
        this.iConvertedFhirRepository = iConvertedFhirRepository;
        this.iRawElrRepository = iRawElrRepository;
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @RetryableTopic(
            attempts = "${kafka.fhir-conversion.consumer.max-retry}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(id = "${kafka.fhir-conversion.consumer.group-id}", topics = "${kafka.validation.producer.topic}")
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);
        String messageType = null;
        try {
            Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
            ValidatedELRModel validatedELRModel = validatedElrResponse.get();
            messageType = validatedELRModel.getMessageType();
            if (messageType.equalsIgnoreCase(HL7v2)) {
                HL7toFhirModel convertedModel = hl7ToFHIRConversion.ConvertHL7v2ToFhir(validatedELRModel, convertedToFhirTopic);
                saveConvertedFhirLRMessage(convertedModel);
                kafkaProducerService.sendMessageAfterConvertedToFhirMessage(convertedModel, convertedToFhirTopic);
            } else {
                throw new UnsupportedOperationException("Invalid Message");
            }
        } catch (Exception e) {
            log.info("Retry queue");
            throw new RuntimeException(e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // Once in DLQ -- we can save message in actual db for further analyze
        // Need specification with DLT - what do we do with these messages
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }

    private void saveConvertedFhirLRMessage(HL7toFhirModel model) {
        iConvertedFhirRepository.save(model);
    }
}
