package gov.cdc.dataingestion.kafka.service;

import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.kafka.integration.service.KafkaConsumerService;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
public class KafkaConsumerServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private IHL7v2Validator iHl7v2Validator;
    @Mock
    private IRawELRRepository iRawELRRepository;
    @Mock
    private IValidatedELRRepository iValidatedELRRepository;
    @Mock
    private IHL7ToFHIRConversion iHl7ToFHIRConversion;
    @Mock
    private IHL7ToFHIRRepository iHL7ToFHIRRepository;
    @Mock
    private IHL7DuplicateValidator iHL7DuplicateValidator;
    @Mock
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    @Mock
    private IElrDeadLetterRepository elrDeadLetterRepository;


    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withStartupTimeout(Duration.ofMinutes(5));

    private static final DockerImageName taggedImageName = DockerImageName.parse("mcr.microsoft.com/azure-sql-edge")
            .withTag("latest")
            .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");

    @Container
    public MSSQLServerContainer mssqlserver  =
            new MSSQLServerContainer<>(taggedImageName)
                    .acceptLicense()
                    .withInitScript("sql-script/test-script.sql")
                    .withStartupTimeout(Duration.ofMinutes(5))
            ;

    private KafkaConsumerService kafkaConsumerService;

    private KafkaConsumer<String, String> consumer;
    private String guidForTesting = "";

    private String testHL7Message = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r" +
            "PID|||7005728^^^TML^MR||JOHN^DOE^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r" +
            "OBX|1|ST|||Test Demo CDC 2-8-16";

    private String errorMessage = "java.lang.RuntimeException: The HL7 version 2.5.1\\rPID is not recognized \tat gov.cdc.dataingestion.kafka.integration.service.KafkaConsumerService.handleMessageForXmlConversionElr(KafkaConsumerService.java:242) \tat java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104) \tat java.base/java.lang.reflect.Method.invoke(Method.java:577) \tat org.springframework.messaging.handler.invocation.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:169) \tat org.springframework.messaging.handler.invocation.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:119) \tat org.springframework.kafka.listener.adapter.HandlerAdapter.invoke(HandlerAdapter.java:56) \tat org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter.invokeHandler(MessagingMessageListenerAdapter.java:366) \t... 18 more ";
    private String rawTopic = "elr_raw";
    private String validateTopic = "elr_validated";
    private String xmlPrepTopic = "xml_prep";
    private String fhirPrepTopic = "fhir_prep";
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        String bootstrapServers = kafkaContainer.getBootstrapServers();

        // Create Kafka consumer properties
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "data-ingestion-group");
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "30000");
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        // Create Kafka consumer
        consumer = new KafkaConsumer<>(consumerProperties);

        // Subscribe to the test topic
        consumer.subscribe(Arrays.asList(rawTopic, validateTopic, xmlPrepTopic,  fhirPrepTopic));


        kafkaConsumerService = new KafkaConsumerService(
                iValidatedELRRepository,
                iRawELRRepository,
                kafkaProducerService,
                iHl7v2Validator,
                iHl7ToFHIRConversion,
                iHL7ToFHIRRepository,
                iHL7DuplicateValidator,
                nbsRepositoryServiceProvider,
                elrDeadLetterRepository
        );
    }
    @AfterEach
    public void tearDown() {
        consumer.close();
        mssqlserver.stop();
    }

    @Test
    public void rawConsumerTest() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(rawTopic);
        String message =  guidForTesting;
        produceMessage(rawTopic, message, EnumKafkaOperation.INJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();

        RawERLModel rawModel = new RawERLModel();
        rawModel.setId(guidForTesting);
        rawModel.setType("HL7");

        when(iRawELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(rawModel));

        kafkaConsumerService.handleMessageForRawElr(value, rawTopic);

        verify(iRawELRRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void validateConsumerTest() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(validateTopic);
        String message =  guidForTesting;
        produceMessage(validateTopic, message, EnumKafkaOperation.INJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();

        ValidatedELRModel model = new ValidatedELRModel();
        model.setId(guidForTesting);

        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(model));

        kafkaConsumerService.handleMessageForValidatedElr(value, validateTopic);

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void xmlPreparationConsumerTest() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(xmlPrepTopic);
        String message =  guidForTesting;
        produceMessage(xmlPrepTopic, message, EnumKafkaOperation.INJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();

        ValidatedELRModel model = new ValidatedELRModel();
        model.setId(guidForTesting);
        model.setRawMessage(testHL7Message);

        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(model));

        kafkaConsumerService.handleMessageForXmlConversionElr(value, xmlPrepTopic, EnumKafkaOperation.INJECTION.name());

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void xmlPreparationConsumerTestReInjection() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(xmlPrepTopic);
        String message =  guidForTesting;
        produceMessage(xmlPrepTopic, message, EnumKafkaOperation.REINJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();


        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setMessage(testHL7Message);
        when(elrDeadLetterRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(model));

        when(iHl7v2Validator.MessageStringValidation(eq(testHL7Message)))
                .thenReturn(testHL7Message);

        kafkaConsumerService.handleMessageForXmlConversionElr(value, xmlPrepTopic, EnumKafkaOperation.REINJECTION.name());

        verify(iHl7v2Validator, times(1)).MessageStringValidation(eq(testHL7Message));
        verify(elrDeadLetterRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void fhirPreparationConsumerTest() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(fhirPrepTopic);
        String message =  guidForTesting;
        produceMessage(fhirPrepTopic, message, EnumKafkaOperation.INJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();

        ValidatedELRModel model = new ValidatedELRModel();
        model.setId(guidForTesting);
        model.setRawMessage(testHL7Message);

        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(model));


        kafkaConsumerService.handleMessageForFhirConversionElr(value, fhirPrepTopic, EnumKafkaOperation.INJECTION.name());

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void fhirPreparationConsumerTestReInjection() {
        // Produce a test message to the topic
        initialDataInsertionAndSelection(fhirPrepTopic);
        String message =  guidForTesting;
        produceMessage(fhirPrepTopic, message, EnumKafkaOperation.REINJECTION);

        // Consume the message
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        // Perform assertions
        assertEquals(1, records.count());

        ConsumerRecord<String, String> firstRecord = records.iterator().next();
        String value = firstRecord.value();

        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setMessage(testHL7Message);
        when(elrDeadLetterRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(model));

        when(iHl7v2Validator.MessageStringValidation(eq(testHL7Message)))
                .thenReturn(testHL7Message);


        kafkaConsumerService.handleMessageForFhirConversionElr(value, fhirPrepTopic, EnumKafkaOperation.REINJECTION.name());

        verify(iHl7v2Validator, times(1)).MessageStringValidation(eq(testHL7Message));
        verify(elrDeadLetterRepository, times(1)).findById(eq(guidForTesting));

    }

    @Test
    public void dltHandlerLogicForRawPipeline() {
        initialDataInsertionAndSelection(rawTopic);
        String message =  guidForTesting;

        RawERLModel rawModel = new RawERLModel();
        rawModel.setId(guidForTesting);
        rawModel.setType("HL7");
        rawModel.setPayload(testHL7Message);
        when(iRawELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(rawModel));

        kafkaConsumerService.handleDlt(message, rawTopic + "_dlt", "n/a", errorMessage, "0", rawTopic);

        verify(iRawELRRepository, times(1)).findById(eq(guidForTesting));
    }

    @Test
    public void dltHandlerLogicForValidatePipeline() {
        initialDataInsertionAndSelection(validateTopic);
        String message =  guidForTesting;

        ValidatedELRModel rawModel = new ValidatedELRModel();
        rawModel.setId(guidForTesting);
        rawModel.setRawMessage(testHL7Message);
        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(rawModel));

        kafkaConsumerService.handleDlt(message, validateTopic + "_dlt", "n/a", errorMessage, "0", validateTopic);

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));
    }

    @Test
    public void dltHandlerLogicForPrepXMLPipeline() {
        initialDataInsertionAndSelection(xmlPrepTopic);
        String message =  guidForTesting;

        ValidatedELRModel rawModel = new ValidatedELRModel();
        rawModel.setId(guidForTesting);
        rawModel.setRawMessage(testHL7Message);
        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(rawModel));

        kafkaConsumerService.handleDlt(message, xmlPrepTopic + "_dlt", "n/a", errorMessage, "0", xmlPrepTopic);

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));
    }

    @Test
    public void dltHandlerLogicForPrepFhirPipeline() {
        initialDataInsertionAndSelection(fhirPrepTopic);
        String message =  guidForTesting;

        ValidatedELRModel rawModel = new ValidatedELRModel();
        rawModel.setId(guidForTesting);
        rawModel.setRawMessage(testHL7Message);
        when(iValidatedELRRepository.findById(eq(guidForTesting)))
                .thenReturn(Optional.of(rawModel));

        kafkaConsumerService.handleDlt(message, fhirPrepTopic + "_dlt", "n/a", errorMessage, "0", fhirPrepTopic);

        verify(iValidatedELRRepository, times(1)).findById(eq(guidForTesting));
    }

    private void initialDataInsertionAndSelection(String dltSourceMessage) {
        //region CONNECTION
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(mssqlserver.getJdbcUrl(), mssqlserver.getUsername(), mssqlserver.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //endregion

        //region INITIAL INSERTION - insert dlt data to test container db
        String sqlInsert = "INSERT INTO [NBS_DataIngest].[dbo].[elr_dlt] (" +
                "error_message_id, error_message_source, error_stack_trace,error_stack_trace_short,message, dlt_status, dlt_occurrence, " +
                "created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "NEWID(), '" + dltSourceMessage +"', " + "'Sample Error Stack Trace','Sample Error Stack Trace','message', 'ERROR', 1, " +
                "'system', 'system', GETDATE(), NULL" +
                ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //endregion -  -,

        //region INITIAL SELECTION - unique id from dlt for container testing
        String sqlSelect = "SELECT TOP 1 * FROM [NBS_DataIngest].[dbo].[elr_dlt];";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlSelect);

            // Iterate over the ResultSet to get the first record
            while(rs.next()) {
                guidForTesting = rs.getString("error_message_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //endregion

        //region STEP INSERTION - dependent tables, insertion for elr_raw, elr_fhir, and elr_validated
        String rawElrQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_raw] (" +
                "id, message_type, payload, created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " + "'Sample Message Type', 'Sample Payload', 'system', 'system', GETDATE(), NULL" +
                ");";

        String fhirElrQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_fhir] (" +
                "id, fhir_message, raw_message_id, created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " +"'Sample FHIR Message', 'Sample Raw Message ID', 'system', 'system', GETDATE(), NULL" +
                ");";

        String elrValidateQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_validated] (" +
                "id, raw_message_id, message_type, message_version, validated_message, hashed_hl7_string, " +
                "created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " + "'Sample Raw Message ID', 'Sample Message Type', 'Sample Message Version', 'Sample Validated Message', " +
                "NULL, 'system', 'system', GETDATE(), NULL" +
                ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(rawElrQuery);
            stmt.execute(fhirElrQuery);
            stmt.execute(elrValidateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //endregion
    }


    private void produceMessage(String topic, String message, EnumKafkaOperation operation) {
        // Create Kafka producer properties
        Properties producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  StringSerializer.class.getName());

        // Create the Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

        String uniqueID = "HL7" + "_" + UUID.randomUUID();

        // Send the message to the Kafka topic
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, uniqueID, message);
        record.headers().add(KafkaHeaderValue.MessageOperation, operation.name().getBytes());
        producer.send(record);

        // Flush and close the producer
        producer.flush();
        producer.close();
    }



}