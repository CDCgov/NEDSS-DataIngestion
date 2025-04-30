package gov.cdc.dataprocessing.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.thread}")
    private Integer thread;

    @Value("${spring.kafka.group-id}")
    private String groupId;

    @Value("${spring.kafka.group-id-phc}")
    private String groupIdPhc;

    @Value("${spring.kafka.group-id-lab}")
    private String groupIdLab;

    @Value("${spring.kafka.group-id-edx}")
    private String groupIdEdx;

    private Map<String, Object> baseConsumerConfigs(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024); // Fetch at least 1MB of data
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Wait up to 500ms for data
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 10 * 1024 * 1024); // Fetch up to 10MB per partition
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Max 500 records per poll
//        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // Allow 5 minutes for processing
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 30-second session timeout
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // Heartbeat every 10 seconds
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit
        return config;
    }

    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        return new DefaultKafkaConsumerFactory<>(baseConsumerConfigs(groupId));
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> createContainerFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(groupId));
        factory.setConcurrency(thread);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryStep1() {
        return createContainerFactory(groupId);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryStep2() {
        return createContainerFactory(groupIdPhc);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryStep3() {
        return createContainerFactory(groupIdLab);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryStep4() {
        return createContainerFactory(groupIdEdx);
    }
}