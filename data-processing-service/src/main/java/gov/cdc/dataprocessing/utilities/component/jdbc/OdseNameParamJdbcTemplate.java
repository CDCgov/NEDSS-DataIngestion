package gov.cdc.dataprocessing.utilities.component.jdbc;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OdseNameParamJdbcTemplate extends NamedParameterJdbcTemplate {
  private static final Logger log = LoggerFactory.getLogger(OdseNameParamJdbcTemplate.class);

  @Getter private boolean isNbsDocReceivedTimeEnabled;

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
            "IF EXISTS ( SELECT 1 FROM NBS_ODSE.INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'NBS_Document' AND COLUMN_NAME = 'received_time') SELECT 1 AS column_exists ELSE SELECT 0 AS column_exists;";
    try {
      this.isNbsDocReceivedTimeEnabled =
          getJdbcOperations().queryForObject(sql, Integer.class) == 1;
    } catch (EmptyResultDataAccessException | NullPointerException e) {
      log.error(
          "Exception raised while querying for NBS_Document.received_time: {}", e.getMessage());
      this.isNbsDocReceivedTimeEnabled = false;
    }
    log.info("NBS_Release.received_time detected: {}", this.isNbsDocReceivedTimeEnabled);
  }
}
