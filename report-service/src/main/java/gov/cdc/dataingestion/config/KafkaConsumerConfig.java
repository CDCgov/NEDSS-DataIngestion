package gov.cdc.dataingestion.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;


import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.group-id}")
    private String groupId = "";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers = "";

    @Value("${spring.kafka.consumer.maxPollIntervalMs}")
    private String maxPollInterval = "";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

//    @Autowired
//    private KafkaErrorHandlerConfig errorHandler;
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
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

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaBlockingRetryContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setCommonErrorHandler(new DefaultErrorHandler(
//                errorHandler.deadLetterPublishingRecoverer(kafkaTemplate))
//        );
//        return factory;
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> multiTypeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // Other configurations
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.afterPropertiesSet();
        return factory;
    }

//    @Bean
//    public DefaultErrorHandler errorHandler() {
//        BackOff fixedBackOff = new FixedBackOff(1000, 1);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
//
//                (consumerRecord, exception) -> {
//                    System.out.println("Recovered: " + consumerRecord);
//
//                    // logic to execute when all the retry attemps are exhausted
//                Headers headers = consumerRecord.headers();
//                headers.add("TEST-TEST-TEST", "TEST-TEST-TEST".getBytes());
//                System.out.println("TEST-TEST-TEST");
//
//                consumerRecord.headers().add("TEST-TEST-TEST", "TEST-TEST-TEST".getBytes());
//            },
//            fixedBackOff);
//        errorHandler.addRetryableExceptions(Exception.class);
//     //   errorHandler.addNotRetryableExceptions(NullPointerException.class);
//        return errorHandler;
//    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler((rec, ex) -> {
            System.out.println("Recovered: " + rec);
        }, new FixedBackOff(0L, 1));
    }



}