package gov.cdc.nbs.deduplication.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

  // Deduplication data source
  @Bean
  @ConfigurationProperties("spring.datasource.deduplication")
  public DataSourceProperties deduplicationProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean("deduplication")
  @ConfigurationProperties("spring.datasource.deduplication")
  public DataSource deduplicationDataSource() {
    return deduplicationProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Primary
  @Bean("deduplicationTemplate")
  public JdbcTemplate deduplicationJdbcTemplate(@Qualifier("deduplication") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  // NBS data source
  @Bean
  @ConfigurationProperties("spring.datasource.nbs")
  public DataSourceProperties nbsProperties() {
    return new DataSourceProperties();
  }

  @Bean("nbs")
  @ConfigurationProperties("spring.datasource.nbs")
  public DataSource nbsDataSource() {
    return nbsProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean("nbsTemplate")
  public JdbcTemplate nbsJdbcTemplate(@Qualifier("nbs") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  // MPI data source
  @Bean
  @ConfigurationProperties("spring.datasource.mpi")
  public DataSourceProperties mpiProperties() {
    return new DataSourceProperties();
  }

  @Bean("mpi")
  @ConfigurationProperties("spring.datasource.mpi")
  public DataSource mpiDataSource() {
    return mpiProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean("mpiTemplate")
  public JdbcTemplate mpiJdbcTemplate(@Qualifier("mpi") DataSource mpiDataSource) {
    return new JdbcTemplate(mpiDataSource);
  }

}
