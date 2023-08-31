package gov.cdc.dataingestion.kafka.integration.service;

import ca.uhn.hl7v2.HL7Exception;
import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.ConversionPrepareException;
import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.exception.FhirConversionException;
import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.exception.XmlConversionException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;

import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class KafkaConsumerService {

    //region VARIABLE
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Value("${kafka.retry.suffix}")
    private String retrySuffix = "";

    @Value("${kafka.dlt.suffix}")
    private String dltSuffix = "_dlt";

    @Value("${kafka.validation.topic}")
    private String validatedTopic = "elr_validated";

    @Value("${kafka.fhir-conversion.topic}")
    private String convertedToFhirTopic = "fhir_converted";

    @Value("${kafka.xml-conversion.topic}")
    private String convertedToXmlTopic = "xml_converted";

    @Value("${kafka.raw.topic}")
    private String rawTopic = "elr_raw";

    @Value("${kafka.elr-duplicate.topic}")
    private String validatedElrDuplicateTopic = "";

    @Value("${kafka.xml-conversion-prep.topic}")
    private String prepXmlTopic = "xml_prep";

    @Value("${kafka.fhir-conversion-prep.topic}")
    private String prepFhirTopic = "fhir_prep";
    private final KafkaProducerService kafkaProducerService;
    private final IHL7v2Validator iHl7v2Validator;
    private final IRawELRRepository iRawELRRepository;
    private final IValidatedELRRepository iValidatedELRRepository;
    private final IHL7ToFHIRConversion iHl7ToFHIRConversion;
    private final IHL7ToFHIRRepository iHL7ToFHIRRepository;
    private final IHL7DuplicateValidator iHL7DuplicateValidator;
    private final NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    private final IElrDeadLetterRepository elrDeadLetterRepository;

    private String errorDltMessage = "Message not found in dead letter table";
    //endregion

    //region CONSTRUCTOR
    public KafkaConsumerService(
            IValidatedELRRepository iValidatedELRRepository,
            IRawELRRepository iRawELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7v2Validator iHl7v2Validator,
            IHL7ToFHIRConversion ihl7ToFHIRConversion,
            IHL7ToFHIRRepository iHL7ToFHIRepository,
            IHL7DuplicateValidator iHL7DuplicateValidator,
            NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
            IElrDeadLetterRepository elrDeadLetterRepository
            ) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.iHl7v2Validator = iHl7v2Validator;
        this.iHl7ToFHIRConversion = ihl7ToFHIRConversion;
        this.iHL7ToFHIRRepository = iHL7ToFHIRepository;
        this.iHL7DuplicateValidator = iHL7DuplicateValidator;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.elrDeadLetterRepository = elrDeadLetterRepository;
    }
    //endregion

    //region KAFKA LISTENER
    /**
     * Read Me:
     * - Standard flow
     *      -> RawELR
     *      -> ValidatedELR
     *      -> Preparation For Conversion
     *          (No significant logic, simply send message to appropriate topics for consuming)
     *      -> These 2 paths will be consumed in no particular order
     *          -> XML Conversion (consuming from Prepare XML topic)
     *          -> Fhir Conversion (consuming from Prepare Fhir topic)
     * */

    /**
     * Raw Data Validation Process
     * */
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
            topics = "${kafka.raw.topic}"
    )
    public void handleMessageForRawElr(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaderValue.MessageValidationActive) String messageValidationActive) throws DuplicateHL7FileFoundException, DiHL7Exception {
        log.debug("Received message ID: {} from topic: {}", message, topic);
        boolean hl7ValidationActivated = false;

        if (messageValidationActive != null && messageValidationActive.equalsIgnoreCase("true")) {
            hl7ValidationActivated = true;
        }
        validationHandler(message, hl7ValidationActivated);
    }

    /**
     * After Validation Process
     * Description: After validated by Raw Consumer, data will be sent to validated producer
     * the data will be picked up by this consumer
     * this logic will direct validated data into 2 path (producers + topic)
     * 1 - to XML conversion consumer
     * 2 - to FHIR conversion consumer
     * */
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
    @KafkaListener(topics = "${kafka.validation.topic}")
    public void handleMessageForValidatedElr(String message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws ConversionPrepareException {
        log.debug("Received message ID: {} from topic: {}", message, topic);
        preparationForConversionHandler(message);
    }

    /**
     * XML Conversion
     * */
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
    @KafkaListener(topics = "${kafka.xml-conversion-prep.topic}")
    public void handleMessageForXmlConversionElr(String message,
                                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                 @Header(KafkaHeaderValue.MessageOperation) String operation) throws Exception {
        log.debug("Received message ID: {} from topic: {}", message, topic);
        xmlConversionHandler(message, operation);

    }

    /**
     * FHIR Conversion
     * */
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
    @KafkaListener(topics = "${kafka.fhir-conversion-prep.topic}")
    public void handleMessageForFhirConversionElr(String message,
                                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                  @Header(KafkaHeaderValue.MessageOperation) String operation) throws FhirConversionException, DiHL7Exception {
        log.debug("Received message ID: {} from topic: {}", message, topic);
        conversionHandlerForFhir(message, operation);

    }
    //endregion

    //region DLT HANDLER
    @DltHandler
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timeStamp,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaderValue.DltOccurrence) String dltOccurrence,
            @Header(KafkaHeaderValue.OriginalTopic) String originalTopic
    ) {
        log.debug("Message ID: {} handled by dlq topic: {}", message, topic);

        // increase by 1, indicate the dlt had been occurred
        Integer dltCount = Integer.parseInt(dltOccurrence) + 1;
        // consuming bad data and persist data onto database
        String erroredSource = getDltErrorSource(originalTopic);
        ElrDeadLetterDto elrDeadLetterDto = new ElrDeadLetterDto(
                message,
                erroredSource,
                stacktrace,
                dltCount,
                EnumElrDltStatus.ERROR.name(),
                erroredSource + this.dltSuffix,
                erroredSource + this.dltSuffix
        );
        processingDltRecord(elrDeadLetterDto);
    }
    //endregion

    //region PRIVATE METHOD
    private void processingDltRecord(ElrDeadLetterDto elrDeadLetterDto) {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        try {
            if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
                var message = this.iRawELRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new RuntimeException("Raw data not found; id: " + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getPayload());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(validatedTopic)) {
                var message = this.iValidatedELRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new RuntimeException("Raw data not found; id: " + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getRawMessage());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(prepXmlTopic)) {
                var message = this.iValidatedELRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new RuntimeException("Raw data not found; id: " + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getRawMessage());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic)) {
                var message = this.iValidatedELRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new RuntimeException("Raw data not found; id: " + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getRawMessage());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(convertedToXmlTopic)) {
                //todo: this to handle error that may occur after xml conversion
                throw new RuntimeException("Unsupported Topic; topic: " + elrDeadLetterDto.getErrorMessageSource());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(convertedToFhirTopic)) {
                var message = this.iHL7ToFHIRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new RuntimeException("Raw data not found; id: " + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getFhirMessage());
            } else {
                throw new RuntimeException("Unsupported Topic; topic: " + elrDeadLetterDto.getErrorMessageSource());
            }

            model.setErrorMessageId(elrDeadLetterDto.getErrorMessageId());
            model.setErrorMessageSource(elrDeadLetterDto.getErrorMessageSource());
            model.setErrorStackTrace(elrDeadLetterDto.getErrorStackTrace());
            model.setErrorStackTraceShort(elrDeadLetterDto.getErrorStackTraceShort());
            model.setDltOccurrence(elrDeadLetterDto.getDltOccurrence());
            model.setDltStatus(elrDeadLetterDto.getDltStatus());
            model.setMessage(elrDeadLetterDto.getMessage());
            model.setCreatedBy(elrDeadLetterDto.getCreatedBy());
            model.setUpdatedBy(elrDeadLetterDto.getUpdatedBy());
            this.elrDeadLetterRepository.save(model);
            // TODO: push notification to notify user, error happened, and it was saved of  into rds db
        } catch (Exception e) {
            Gson gson = new Gson();
            String data = gson.toJson(model);

            // TODO: If this happened, then push notification to notify user
            logger.error("Error occurred while processing DLT record: {}", data);

        }
    }
    private String getDltErrorSource(String incomingTopic) {
        String erroredSource = "";
        if (incomingTopic.equalsIgnoreCase(rawTopic)) {
            erroredSource = rawTopic;
        } else if (incomingTopic.equalsIgnoreCase(validatedTopic)) {
            erroredSource = validatedTopic;
        } else if (incomingTopic.equalsIgnoreCase(convertedToFhirTopic)) {
            erroredSource = convertedToFhirTopic;
        } else if (incomingTopic.equalsIgnoreCase(convertedToXmlTopic)) {
            erroredSource = convertedToXmlTopic;
        } else if (incomingTopic.equalsIgnoreCase(prepFhirTopic)) {
            erroredSource = prepFhirTopic;
        } else if (incomingTopic.equalsIgnoreCase(prepXmlTopic)) {
            erroredSource = prepXmlTopic;
        }
        return erroredSource;
    }

    private void preparationForConversionHandler(String message) throws ConversionPrepareException {
        Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
        if(validatedElrResponse.isPresent()) {
            kafkaProducerService.sendMessagePreparationTopic(validatedElrResponse.get(), prepXmlTopic, TopicPreparationType.XML, 0);
            kafkaProducerService.sendMessagePreparationTopic(validatedElrResponse.get(), prepFhirTopic, TopicPreparationType.FHIR, 0);
        } else {
            throw new ConversionPrepareException("Validation ELR Record Not Found");
        }
    }
    private void xmlConversionHandler(String message, String operation) throws XmlConversionException, DiHL7Exception, JAXBException, IOException {
        log.debug("Received message id will be retrieved from db and associated hl7 will be converted to xml");

        String hl7Msg = "";
        if (operation.equalsIgnoreCase(EnumKafkaOperation.INJECTION.name())) {
            Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
            hl7Msg = validatedElrResponse.get().getRawMessage();
        } else {
            Optional<ElrDeadLetterModel> response = this.elrDeadLetterRepository.findById(message);
            if (response.isPresent()) {
                var validMessage = iHl7v2Validator.MessageStringValidation(response.get().getMessage());
                hl7Msg = validMessage;
            } else {
                throw new XmlConversionException(errorDltMessage);
            }
        }

        String rhapsodyXml = Hl7ToRhapsodysXmlConverter.getInstance().convert(message, hl7Msg);

        // Modified from debug ==> info to capture xml for analysis.
        // Please leave below at "info" level for the time being, before going live,
        // this will be changed to debug
        log.info("rhapsodyXml: {}", rhapsodyXml);
      
        nbsRepositoryServiceProvider.saveXmlMessage(message, rhapsodyXml);
        kafkaProducerService.sendMessageAfterConvertedToXml(rhapsodyXml, convertedToXmlTopic, 0);
    }
    private void validationHandler(String message, boolean hl7ValidationActivated) throws DuplicateHL7FileFoundException, DiHL7Exception {
        Optional<RawERLModel> rawElrResponse = this.iRawELRRepository.findById(message);
        RawERLModel elrModel = rawElrResponse.get();
        String messageType = elrModel.getType();
        switch (messageType) {
            case KafkaHeaderValue.MessageType_HL7v2:
                ValidatedELRModel hl7ValidatedModel = iHl7v2Validator.MessageValidation(message, elrModel, validatedTopic, hl7ValidationActivated);
                // Duplication check
                iHL7DuplicateValidator.ValidateHL7Document(hl7ValidatedModel);
                saveValidatedELRMessage(hl7ValidatedModel);
                kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic, 0);
                break;
            case KafkaHeaderValue.MessageType_CSV:
                // TODO: implement csv validation, this is not in the scope of data ingestion at the moment
                break;
            default:
                break;
        }
    }
    private void conversionHandlerForFhir(String message, String operation) throws FhirConversionException, DiHL7Exception {
        String payloadMessage ="";
        ValidatedELRModel model = new ValidatedELRModel();
        if(operation.equalsIgnoreCase(EnumKafkaOperation.INJECTION.name())) {
            Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
            if (validatedElrResponse.isPresent()) {
                payloadMessage = validatedElrResponse.get().getRawMessage();
                model.setRawId(validatedElrResponse.get().getRawId());
                model.setRawMessage(payloadMessage);
            } else {
                throw new FhirConversionException(errorDltMessage);
            }

        }
        else {
            Optional<ElrDeadLetterModel> response = this.elrDeadLetterRepository.findById(message);
            if (response.isPresent()) {
                payloadMessage =  response.get().getMessage();
                var validMessage = iHl7v2Validator.MessageStringValidation(payloadMessage);
                model.setRawId(message);
                model.setRawMessage(validMessage);
            } else {
                throw new FhirConversionException(errorDltMessage);
            }
        }

        try {
            HL7ToFHIRModel convertedModel = iHl7ToFHIRConversion.ConvertHL7v2ToFhir(model, convertedToFhirTopic);
            iHL7ToFHIRRepository.save(convertedModel);
            kafkaProducerService.sendMessageAfterConvertedToFhirMessage(convertedModel, convertedToFhirTopic, 0);
        } catch (Exception e) {
            throw new FhirConversionException(e.getMessage());
        }
    }
    private void saveValidatedELRMessage(ValidatedELRModel model) {
        iValidatedELRRepository.save(model);
    }
    //endregion
}