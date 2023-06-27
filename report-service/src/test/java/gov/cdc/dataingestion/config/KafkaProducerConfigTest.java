package gov.cdc.dataingestion.config;

import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

class KafkaProducerConfigTest {

    private static KafkaProducerConfig kafkaProducerConfig;

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withStartupTimeout(Duration.ofMinutes(5));

    @BeforeAll
    public static void setUp() {
        kafkaContainer.start();
        kafkaProducerConfig = new KafkaProducerConfig();
    }

    @AfterAll
    public static void tearDown() {
        kafkaContainer.stop();
    }

    @Test
    void producerFactory_configValid() {

        // Act
        var target =
                kafkaProducerConfig.producerFactory();

        // Assert
        Assertions.assertNotNull(target);
    }

    @Test
    void kafkaTemplate_configValid() {

        // Act
        var target =
                kafkaProducerConfig.kafkaTemplate();

        // Assert
        Assertions.assertNotNull(target);
    }
}
