package gov.cdc.dataprocessing.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerConfigTest {

    @InjectMocks
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Value("${spring.kafka.group-id}")
    private String groupId;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.maxPollIntervalMs}")
    private String maxPollInterval;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize properties for the test
        groupId = "test-group-id";
        bootstrapServers = "localhost:9092";
        maxPollInterval = "60000";

        kafkaConsumerConfig.groupId = groupId;
        kafkaConsumerConfig.bootstrapServers = bootstrapServers;
        kafkaConsumerConfig.maxPollInterval = maxPollInterval;
    }

    @Test
    void testConsumerFactory() {
        ConsumerFactory<String, String> consumerFactory = kafkaConsumerConfig.consumerFactory();
        assertNotNull(consumerFactory);

        Map<String, Object> configs = consumerFactory.getConfigurationProperties();
        assertEquals(bootstrapServers, configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(groupId, configs.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals(maxPollInterval, configs.get(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG));
        assertEquals("false", configs.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
    }

    @Test
    void testKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaConsumerConfig.kafkaListenerContainerFactory();
        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory());
        assertNotEquals(kafkaConsumerConfig.consumerFactory(), factory.getConsumerFactory());
    }
}
