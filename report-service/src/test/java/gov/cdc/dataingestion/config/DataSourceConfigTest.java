package gov.cdc.dataingestion.config;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@EnableJpaRepositories(
        entityManagerFactoryRef = "ingestEntityManagerFactory",
        transactionManagerRef = "ingestTransactionManager",
        basePackages = {
                "gov.cdc.dataingestion.validation.repository",
                "gov.cdc.dataingestion.report.repository",
                "gov.cdc.dataingestion.conversion.repository",
                "gov.cdc.dataingestion.deadletter.repository",
                "gov.cdc.dataingestion.registration.repository",
                "gov.cdc.dataingestion.security.repository"
        }
)
public class DataSourceConfigTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.register(TestConfiguration.class);
        context.refresh();
    }

    @Test
    public void dataSource_BeanIsDefined() {
        // Act
        DataSource dataSource = context.getBean(DataSource.class);

        // Assert
        Assertions.assertNotNull(dataSource);
        Assertions.assertEquals(HikariDataSource.class, dataSource.getClass());
    }

    @Configuration
    @PropertySource("classpath:/application.yaml")
    @EnableConfigurationProperties
    public static class TestConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource")
        public DataSource dataSource() {
            return DataSourceBuilder.create().build();
        }
    }
}
