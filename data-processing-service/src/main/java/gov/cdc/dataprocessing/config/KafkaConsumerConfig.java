package gov.cdc.dataprocessing.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@EnableTransactionManagement
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.group-id}")
    private String groupId = "";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers = "";

    // Higher value for more intensive operation, also increase latency
    // default is 30000, equivalent to 5 min
    @Value("${spring.kafka.consumer.maxPollIntervalMs}")
    private String maxPollInterval = "";

    private final ProducerFactory<String, String> producerFactory;
    private final KafkaTransactionManager<String, String> kafkaTransactionManager;

    public KafkaConsumerConfig(ProducerFactory<String, String> producerFactory, KafkaTransactionManager<String, String> kafkaTransactionManager) {
        this.producerFactory = producerFactory;
        this.kafkaTransactionManager = kafkaTransactionManager;
    }




    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "30000");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");  // Disable auto commit for manual commit
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);  // Fetch up to 10 messages per poll

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);  // Enable batch processing
        factory.getContainerProperties().setTransactionManager(kafkaTransactionManager);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return kafkaTransactionManager;
    }
}