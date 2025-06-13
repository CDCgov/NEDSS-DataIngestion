package gov.cdc.nbs.deduplication.config.container;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

class RecordLinkageInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  @SuppressWarnings("resource") // We don't want to close these containers
  public void initialize(@NonNull final ConfigurableApplicationContext context) {
    final Network network = Network.newNetwork();
    final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
        .withNetwork(network)
        .withDatabaseName("postgres")
        .withNetworkAliases("postgres");

    final GenericContainer<?> recordLinkageContainer = new GenericContainer<>(
        "ghcr.io/cdcgov/recordlinker:v25.8.0")
        .withExposedPorts(8070)
        .withEnv("PORT", "8070")
        .withNetwork(network)
        .withNetworkAliases("recordLinkage");

    postgresContainer.start();

    System.setProperty("spring.datasource.mpi.url", postgresContainer.getJdbcUrl());
    System.setProperty("spring.datasource.mpi.username", postgresContainer.getUsername());
    System.setProperty("spring.datasource.mpi.password", postgresContainer.getPassword());

    final String dbUri = String.format(
        "postgresql+psycopg2://%s:%s@postgres:5432/postgres",
        postgresContainer.getUsername(),
        postgresContainer.getPassword());

    recordLinkageContainer.addEnv("DB_URI", dbUri);
    recordLinkageContainer.addEnv("API_ROOT_PATH", "/api/record-linker");
    recordLinkageContainer.start();
    System.setProperty("deduplication.recordLinker.url",
        "http://localhost:" + recordLinkageContainer.getMappedPort(8070) + "/api/record-linker");

  }
}
