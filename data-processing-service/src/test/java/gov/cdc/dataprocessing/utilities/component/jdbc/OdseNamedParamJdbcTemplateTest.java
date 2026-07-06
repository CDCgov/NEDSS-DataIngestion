package gov.cdc.dataprocessing.utilities.component.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcOperations;

public class OdseNamedParamJdbcTemplateTest {
  @Test
  void setsReleaseVersion_whenQueryReturnsValue() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(String.class))).thenReturn("1.2.3");

    DataSource ds = mock(DataSource.class);

    OdseNameParamJdbcTemplate template =
        new OdseNameParamJdbcTemplate(ds) {
          @Override
          public JdbcOperations getJdbcOperations() {
            return jdbcOps;
          }
        };

    template.afterPropertiesSet();

    verify((InitializingBean) jdbcOps).afterPropertiesSet();
    assertEquals("1.2.3", template.getNbsReleaseVersion());
  }

  @Test
  void leavesReleaseVersionNull_whenQueryReturnsEmpty() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(String.class))).thenReturn("6.0.19.1");

    DataSource ds = mock(DataSource.class);

    OdseNameParamJdbcTemplate template =
        new OdseNameParamJdbcTemplate(ds) {
          @Override
          public JdbcOperations getJdbcOperations() {
            return jdbcOps;
          }
        };

    template.afterPropertiesSet();

    // same version
    String inputVersion = "6.0.19.1";
    int result = template.compareVersionToRelease(inputVersion);

    assertEquals(0, result);

    // newer version
    inputVersion = "6.0.19.2";
    result = template.compareVersionToRelease(inputVersion);

    assertEquals(1, result);

    // newer version
    inputVersion = "7.0.13.1";
    result = template.compareVersionToRelease(inputVersion);

    assertEquals(1, result);

    // older version
    inputVersion = "6.0.17.1";
    result = template.compareVersionToRelease(inputVersion);

    assertEquals(-1, result);
  }
}
