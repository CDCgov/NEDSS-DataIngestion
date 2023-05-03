package gov.cdc.dataingestion.kafka.integration.service;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.model.ElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.exception.FhirConversionException;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.validation.model.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;

import gov.cdc.dataingestion.nbs.converters.Hl7ToXmlConverter;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class KafkaConsumerService {
    //private static String HEADER = "MSH|^~\\&|||||20080925161613||ADT^A05||P|2.6|";

    @Value("${kafka.validation.topic}")
    private String validatedTopic = "";

    @Value("${kafka.fhir-conversion.topic}")
    private String convertedToFhirTopic = "";

    @Value("${kafka.xml-conversion.topic}")
    private String convertedToXmlTopic = "";

    @Value("${kafka.raw.topic}")
    private String rawTopic = "";

    @Value("${kafka.elr-duplicate.topic}")
    private String validatedElrDuplicateTopic = "";

    private KafkaProducerService kafkaProducerService;
    private IHL7v2Validator iHl7v2Validator;
    private IRawELRRepository iRawELRRepository;
    private IValidatedELRRepository iValidatedELRRepository;
    private IHL7ToFHIRConversion iHl7ToFHIRConversion;
    private IHL7ToFHIRRepository iHL7ToFHIRRepository;
    private IHL7DuplicateValidator iHL7DuplicateValidator;

    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    private ElrDeadLetterService elrDeadLetterService;


    public KafkaConsumerService(
            IValidatedELRRepository iValidatedELRRepository,
            IRawELRRepository iRawELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7v2Validator iHl7v2Validator,
            IHL7ToFHIRConversion ihl7ToFHIRConversion,
            IHL7ToFHIRRepository iHL7ToFHIRepository,
            IHL7DuplicateValidator iHL7DuplicateValidator,
            NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
            ElrDeadLetterService elrDeadLetterService) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.iHl7v2Validator = iHl7v2Validator;
        this.iHl7ToFHIRConversion = ihl7ToFHIRConversion;
        this.iHL7ToFHIRRepository = iHL7ToFHIRepository;
        this.iHL7DuplicateValidator = iHL7DuplicateValidator;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.elrDeadLetterService = elrDeadLetterService;
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            autoCreateTopics = "false",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    DuplicateHL7FileFoundException.class,
                    //HL7Exception.class
            }
    )
    @KafkaListener(topics = "${kafka.raw.topic}")
    public void handleMessageForRawElr(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)  {
        log.info("Received message ID: {} from topic: {}", message, topic);

        try {
            validationHandler(message);
        } catch (Exception e) {
            log.info("Retry queue");
            // run time error then -- do retry
            // get root message
            throw new RuntimeException(ExceptionUtils.getRootCause(e).getMessage());
        }
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            autoCreateTopics = "false",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    DuplicateHL7FileFoundException.class,
                    //HL7Exception.class
            }
    )
    @KafkaListener(topics = "${kafka.validation.topic}")
    public void handleMessageForValidatedElr(String message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message ID: {} from topic: {}", message, topic);

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                conversionHandler(message);
            } catch (Exception e) {
                log.info("Retry queue");
                throw new RuntimeException(ExceptionUtils.getRootCause(e).getMessage());
            }
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                xmlConversionHandler(message);
            } catch (Exception e) {
                log.info("Retry queue");
                throw new RuntimeException(ExceptionUtils.getRootCause(e).getMessage());
            }
        });
        CompletableFuture.allOf(future1, future2).join();

    }

    @DltHandler
    public void handleDlt(
            ConsumerRecord<String, String> record,
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaderValue.DltOccurrence) String dltOccurrence
    ) {
        // Once in DLQ -- we can save message in actual db for further analyze
        log.info("Message ID: {} handled by dlq topic: {}", message, topic);
        log.info("Stack Trace: {}", stacktrace);

        var test = record;
        String dtlSuffix = "-dlt";

        // use this for data re-injection
        String erroredSource = "";
        String errorStackTrace = stacktrace;
        // increase by 1, indicate the dlt had been occurred

        Integer dltCount = Integer.parseInt(dltOccurrence) + 1;
        // consuming bad data and persist data onto database
        if (topic.equalsIgnoreCase(rawTopic + dtlSuffix)) {
            erroredSource = rawTopic;
        } else if (topic.equalsIgnoreCase(validatedTopic + dtlSuffix)) {
            erroredSource = validatedTopic;
        } else if (topic.equalsIgnoreCase(convertedToFhirTopic + dtlSuffix)) {
            erroredSource = convertedToFhirTopic;
        } else if (topic.equalsIgnoreCase(convertedToXmlTopic + dtlSuffix)) {
            erroredSource = convertedToXmlTopic;
        }

        ElrDeadLetterDto elrDeadLetterDto = new ElrDeadLetterDto(
                message,
                erroredSource,
                errorStackTrace,
                dltCount,
                ElrDltStatus.ERROR.name(),
                erroredSource + dtlSuffix,
                erroredSource + dtlSuffix
        );

        // better off write as log file
        this.elrDeadLetterService.saveDltRecord(elrDeadLetterDto);
    }

    private void xmlConversionHandler(String message) throws Exception {
        log.info("Received message id will be retrieved from db and associated hl7 will be converted to xml");

        Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
        String hl7AsXml = Hl7ToXmlConverter.getInstance().convertXl7ToXml(validatedElrResponse.get().getRawMessage());

        log.info("Converted xml: {}", hl7AsXml);

        nbsRepositoryServiceProvider.saveXmlMessage(hl7AsXml);
        kafkaProducerService.sendMessageAfterConvertedToXml(hl7AsXml, convertedToXmlTopic, 0);
    }

    private void validationHandler(String message) throws HL7Exception, DuplicateHL7FileFoundException {
        Optional<RawERLModel> rawElrResponse = this.iRawELRRepository.findById(message);
        RawERLModel elrModel = rawElrResponse.get();
        String messageType = elrModel.getType();
        switch (messageType) {
            case KafkaHeaderValue.MessageType_HL7v2:
                ValidatedELRModel hl7ValidatedModel = iHl7v2Validator.MessageValidation(message, elrModel, validatedTopic);
                iHL7DuplicateValidator.ValidateHL7Document(hl7ValidatedModel);
                saveValidatedELRMessage(hl7ValidatedModel);
                kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic, 0);
                break;
            case KafkaHeaderValue.MessageType_CSV:
                // ValidatedELRModel csvValidatedModel = csvValidator.ValidateCSVAgainstCVSSchema(message);
                // kafkaProducerService.sendMessageAfterValidatingMessage(csvValidatedModel, validatedTopic);
                break;
            default:
                break;
        }
    }
    private void conversionHandler(String message) throws FhirConversionException {
        Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
        ValidatedELRModel validatedELRModel = validatedElrResponse.get();
        String messageType = validatedELRModel.getMessageType();
        if (messageType.equalsIgnoreCase(KafkaHeaderValue.MessageType_HL7v2)) {
            try {
                HL7ToFHIRModel convertedModel = iHl7ToFHIRConversion.ConvertHL7v2ToFhir(validatedELRModel, convertedToFhirTopic);
                iHL7ToFHIRRepository.save(convertedModel);
                kafkaProducerService.sendMessageAfterConvertedToFhirMessage(convertedModel, convertedToFhirTopic, 0);
            } catch (Exception e) {
                throw new FhirConversionException(e.getMessage());
            }
        } else {
            throw new FhirConversionException("Invalid Message");
        }
    }

    private void saveValidatedELRMessage(ValidatedELRModel model) {
        iValidatedELRRepository.save(model);
    }
}