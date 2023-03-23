package gov.cdc.dataingestion.consumer.validationservice.service;

import gov.cdc.dataingestion.consumer.validationservice.integration.CsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.integration.HL7v2Validator;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.junit.Before;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
public class KafkaConsumerServiceTest {
    private KafkaConsumerService target;
    private HL7v2Validator hl7v2Validator;
    private CsvValidator csvValidator;
    private KafkaProducerService kafkaProducerService;

    private MockConsumer<String, String> consumer;

    @Before
    public void setupMock() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        hl7v2Validator = mock(HL7v2Validator.class);
        csvValidator = mock(CsvValidator.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        target = new KafkaConsumerService(kafkaProducerService);

    }
}