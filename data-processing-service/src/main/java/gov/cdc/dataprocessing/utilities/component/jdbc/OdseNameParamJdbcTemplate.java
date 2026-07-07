package gov.cdc.dataprocessing.utilities.component.jdbc;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import jakarta.el.PropertyNotFoundException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OdseNameParamJdbcTemplate extends NamedParameterJdbcTemplate {
  private static final Logger log = LoggerFactory.getLogger(OdseNameParamJdbcTemplate.class);

  @Getter private String nbsReleaseVersion;
  @Getter private String nbsReleaseVersionReceivedTime;
  @Getter private boolean isNbsDocReceivedTimeEnabled;

  public OdseNameParamJdbcTemplate(
          @Qualifier("odseDataSource") DataSource dataSource,
          @Value("${nedss.nbs-release-version-doc-received-time}") String nbsReleaseVersionReceivedTime
  ) {
    super(dataSource);
    this.nbsReleaseVersionReceivedTime = nbsReleaseVersionReceivedTime;
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
    log.info("NBS_Release Version detected: {}", this.nbsReleaseVersion);
  }

  private void setIsNbsDocReceivedTimeEnabled() {
      if (this.nbsReleaseVersionReceivedTime == null) {
          throw new PropertyNotFoundException("nedss.nbs-release-version-doc-received-time detected as null.");
      } else if (this.nbsReleaseVersion == null) {
          throw new IllegalStateException(
                  "NBS Release Version was not detected. Version comparison cannot be performed. Check the NBS_Release table.");
      }

      /*
      -1 -> detected version is older
      1 -> input version is newer
      0 -> input version is the same
       */

      String[] levels1 = inputVersion.split("\\.");
      String[] levels2 = this.nbsReleaseVersion.split("\\.");

      int length = Math.max(levels1.length, levels2.length);

      for (int i = 0; i < length; i++) {
          // If one version is shorter, treat missing segments as 0
          int v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
          int v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;

          if (v1 < v2) this.isNbsDocReceivedTimeEnabled = false;
          if (v1 > v2) this.isNbsDocReceivedTimeEnabled = true;
      }

      this.isNbsDocReceivedTimeEnabled = true;
  }

  /*
      This method takes in a version string and compares it to the RELEASE version
      -1 -> input is older
      1 -> input version is newer
      0 -> input version is the same
  */
  private int compareVersionToRelease(String inputVersion)
      throws IllegalArgumentException, IllegalStateException {
    if (inputVersion == null) {
      throw new IllegalArgumentException("The input version cannot be null.");
    } else if (this.nbsReleaseVersion == null) {
      throw new IllegalStateException(
          "NBS Release Version was not detected. Version comparison cannot be performed. Check the NBS_Release table.");
    }

     // Both versions are identical
  }
}
