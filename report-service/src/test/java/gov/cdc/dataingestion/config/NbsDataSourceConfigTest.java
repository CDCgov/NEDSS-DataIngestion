package gov.cdc.dataingestion.config;

import com.zaxxer.hikari.HikariDataSource;
import gov.cdc.dataingestion.config.NbsDataSourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@EnableJpaRepositories(
        entityManagerFactoryRef = "nbsEntityManagerFactory",
        transactionManagerRef = "nbsTransactionManager",
        basePackages = {
                "gov.cdc.dataingestion.nbs.repository"
        }
)
public class NbsDataSourceConfigTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.register(TestConfiguration.class);
        context.refresh();
    }

    @Test
    public void nbsDataSource_BeanIsDefined() {
        // Act
        DataSource dataSource = context.getBean("nbsDataSource", DataSource.class);

        // Assert
        Assertions.assertNotNull(dataSource);
        Assertions.assertEquals(HikariDataSource.class, dataSource.getClass());
    }

    @Configuration
    @PropertySource("classpath:/application.yaml")
    public static class TestConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.nbs")
        public DataSource nbsDataSource() {
            return DataSourceBuilder.create().build();
        }

        @Bean
        public NbsDataSourceConfig nbsDataSourceConfig() {
            return new NbsDataSourceConfig();
        }
    }
}
