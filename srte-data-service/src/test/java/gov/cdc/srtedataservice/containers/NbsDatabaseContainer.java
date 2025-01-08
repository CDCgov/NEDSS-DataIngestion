package gov.cdc.srtedataservice.containers;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

class NbsDatabaseContainer<S extends MSSQLServerContainer<S>> extends MSSQLServerContainer<S> {
  private static final DockerImageName MS_SQL_SERVER_IMAGE = DockerImageName.parse(IMAGE);

  private String username;

  public NbsDatabaseContainer(final String image) {
    super(DockerImageName.parse(image).asCompatibleSubstituteFor(MS_SQL_SERVER_IMAGE));
    addEnv("ACCEPT_EULA", "Y");
  }

  @Override
  public S withUsername(final String username) {
    this.username = username;
    return self();
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other)
      return true;
    return other instanceof NbsDatabaseContainer<?> && super.equals(other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), username);
  }
}
