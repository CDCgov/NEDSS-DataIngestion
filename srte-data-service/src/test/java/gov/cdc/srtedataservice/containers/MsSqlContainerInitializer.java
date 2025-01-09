package gov.cdc.srtedataservice.containers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.images.PullPolicy;

class MsSqlContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  @SuppressWarnings("resource") // We don't want to close this
  public void initialize(@NonNull final ConfigurableApplicationContext context) {
    String image = context.getEnvironment().getProperty("testing.database.mssql.image", "dataingestion-di-mssql");
    String username = context.getEnvironment().getProperty("testing.database.mssql.username");
    String password = context.getEnvironment().getProperty("testing.database.mssql.password");

    JdbcDatabaseContainer<?> container = new NbsDatabaseContainer<>(image)
        .withUsername(username)
        .withPassword(password)
        .withImagePullPolicy(PullPolicy.defaultPolicy());

    container.start();

    System.setProperty("spring.datasource.username", username);
    System.setProperty("spring.datasource.password", password);
    System.setProperty(
        "spring.datasource.nbs.url",
        container.getJdbcUrl() + ";databaseName=NBS_MSGOUTE;encrypt=true;trustServerCertificate=true;");
    System.setProperty(
        "spring.datasource.odse.url",
        container.getJdbcUrl() + ";databaseName=NBS_ODSE;encrypt=true;trustServerCertificate=true;");
    System.setProperty(
        "spring.datasource.srte.url",
        container.getJdbcUrl() + ";databaseName=NBS_SRTE;encrypt=true;trustServerCertificate=true;");

  }

}
