package gov.cdc.nbs.deduplication.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

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

  @Primary
  @Bean("deduplicationNamedTemplate")
  public NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate(
      @Qualifier("deduplicationTemplate") JdbcTemplate template) {
    return new NamedParameterJdbcTemplate(template);
  }

  @Bean("deduplicationJdbcClient")
  public JdbcClient deduplicationJdbcClient(@Qualifier("deduplication") DataSource deduplicationDataSource) {
    return JdbcClient.create(deduplicationDataSource);
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

  @Bean("nbsNamedTemplate")
  public NamedParameterJdbcTemplate nbsNamedJdbcTemplate(
      @Qualifier("nbsTemplate") JdbcTemplate template) {
    return new NamedParameterJdbcTemplate(template);
  }

  @Bean("nbsJdbcClient")
  public JdbcClient nbsJdbcClient(@Qualifier("nbs") DataSource nbsDataSource) {
    return JdbcClient.create(nbsDataSource);
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

  @Bean("mpiNamedTemplate")
  public NamedParameterJdbcTemplate mpiNamedJdbcTemplate(
      @Qualifier("mpiTemplate") JdbcTemplate template) {
    return new NamedParameterJdbcTemplate(template);
  }

  @Bean("mpiJdbcClient")
  public JdbcClient mpiJdbcClient(@Qualifier("mpi") DataSource mpiDataSource) {
    return JdbcClient.create(mpiDataSource);
  }

}
