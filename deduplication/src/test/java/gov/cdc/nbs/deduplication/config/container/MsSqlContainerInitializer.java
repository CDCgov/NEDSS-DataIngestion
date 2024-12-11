package gov.cdc.nbs.deduplication.config.container;

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

    System.setProperty("spring.datasource.deduplication.url", container.getJdbcUrl() + ";database=deduplication");
    System.setProperty("spring.datasource.deduplication.username", username);
    System.setProperty("spring.datasource.deduplication.password", password);

    System.setProperty("spring.datasource.nbs.url", container.getJdbcUrl() + ";database=nbs_odse");
    System.setProperty("spring.datasource.nbs.username", username);
    System.setProperty("spring.datasource.nbs.password", password);

  }

}
