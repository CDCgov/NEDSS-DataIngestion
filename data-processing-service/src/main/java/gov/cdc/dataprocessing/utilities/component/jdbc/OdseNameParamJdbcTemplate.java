package gov.cdc.dataprocessing.utilities.component.jdbc;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OdseNameParamJdbcTemplate extends NamedParameterJdbcTemplate {
  @Getter private String nbsReleaseVersion;

  // This is not an IP address. It is a version number.
  public static final String RELEASE_VERSION_RECEIVED_TIME_ENABLED = "6.0.19.1";

  public OdseNameParamJdbcTemplate(@Qualifier("odseDataSource") DataSource dataSource) {
    super(dataSource);
  }

  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    if (getJdbcOperations() instanceof InitializingBean initializingBean) {
      // make sure DataSource is ready
      initializingBean.afterPropertiesSet();
    }
    setNbsReleaseVersion();
  }

  private void setNbsReleaseVersion() {
    String sql =
        "SELECT TOP 1 Version FROM dbo.NBS_Release WHERE NBS_release_uid = (SELECT MAX(NBS_release_uid) FROM dbo.NBS_Release);";
    try {
      this.nbsReleaseVersion = getJdbcOperations().queryForObject(sql, String.class);
    } catch (EmptyResultDataAccessException e) {
      this.nbsReleaseVersion = null;
    }
  }

  /*
      This method takes in a version string and compares it to the RELEASE version
      -1 -> input is older
      1 -> CODE_BASE version is older
      0 -> input version is the same
  */
  public int compareVersionToRelease(String inputVersion) {
    String[] levels1 = inputVersion.split("\\.");
    String[] levels2 = this.nbsReleaseVersion.split("\\.");

    int length = Math.max(levels1.length, levels2.length);

    for (int i = 0; i < length; i++) {
      // If one version is shorter, treat missing segments as 0
      int v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
      int v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;

      if (v1 < v2) return -1;
      if (v1 > v2) return 1;
    }

    return 0; // Both versions are identical
  }
}
