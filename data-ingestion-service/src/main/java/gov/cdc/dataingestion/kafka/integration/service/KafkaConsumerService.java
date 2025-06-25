package gov.cdc.dataingestion.kafka.integration.service;


import ca.uhn.hl7v2.HL7Exception;
import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.custommetrics.TimeMetricsBuilder;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.*;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import gov.cdc.dataingestion.share.helper.OBRSplitter;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gov.cdc.dataingestion.constant.MessageType.XML_ELR;
import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@Slf4j
public class KafkaConsumerService {

    //region VARIABLE
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Value("${service.timezone}")
    private String tz = "UTC";

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

    @Value("${kafka.rti.topic}")
    private String rtiTopic = "dp_elr_unprocessed";

    @Value("${kafka.raw.topic}")
    private String rawTopic = "elr_raw";

    @Value("${kafka.xml-conversion-prep.topic}")
    private String prepXmlTopic = "xml_prep";

    @Value("${kafka.fhir-conversion-prep.topic}")
    private String prepFhirTopic = "fhir_prep";

    @Value("${features.obrSpliting.enabled}")
    private boolean obrSplitingEnabled;

    private final KafkaProducerService kafkaProducerService;
    private final IHL7v2Validator iHl7v2Validator;
    private final IRawElrRepository iRawELRRepository;
    private final IValidatedELRRepository iValidatedELRRepository;
    private final IHL7DuplicateValidator iHL7DuplicateValidator;
    private final NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    private final IElrDeadLetterRepository elrDeadLetterRepository;

    private final IReportStatusRepository iReportStatusRepository;
    private final CustomMetricsBuilder customMetricsBuilder;
    private final TimeMetricsBuilder timeMetricsBuilder;
    private final OBRSplitter elrSplitter;
    private String errorDltMessage = "Message not found in dead letter table";
    private String topicDebugLog = "Received message ID: {} from topic: {}";
    private String processDltErrorMessage = "Raw data not found; id: ";
    //endregion


