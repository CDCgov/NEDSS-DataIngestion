package gov.cdc.nbs.deduplication.config.container;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("test")
public class RecordLinkageContainers {

  private static Network network = Network.newNetwork();

  @SuppressWarnings("resource") // We don't want to close this
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres")
      .withInitScript("postgres.sql")
      .withNetwork(network)
      .withNetworkAliases("postgres");

  @SuppressWarnings("resource") // We don't want to close this
  private static final GenericContainer<?> RL_CONTAINER = new GenericContainer<>(
      "ghcr.io/cdcgov/recordlinker:latest")
      .withExposedPorts(8080)
      .withNetwork(network)
      .withNetworkAliases("recordLinkage")
      .withEnv("DB_URI", "postgresql+psycopg2://test:test@postgres:5432/postgres");

  static {
    POSTGRES_CONTAINER.start();
    // Dynamic property source does not play well with @ConfigurationProperties used
    // in DataSourceConfig so properties are set directly
    System.setProperty("spring.datasource.mpi.url", POSTGRES_CONTAINER.getJdbcUrl());
    System.setProperty("spring.datasource.mpi.username", POSTGRES_CONTAINER.getUsername());
    System.setProperty("spring.datasource.mpi.password", POSTGRES_CONTAINER.getPassword());

    RL_CONTAINER.start();
    System.setProperty("recordLinkage.url", "http://localhost:" + RL_CONTAINER.getMappedPort(8080));
  }

}
