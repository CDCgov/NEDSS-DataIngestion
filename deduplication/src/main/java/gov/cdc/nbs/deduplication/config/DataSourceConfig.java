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

  @Bean
  @Primary
  public JdbcTemplate deduplicationJdbcTemplate(@Qualifier("deduplication") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

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

}
