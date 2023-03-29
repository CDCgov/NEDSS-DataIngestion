package gov.cdc.dataingestion.validation.integration.service;

import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumerService
{

    private KafkaTemplate kafkaTemplate;
    @Value("${kafka.validation.producer.topic}")
    private String validatedTopic = "";
    private KafkaProducerService kafkaProducerService;
    private IHL7v2Validator hl7v2Validator;
    private ICsvValidator csvValidator;

    private IRawELRRepository rawELRRepository;
    private IValidatedELRRepository IValidatedELRRepository;

    public KafkaConsumerService(
            IValidatedELRRepository IValidatedELRRepository,
            IRawELRRepository rawELRRepository,
            KafkaTemplate kafkaTemplate,
            KafkaProducerService kafkaProducerService,
            IHL7v2Validator ihl7v2Validator,
            ICsvValidator iCsvValidator) {
        this.IValidatedELRRepository = IValidatedELRRepository;
        this.rawELRRepository = rawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.hl7v2Validator = ihl7v2Validator;
        this.csvValidator = iCsvValidator;
        this.kafkaTemplate = kafkaTemplate;
    }

    @RetryableTopic(
            attempts = "${kafka.validation.consumer.max-retry}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(id = "${kafka.group-id}", topics = "#{'${kafka.validation.consumer.topics}'.split(',')}")
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);
        String messageType = null;

        try {
            Optional<RawERLModel> rawElrResponse = this.rawELRRepository.findById(message);
            RawERLModel elrModel =  rawElrResponse.get();
            messageType = elrModel.getType();
            switch (messageType) {
                case KafkaHeaderValue.MessageType_HL7v2:
                    ValidatedELRModel hl7ValidatedModel = hl7v2Validator.MessageValidation(message, elrModel, validatedTopic);

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

    private void saveValidatedELRMessage(ValidatedELRModel model) {
        IValidatedELRRepository.save(model);
    }


}