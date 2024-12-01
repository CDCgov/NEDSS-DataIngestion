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

@Configuration
@EnableJpaRepositories(
    basePackages = "gov.cdc.nbs.mpidatasyncer.repository.mpi",
    entityManagerFactoryRef = "mpiEntityManagerFactory",
    transactionManagerRef = "mpiTransactionManager"
)
public class MpiDataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.mpi")
  public DataSourceProperties mpiDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.mpi")
  public DataSource mpiDataSource() {
    return mpiDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean mpiEntityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(mpiDataSource())
        .packages("gov.cdc.nbs.mpidatasyncer.entity.mpi")
        .build();
  }

  @Bean
  public PlatformTransactionManager mpiTransactionManager(EntityManagerFactoryBuilder builder) {

    EntityManagerFactory entityManagerFactory = mpiEntityManagerFactory(builder).getObject();
    if (entityManagerFactory == null) {
      throw new IllegalStateException("MpiEntityManagerFactory must not be null");
    }
    return new JpaTransactionManager(entityManagerFactory);
  }
}
