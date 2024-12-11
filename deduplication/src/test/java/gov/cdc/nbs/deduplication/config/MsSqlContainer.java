package gov.cdc.nbs.deduplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MSSQLServerContainer;

@Configuration
@Profile("test")
public class MsSqlContainer {
  @SuppressWarnings("resource") // We don't want to close this

  private static final MSSQLServerContainer<?> MSSQL_CONTAINER = new MSSQLServerContainer<>(
      "mcr.microsoft.com/mssql/server:2022-latest")
      .acceptLicense()
      .withInitScript("mssql.sql");

  static {
    MSSQL_CONTAINER.start();
    // Dynamic property source does not play well with @ConfigurationProperties used
    // in DataSourceConfig so properties are set directly
    System.setProperty("spring.datasource.deduplication.url", MSSQL_CONTAINER.getJdbcUrl() + ";database=deduplication");
    System.setProperty("spring.datasource.deduplication.username", MSSQL_CONTAINER.getUsername());
    System.setProperty("spring.datasource.deduplication.password", MSSQL_CONTAINER.getPassword());

    System.setProperty("spring.datasource.nbs.url", MSSQL_CONTAINER.getJdbcUrl() + ";database=nbs_odse");
    System.setProperty("spring.datasource.nbs.username", MSSQL_CONTAINER.getUsername());
    System.setProperty("spring.datasource.nbs.password", MSSQL_CONTAINER.getPassword());
  }

}
