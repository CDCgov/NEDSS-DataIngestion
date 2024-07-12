package gov.cdc.dataingestion.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

class KafkaConsumerConfigTest {

    private static KafkaConsumerConfig kafkaConsumerConfig;

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withStartupTimeout(Duration.ofMinutes(5));

    @BeforeAll
    public static void setUp() {
        kafkaContainer.start();
        kafkaConsumerConfig = new KafkaConsumerConfig();
    }

    @AfterAll
    public static void tearDown() {
        kafkaContainer.stop();
    }

    @Test
    void kafkaListenerContainerFactory_ConfigurationIsValid() {

        // Act
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory =
                kafkaConsumerConfig.kafkaListenerContainerFactory();

        // Assert
        Assertions.assertNotNull(kafkaListenerContainerFactory);
    }
}