    //region CONSTRUCTOR
    public KafkaConsumerService(
            IValidatedELRRepository iValidatedELRRepository,
            IRawElrRepository iRawELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7v2Validator iHl7v2Validator,
            IHL7DuplicateValidator iHL7DuplicateValidator,
            NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
            IElrDeadLetterRepository elrDeadLetterRepository,
            IReportStatusRepository iReportStatusRepository,
            CustomMetricsBuilder customMetricsBuilder,
            TimeMetricsBuilder timeMetricsBuilder,
            OBRSplitter elrSplitter) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.iHl7v2Validator = iHl7v2Validator;
        this.iHL7DuplicateValidator = iHL7DuplicateValidator;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.elrDeadLetterRepository = elrDeadLetterRepository;
        this.iReportStatusRepository = iReportStatusRepository;
        this.customMetricsBuilder = customMetricsBuilder;
        this.timeMetricsBuilder = timeMetricsBuilder;
        this.elrSplitter = elrSplitter;
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
            topics = "${kafka.raw.topic}",
            containerFactory = "kafkaListenerContainerFactoryRaw"
    )
    public void handleMessageForRawElr(String message,
                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                 @Header(KafkaHeaderValue.MESSAGE_VALIDATION_ACTIVE) String messageValidationActive,
                 @Header(KafkaHeaderValue.DATA_PROCESSING_ENABLE) String dataProcessingEnable) {
        timeMetricsBuilder.recordElrRawEventTime(() -> {
            log.debug(topicDebugLog, message, topic);
            boolean hl7ValidationActivated = false;

            if (messageValidationActive != null && messageValidationActive.equalsIgnoreCase("true")) {
                hl7ValidationActivated = true;
            }
            try {
                validationHandler(message, hl7ValidationActivated, dataProcessingEnable);
            } catch (DuplicateHL7FileFoundException | DiHL7Exception | KafkaProducerException e) {
                throw new RuntimeException(e); //NOSONAR
            }
        });
    }


    /**
     * Receive XML converted HL7 and save it to NBS Interface
     * Description: The ELR ingestion endpoint that we have right now is modified to accept both
     * plain text HL7 as well as HL7s converted to XML already.
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
            topics = "${kafka.raw.xml-topic}",
            containerFactory = "kafkaListenerContainerFactoryRawXml"
    )
    public void handleMessageForElrXml(String message,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String messageId,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(KafkaHeaderValue.DATA_PROCESSING_ENABLE) String dataProcessingEnable)
    {

        timeMetricsBuilder.recordElrRawXmlEventTime(() -> {
            log.debug(topicDebugLog, messageId, topic);

            boolean dataProcessingApplied = Boolean.parseBoolean(dataProcessingEnable);
            NbsInterfaceModel nbsInterfaceModel = null;
            try {
                nbsInterfaceModel = nbsRepositoryServiceProvider.saveElrXmlMessage(messageId, message, dataProcessingApplied);
            } catch (XmlConversionException e) {
                throw new RuntimeException(e); //NOSONAR
            }
            log.debug("Saved Elr xml to NBS_interface table with uid: {}", nbsInterfaceModel.getNbsInterfaceUid());

            ReportStatusIdData reportStatusIdData = new ReportStatusIdData();
            reportStatusIdData.setRawMessageId(messageId.replaceAll(XML_ELR + "_", ""));
            reportStatusIdData.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());
            reportStatusIdData.setCreatedBy("elr_raw_xml");
            reportStatusIdData.setUpdatedBy("elr_raw_xml");
            var time = getCurrentTimeStamp(tz);
            reportStatusIdData.setCreatedOn(time);
            reportStatusIdData.setUpdatedOn(time);

            iReportStatusRepository.save(reportStatusIdData);

            if (dataProcessingApplied) {
                try {
                    kafkaProducerService.sendMessageAfterConvertedToXml(
                            String.valueOf(nbsInterfaceModel.getNbsInterfaceUid()), rtiTopic, 0);
                } catch (KafkaProducerException e) {
                    throw new RuntimeException(e); //NOSONAR
                }
            }
            else {
                try {
                    kafkaProducerService.sendMessageAfterConvertedToXml(
                            nbsInterfaceModel.getNbsInterfaceUid().toString(), convertedToXmlTopic, 0);
                } catch (KafkaProducerException e) {
                    throw new RuntimeException(e); //NOSONAR
                }
            }
        });

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
    @KafkaListener(topics = "${kafka.validation.topic}",
            containerFactory = "kafkaListenerContainerFactoryValidate"
    )
    public void handleMessageForValidatedElr(String message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                             @Header(KafkaHeaderValue.DATA_PROCESSING_ENABLE) String dataProcessingEnable) {

        timeMetricsBuilder.recordElrValidatedTime(() -> {
            log.debug(topicDebugLog, message, topic);
            try {
                preparationForConversionHandler(message, dataProcessingEnable);
            } catch (Exception e) {
                throw new RuntimeException(e); //NOSONAR
            }
        });
    }

    /**
     * XML Conversion
     * */
    @KafkaListener(topics = "${kafka.xml-conversion-prep.topic}",
            containerFactory = "kafkaListenerContainerFactoryXml")
    public void handleMessageForXmlConversionElr(String message,
                                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                 @Header(KafkaHeaderValue.MESSAGE_OPERATION) String operation,
                                                 @Header(KafkaHeaderValue.DATA_PROCESSING_ENABLE) String dataProcessingEnable)  {
        timeMetricsBuilder.recordXmlPrepTime(() -> {
            log.debug(topicDebugLog, message, topic);
            try {
                xmlConversionHandler(message, operation, dataProcessingEnable);
            } catch (KafkaProducerException e) {
                throw new RuntimeException(e); //NOSONAR
            }
        });
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
            topics = "xml_prep_dlt_manual",
            containerFactory = "kafkaListenerContainerFactoryDltManual"
    )
    public void handleDltManual(
            String message,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaderValue.DLT_OCCURRENCE) String dltOccurrence,
            @Header(KafkaHeaderValue.ORIGINAL_TOPIC) String originalTopic,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String msg
    ) {
        shareProcessingDlt(dltOccurrence, originalTopic, message, stacktrace, msg);
    }

    //region DLT HANDLER
    @DltHandler()
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timeStamp,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaderValue.DLT_OCCURRENCE) String dltOccurrence,
            @Header(KafkaHeaderValue.ORIGINAL_TOPIC) String originalTopic
    ) {
        shareProcessingDlt(dltOccurrence, originalTopic, message, stacktrace, null);
    }
    //endregion

    private void shareProcessingDlt(String dltOccurrence, String originalTopic, String message, String stacktrace, String shortMsg) {

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

        if (shortMsg != null && !shortMsg.isEmpty()) {
            elrDeadLetterDto.setErrorStackTraceShort(shortMsg);
        }
        processingDltRecord(elrDeadLetterDto);
    }

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
            else {
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
            model.setCreatedOn(getCurrentTimeStamp(tz));
            model.setUpdatedOn(getCurrentTimeStamp(tz));
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

    private void preparationForConversionHandler(String message, String dataProcessingEnable) throws ConversionPrepareException, KafkaProducerException {
        Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(message);
        if(validatedElrResponse.isPresent()) {
            kafkaProducerService.sendMessagePreparationTopic(validatedElrResponse.get(), prepXmlTopic, TopicPreparationType.XML, 0, dataProcessingEnable);
            //kafkaProducerService.sendMessagePreparationTopic(validatedElrResponse.get(), prepFhirTopic, TopicPreparationType.FHIR, 0, dataProcessingEnable);//NOSONAR
        } else {
            throw new ConversionPrepareException("Validation ELR Record Not Found");
        }
    }

    /**
     * make this public so we can add unit test for now.
     * we need to implementation interface pattern for NBS convert and transformation classes. it better for unit testing
     * */
    @SuppressWarnings("java:S3776")
    public void xmlConversionHandlerProcessing(String messageId, String operation, String dataProcessingEnable) throws KafkaProducerException {
        String hl7Msg = "";
        try {
            Optional<ValidatedELRModel> validatedELRModel = iValidatedELRRepository.findById(messageId);
            if (validatedELRModel.isEmpty()) {
                throw new XmlConversionException("Message Not Found in Validated");
            }
            List<ReportStatusIdData> statusList = iReportStatusRepository.findByRawMessageId(validatedELRModel.get().getRawId());
            if (statusList!=null && !statusList.isEmpty()) {
                for(ReportStatusIdData reportStatusIdData:statusList){
                    if(reportStatusIdData.getNbsInterfaceUid() != null){
                        logger.info("Kafka Rebalancing Error Hit");
                        return;
                    }
                }
            }
            if (operation.equalsIgnoreCase(EnumKafkaOperation.INJECTION.name())) {
                Optional<ValidatedELRModel> validatedElrResponse = this.iValidatedELRRepository.findById(messageId);
                hl7Msg = validatedElrResponse.map(ValidatedELRModel::getRawMessage).orElse("");
            } else {
                Optional<ElrDeadLetterModel> response = this.elrDeadLetterRepository.findById(messageId);
                if (response.isPresent()) {
                    var validMessage = iHl7v2Validator.messageStringFormat(response.get().getMessage());
                    validMessage = iHl7v2Validator.processFhsMessage(validMessage);
                    validMessage = iHl7v2Validator.hl7MessageValidation(validMessage);
                    hl7Msg = validMessage;
                } else {
                    throw new XmlConversionException(errorDltMessage);
                }
            }
            boolean dataProcessingApplied = Boolean.parseBoolean(dataProcessingEnable);

            HL7ParsedMessage<OruR1> parsedMessageOrig = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(hl7Msg);
            List<HL7ParsedMessage<OruR1>> parsedMessageList;
            log.info("OBR splitting feature flag enabled: {}", obrSplitingEnabled);
            if(obrSplitingEnabled){
                parsedMessageList= splitElrByOBR(parsedMessageOrig);
            }else{
                parsedMessageList= new ArrayList<>();
                parsedMessageList.add(parsedMessageOrig);
            }

            for(HL7ParsedMessage<OruR1> hl7ParsedMessage:parsedMessageList) {
                String phdcXml = Hl7ToRhapsodysXmlConverter.getInstance().convert(messageId, hl7ParsedMessage);
                log.debug("phdcXml: {}", phdcXml);
                NbsInterfaceModel nbsInterfaceModel = nbsRepositoryServiceProvider.saveXmlMessage(messageId, phdcXml, hl7ParsedMessage, dataProcessingApplied);

                customMetricsBuilder.incrementXmlConversionRequested();
                // Once the XML is saved to the NBS_Interface table, we get the ID to save it
                // in the Data Ingestion elr_record_status_id table, so that we can get the status
                // of the record straight-forward from the NBS_Interface table.

                if(nbsInterfaceModel == null) {
                    customMetricsBuilder.incrementXmlConversionRequestedFailure();
                }
                else {
                    customMetricsBuilder.incrementXmlConversionRequestedSuccess();
                    ReportStatusIdData reportStatusIdData = new ReportStatusIdData();
                    reportStatusIdData.setRawMessageId(validatedELRModel.get().getRawId());
                    reportStatusIdData.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());
                    reportStatusIdData.setCreatedBy(convertedToXmlTopic);
                    reportStatusIdData.setUpdatedBy(convertedToXmlTopic);

                    var timestamp = getCurrentTimeStamp(tz);
                    reportStatusIdData.setCreatedOn(timestamp);
                    reportStatusIdData.setUpdatedOn(timestamp);
                    iReportStatusRepository.save(reportStatusIdData);
                }
                if (dataProcessingApplied) {
                    kafkaProducerService.sendMessageAfterConvertedToXml(nbsInterfaceModel.getNbsInterfaceUid().toString(), rtiTopic, 0); //NOSONAR
                } else {
                    kafkaProducerService.sendMessageAfterConvertedToXml(nbsInterfaceModel.getNbsInterfaceUid().toString(), convertedToXmlTopic, 0);
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            var msg =  e.getMessage();
            // Handle any exceptions here
            kafkaProducerService.sendMessageDlt(
                    msg, messageId, "xml_prep_dlt_manual", 0 ,
                    stackTrace,prepXmlTopic
            );
        }
    }
    private List<HL7ParsedMessage<OruR1>> splitElrByOBR(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        return elrSplitter.splitElr(parsedMessageOrig);
    }

    private void xmlConversionHandler(String message, String operation, String dataProcessingEnable) throws KafkaProducerException {
        log.debug("Received message id will be retrieved from db and associated hl7 will be converted to xml");
        xmlConversionHandlerProcessing(message, operation, dataProcessingEnable);
    }
    private void validationHandler(String message, boolean hl7ValidationActivated, String dataProcessingEnable) throws DuplicateHL7FileFoundException, DiHL7Exception, KafkaProducerException {
        Optional<RawElrModel> rawElrResponse = this.iRawELRRepository.findById(message);
        RawElrModel elrModel;
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
                    String errorMsg=e.getMessage();
                    if(errorMsg!=null && errorMsg.contains("ValidationException") && errorMsg.contains("HL7 datetime string at MSH-6"))
                    {
                        errorMsg=errorMsg.replace("HL7 datetime string at MSH-6","HL7 datetime string at MSH-7");
                    }
                    throw new DiHL7Exception(errorMsg);
                }
                // Duplication check
                iHL7DuplicateValidator.validateHL7Document(hl7ValidatedModel);
                saveValidatedELRMessage(hl7ValidatedModel);
                kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic, 0, dataProcessingEnable);
                break;
            case KafkaHeaderValue.MESSAGE_TYPE_CSV:
                // TODO: implement csv validation, this is not in the scope of data ingestion at the moment //NOSONAR
                break;
            default:
                break;
        }
    }

    private void saveValidatedELRMessage(ValidatedELRModel model) {
        model.setCreatedOn(getCurrentTimeStamp(tz));
        model.setUpdatedOn(getCurrentTimeStamp(tz));
        iValidatedELRRepository.save(model);
    }
    //endregion
}