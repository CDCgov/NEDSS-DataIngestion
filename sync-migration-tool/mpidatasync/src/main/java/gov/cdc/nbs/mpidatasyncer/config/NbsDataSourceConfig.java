package gov.cdc.nbs.mpidatasyncer.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    basePackages = "gov.cdc.nbs.mpidatasyncer.repository.nbs",
    entityManagerFactoryRef = "nbsEntityManagerFactory",
    transactionManagerRef = "nbsTransactionManager"
)
public class NbsDataSourceConfig {

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.nbs")
  public DataSourceProperties nbsDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.nbs")
  public DataSource nbsDataSource() {
    return nbsDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean nbsEntityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(nbsDataSource())
        .packages("gov.cdc.nbs.mpidatasyncer.entity.nbs")
        .build();
  }

  @Primary
  @Bean
  public PlatformTransactionManager nbsTransactionManager(EntityManagerFactoryBuilder builder) {
    EntityManagerFactory entityManagerFactory = nbsEntityManagerFactory(builder).getObject();

    if (entityManagerFactory == null) {
      throw new IllegalStateException("NbsEntityManagerFactory must not be null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }
}
