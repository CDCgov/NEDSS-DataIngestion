package gov.cdc.dataingestion.config;

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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers = "";

    // Higher value for more intensive operation, also increase latency
    // default is 30000, equivalent to 5 min
    @Value("${spring.kafka.consumer.maxPollIntervalMs}")
    private String maxPollInterval = "";


    @Value("${spring.kafka.thread}")
    private Integer thread;

    @Value("${spring.kafka.group-id-default}")
    private String groupIdDefault;

    @Value("${spring.kafka.group-id-raw}")
    private String groupIdRaw;

    @Value("${spring.kafka.group-id-raw-xml}")
    private String groupIdRawXml;

    @Value("${spring.kafka.group-id-validate}")
    private String groupIdValidate;

    @Value("${spring.kafka.group-id-xml}")
    private String groupIdXml;

    @Value("${spring.kafka.group-id-ecr-cda}")
    private String groupIdEcrCda;

    @Value("${spring.kafka.group-id-dlt-manual}")
    private String groupIdDltManual;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdDefault);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // Config for kafka listener aka consumer
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return  factory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryRaw() {
        return createContainerFactory(groupIdRaw);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryRawXml() {
        return createContainerFactory(groupIdRawXml);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryValidate() {
        return createContainerFactory(groupIdValidate);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryXml() {
        return createContainerFactory(groupIdXml);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryEcrCda() {
        return createContainerFactory(groupIdEcrCda);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryDltManual() {
        return createContainerFactory(groupIdDltManual);
    }



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
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500); // Max 500 records per poll
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000); // Allow 5 minutes for processing
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 30-second session timeout
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // Heartbeat every 10 seconds
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // Manual commit
        return config;
    }

    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        return new DefaultKafkaConsumerFactory<>(baseConsumerConfigs(groupId));
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> createContainerFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(groupId));
        factory.setConcurrency(thread);
        return factory;
    }
}