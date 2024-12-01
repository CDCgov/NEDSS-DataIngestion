package gov.cdc.nbs.mpidatasyncer.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "gov.cdc.nbs.mpidatasyncer.repository.syncer",
    entityManagerFactoryRef = "syncerEntityManagerFactory",
    transactionManagerRef = "syncerTransactionManager"
)
public class SyncerDataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.syncer")
  public DataSourceProperties syncerDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.syncer")
  public DataSource syncerDataSource() {
    return syncerDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean syncerEntityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(syncerDataSource())
        .packages("gov.cdc.nbs.mpidatasyncer.entity.syncer")
        .properties(hibernateProperties()) // Set Hibernate properties
        .build();
  }

  @Bean
  public PlatformTransactionManager syncerTransactionManager(EntityManagerFactoryBuilder builder) {
    EntityManagerFactory entityManagerFactory = syncerEntityManagerFactory(builder).getObject();

    if (entityManagerFactory == null) {
      throw new IllegalStateException("SyncerEntityManagerFactory must not be null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }

  private Map<String, Object> hibernateProperties() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "update");
    return properties;
  }
}
