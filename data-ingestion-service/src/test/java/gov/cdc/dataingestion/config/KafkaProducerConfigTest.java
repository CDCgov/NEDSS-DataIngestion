package gov.cdc.dataingestion.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
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
