package gov.cdc.nbs.deduplication.config.container;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

class RecordLinkageInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  @SuppressWarnings("resource") // We don't want to close these containers
  public void initialize(@NonNull final ConfigurableApplicationContext context) {
    final Network network = MsSqlContainerInitializer.network;

    final GenericContainer<?> recordLinkageContainer = new GenericContainer<>(
        "ghcr.io/cdcgov/recordlinker:v25.8.0")
        .withExposedPorts(8070)
        .withEnv("PORT", "8070")
        .withNetwork(network)
        .withNetworkAliases("recordLinkage");

    String username = System.getProperty("spring.datasource.mpi.username");
    String password = System.getProperty("spring.datasource.mpi.password");
    final String dbUri = String.format(
        "mssql+pyodbc://%s:%s@mssql:1433/mpi?driver=ODBC+Driver+18+for+SQL+Server&TrustServerCertificate=yes",
        username,
        password);

    recordLinkageContainer.addEnv("DB_URI", dbUri);
    recordLinkageContainer.addEnv("API_ROOT_PATH", "/api/record-linker");
    recordLinkageContainer.start();
    System.setProperty("deduplication.recordLinker.url",
        "http://localhost:" + recordLinkageContainer.getMappedPort(8070) + "/api/record-linker");

  }
}
