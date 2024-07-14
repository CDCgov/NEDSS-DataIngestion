package gov.cdc.dataprocessing.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaProducerConfigTest {

    @InjectMocks
    private KafkaProducerConfig kafkaProducerConfig;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize properties for the test
        bootstrapServers = "localhost:9092";

        kafkaProducerConfig.bootstrapServers = bootstrapServers;
    }

    @Test
    void testProducerFactory() {
        ProducerFactory<String, String> producerFactory = kafkaProducerConfig.producerFactory();
        assertNotNull(producerFactory);

        Map<String, Object> configs = producerFactory.getConfigurationProperties();
        assertEquals(bootstrapServers, configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class, configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("true", configs.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
    }

    @Test
    void testKafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = kafkaProducerConfig.kafkaTemplate();
        assertNotNull(kafkaTemplate);
        assertEquals(DefaultKafkaProducerFactory.class, kafkaTemplate.getProducerFactory().getClass());
    }
}
