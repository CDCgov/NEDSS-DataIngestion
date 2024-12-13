package gov.cdc.nbs.deduplication.config.container;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("test")
public class PostgresContainer {

  @SuppressWarnings("resource") // We don't want to close this
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres")
      .withInitScript("postgres.sql");

  static {
    POSTGRES_CONTAINER.start();
    // Dynamic property source does not play well with @ConfigurationProperties used
    // in DataSourceConfig so properties are set directly
    System.setProperty("spring.datasource.mpi.url", POSTGRES_CONTAINER.getJdbcUrl());
    System.setProperty("spring.datasource.mpi.username", POSTGRES_CONTAINER.getUsername());
    System.setProperty("spring.datasource.mpi.password", POSTGRES_CONTAINER.getPassword());
  }

}
