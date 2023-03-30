package gov.cdc.dataingestion.kafka.integration.service;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.validation.model.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumerService {

    @Value("${kafka.validation.topic}")
    private String validatedTopic = "";

    @Value("${kafka.fhir-conversion.topic}")
    private String convertedToFhirTopic = "";

    @Value("${kafka.raw.topic}")
    private String rawTopic = "";
    private KafkaProducerService kafkaProducerService;
    private IHL7v2Validator iHl7v2Validator;
    private IRawELRRepository iRawELRRepository;
    private IValidatedELRRepository iValidatedELRRepository;
    private IHL7ToFHIRConversion iHl7ToFHIRConversion;
    private IHL7ToFHIRRepository iHL7ToFHIRRepository;


    public KafkaConsumerService(
            IValidatedELRRepository iValidatedELRRepository,
            IRawELRRepository iRawELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7v2Validator iHl7v2Validator,
            IHL7ToFHIRConversion ihl7ToFHIRConversion,
            IHL7ToFHIRRepository iHL7ToFHIRepository) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.iHl7v2Validator = iHl7v2Validator;
        this.iHl7ToFHIRConversion = ihl7ToFHIRConversion;
        this.iHL7ToFHIRRepository = iHL7ToFHIRepository;
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(topics = "#{'${kafka.topics}'.split(',')}")
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);

        try {
            if (topic.equalsIgnoreCase(rawTopic)) {
                validationHandler(message);
            } else if (topic.equalsIgnoreCase(validatedTopic)) {
                conversionHandler(message);
            }
        } catch (Exception e) {
            log.info("Retry queue");
            // run time error then -- do retry
            throw new RuntimeException(e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // Once in DLQ -- we can save message in actual db for further analyze
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }

    private void validationHandler(String message) throws HL7Exception {
        Optional<RawERLModel> rawElrResponse = this.iRawELRRepository.findById(message);
        RawERLModel elrModel = rawElrResponse.get();
        String messageType = elrModel.getType();
        switch (messageType) {
            case KafkaHeaderValue.MessageType_HL7v2:
                ValidatedELRModel hl7ValidatedModel = iHl7v2Validator.MessageValidation(message, elrModel, validatedTopic);
                saveValidatedELRMessage(hl7ValidatedModel);
                kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic);
                break;
            case KafkaHeaderValue.MessageType_CSV:
                // ValidatedELRModel csvValidatedModel = csvValidator.ValidateCSVAgainstCVSSchema(message);
                // kafkaProducerService.sendMessageAfterValidatingMessage(csvValidatedModel, validatedTopic);
                break;
            default:
                break;
        }
    }
    private void conversionHandler(String message) {
        Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
        ValidatedELRModel validatedELRModel = validatedElrResponse.get();
        String messageType = validatedELRModel.getMessageType();
        if (messageType.equalsIgnoreCase(KafkaHeaderValue.MessageType_HL7v2)) {
            HL7ToFHIRModel convertedModel = iHl7ToFHIRConversion.ConvertHL7v2ToFhir(validatedELRModel, convertedToFhirTopic);
            // We can save off the fhir record to db here
            // once data is persisted when can get id from db and push it to producer
            iHL7ToFHIRRepository.save(convertedModel);
            kafkaProducerService.sendMessageAfterConvertedToFhirMessage(convertedModel, convertedToFhirTopic);
        } else {
            throw new UnsupportedOperationException("Invalid Message");
        }
    }

    private void saveValidatedELRMessage(ValidatedELRModel model) {
        iValidatedELRRepository.save(model);
    }

}