package gov.cdc.dataingestion.kafka.integration.service;


import ca.uhn.hl7v2.HL7Exception;
import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.*;
import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
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
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

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

    private final ICdaMapper cdaMapper;
    private final IEcrMsgQueryService ecrMsgQueryService;
    private final IReportStatusRepository iReportStatusRepository;
    private final CustomMetricsBuilder customMetricsBuilder;

    private String errorDltMessage = "Message not found in dead letter table";
    private String topicDebugLog = "Received message ID: {} from topic: {}";
    private String processDltErrorMessage = "Raw data not found; id: ";
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
            IElrDeadLetterRepository elrDeadLetterRepository,
            ICdaMapper cdaMapper,
            IEcrMsgQueryService ecrMsgQueryService,
            IReportStatusRepository iReportStatusRepository,
            CustomMetricsBuilder customMetricsBuilder) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.iHl7v2Validator = iHl7v2Validator;
        this.iHl7ToFHIRConversion = ihl7ToFHIRConversion;
        this.iHL7ToFHIRRepository = iHL7ToFHIRepository;
        this.iHL7DuplicateValidator = iHL7DuplicateValidator;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.elrDeadLetterRepository = elrDeadLetterRepository;
        this.cdaMapper = cdaMapper;
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.iReportStatusRepository = iReportStatusRepository;
        this.customMetricsBuilder = customMetricsBuilder;
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
                               @Header(KafkaHeaderValue.MESSAGE_VALIDATION_ACTIVE) String messageValidationActive) throws DuplicateHL7FileFoundException, DiHL7Exception {
        log.debug(topicDebugLog, message, topic);
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
        log.debug(topicDebugLog, message, topic);
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
                                                 @Header(KafkaHeaderValue.MESSAGE_OPERATION) String operation) throws Exception {
        log.debug(topicDebugLog, message, topic);
        customMetricsBuilder.incrementXmlConversionRequested();
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
                                                  @Header(KafkaHeaderValue.MESSAGE_OPERATION) String operation) throws FhirConversionException, DiHL7Exception {
        log.debug(topicDebugLog, message, topic);
        conversionHandlerForFhir(message, operation);

    }
    //endregion

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
            topics = "ecr_cda"
    )
    public void handleMessageForPhdcEcrTransformToCda(String message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws EcrCdaXmlException {
        log.debug(topicDebugLog, message, topic);
        var result = ecrMsgQueryService.getSelectedEcrRecord();
        var xmlResult = this.cdaMapper.tranformSelectedEcrToCDAXml(result);
        nbsRepositoryServiceProvider.saveEcrCdaXmlMessage(result.getMsgContainer().getNbsInterfaceUid().toString()
                , result.getMsgContainer().getDataMigrationStatus(), xmlResult);
    }

    //region DLT HANDLER
    @DltHandler
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timeStamp,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaderValue.DLT_OCCURRENCE) String dltOccurrence,
            @Header(KafkaHeaderValue.ORIGINAL_TOPIC) String originalTopic
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
                        .orElseThrow(() -> new DeadLetterTopicException(processDltErrorMessage + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getPayload());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(validatedTopic) ||
                    elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(prepXmlTopic) ||
                    elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic)) {
                var message = this.iValidatedELRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new DeadLetterTopicException(processDltErrorMessage + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getRawMessage());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(convertedToXmlTopic)) {
                throw new DeadLetterTopicException("Unsupported Topic; topic: " + elrDeadLetterDto.getErrorMessageSource());
            }
            else if (elrDeadLetterDto.getErrorMessageSource().equalsIgnoreCase(convertedToFhirTopic)) {
                var message = this.iHL7ToFHIRRepository.findById(elrDeadLetterDto.getErrorMessageId())
                        .orElseThrow(() -> new DeadLetterTopicException(processDltErrorMessage + elrDeadLetterDto.getErrorMessageId()));
                elrDeadLetterDto.setMessage(message.getFhirMessage());
            } else {
                throw new DeadLetterTopicException("Unsupported Topic; topic: " + elrDeadLetterDto.getErrorMessageSource());
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
        } catch (Exception e) {
            Gson gson = new Gson();
            String data = gson.toJson(model);
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
    private void xmlConversionHandler(String message, String operation) {

        // Update: change method to Async process, intensive process in this method cause consumer lagging and strange behavior
        // TODO: consider breaking down this logic
        // PROCESS as follow:
        //  - HL7 -> XML
                // xml conversion can be broke down into multiple smaller pipeline
        //  - Saving record to status table can also be broke to downstream pipeline
        CompletableFuture.runAsync(() -> {
            log.debug("Received message id will be retrieved from db and associated hl7 will be converted to xml");

            String hl7Msg = "";
            try {
                if (operation.equalsIgnoreCase(EnumKafkaOperation.INJECTION.name())) {
                    Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
                    hl7Msg = validatedElrResponse.map(ValidatedELRModel::getRawMessage).orElse("");
                } else {
                    Optional<ElrDeadLetterModel> response = this.elrDeadLetterRepository.findById(message);
                    if (response.isPresent()) {
                        var validMessage = iHl7v2Validator.messageStringValidation(response.get().getMessage());
                        validMessage = iHl7v2Validator.processFhsMessage(validMessage);
                        hl7Msg = validMessage;
                    } else {
                        throw new XmlConversionException(errorDltMessage);
                    }
                }
                HL7ParsedMessage<OruR1> parsedMessage = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(hl7Msg);
                String rhapsodyXml = Hl7ToRhapsodysXmlConverter.getInstance().convert(message, parsedMessage);

                // Modified from debug ==> info to capture xml for analysis.
                // Please leave below at "info" level for the time being, before going live,
                // this will be changed to debug
                log.info("rhapsodyXml: {}", rhapsodyXml);

                NbsInterfaceModel nbsInterfaceModel = nbsRepositoryServiceProvider.saveXmlMessage(message, rhapsodyXml, parsedMessage);


                // Once the XML is saved to the NBS_Interface table, we get the ID to save it
                // in the Data Ingestion elr_record_status_id table, so that we can get the status
                // of the record straight-forward from the NBS_Interface table.

                if(nbsInterfaceModel == null) {
                    customMetricsBuilder.incrementXmlConversionRequestedFailure();
                }
                else {
                    customMetricsBuilder.incrementXmlConversionRequestedSuccess();
                    ReportStatusIdData reportStatusIdData = new ReportStatusIdData();
                    Optional<ValidatedELRModel> validatedELRModel = iValidatedELRRepository.findById(message);
                    reportStatusIdData.setRawMessageId(validatedELRModel.get().getRawId());
                    reportStatusIdData.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());
                    reportStatusIdData.setCreatedBy("SPECIAL_TESTER_1_" + convertedToXmlTopic);
                    reportStatusIdData.setUpdatedBy(convertedToXmlTopic);
                    reportStatusIdData.setCreatedOn(getCurrentTimeStamp());
                    reportStatusIdData.setUpdatedOn(getCurrentTimeStamp());
                    iReportStatusRepository.save(reportStatusIdData);
                }

                kafkaProducerService.sendMessageAfterConvertedToXml(rhapsodyXml, convertedToXmlTopic, 0);

            } catch (Exception e) {
                // Handle any exceptions here
                throw new DiAsyncException(e.getMessage());
            }
        });




    }
    private void validationHandler(String message, boolean hl7ValidationActivated) throws DuplicateHL7FileFoundException, DiHL7Exception {
        Optional<RawERLModel> rawElrResponse = this.iRawELRRepository.findById(message);
        RawERLModel elrModel;
        if (!rawElrResponse.isEmpty()) {
            elrModel = rawElrResponse.get();
        } else {
            throw new  DiHL7Exception("Raw ELR record is empty for Id: " + message);
        }
        String messageType = elrModel.getType();
        switch (messageType) {
            case KafkaHeaderValue.MESSAGE_TYPE_HL7:
                customMetricsBuilder.incrementMessagesValidated();
                ValidatedELRModel hl7ValidatedModel;
                try {
                    hl7ValidatedModel = iHl7v2Validator.messageValidation(message, elrModel, validatedTopic, hl7ValidationActivated);
                    customMetricsBuilder.incrementMessagesValidatedSuccess();
                } catch (DiHL7Exception e) {
                    customMetricsBuilder.incrementMessagesValidatedFailure();
                    throw new DiHL7Exception(e.getMessage());
                }
                // Duplication check
                iHL7DuplicateValidator.validateHL7Document(hl7ValidatedModel);
                saveValidatedELRMessage(hl7ValidatedModel);
                kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic, 0);
                break;
            case KafkaHeaderValue.MESSAGE_TYPE_CSV:
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
                var validMessage = iHl7v2Validator.messageStringValidation(payloadMessage);
                model.setRawId(message);
                model.setRawMessage(validMessage);
            } else {
                throw new FhirConversionException(errorDltMessage);
            }
        }

        try {
            HL7ToFHIRModel convertedModel = iHl7ToFHIRConversion.convertHL7v2ToFhir(model, convertedToFhirTopic);
            iHL7ToFHIRRepository.save(convertedModel);
            kafkaProducerService.sendMessageAfterConvertedToFhirMessage(convertedModel, convertedToFhirTopic, 0);
        } catch (Exception e) {
            throw new FhirConversionException(e.getMessage());
        }
    }
    private void saveValidatedELRMessage(ValidatedELRModel model) {
        model.setCreatedOn(getCurrentTimeStamp());
        model.setUpdatedOn(getCurrentTimeStamp());
        iValidatedELRRepository.save(model);
    }
    //endregion
}