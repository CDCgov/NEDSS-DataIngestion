package gov.cdc.dataprocessing.utilities.component.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

class OdseNameParamJdbcTemplateTest {

  @Test
  void enablesWhenQueryReturnsOne() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

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
    verify(jdbcOps).queryForObject(anyString(), eq(Integer.class));
    assertTrue(template.isNbsDocReceivedTimeEnabled());
  }

  @Test
  void disablesWhenQueryReturnsZero() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(Integer.class))).thenReturn(0);

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
    verify(jdbcOps).queryForObject(anyString(), eq(Integer.class));
    assertFalse(template.isNbsDocReceivedTimeEnabled());
  }

  @Test
  void setsFalseWhenQueryThrowsEmptyResult() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    DataSource ds = mock(DataSource.class);

    OdseNameParamJdbcTemplate template =
        new OdseNameParamJdbcTemplate(ds) {
          @Override
          public JdbcOperations getJdbcOperations() {
            return jdbcOps;
          }
        };

    // should not throw; exception is handled internally and flag set to false
    template.afterPropertiesSet();

    verify((InitializingBean) jdbcOps).afterPropertiesSet();
    verify(jdbcOps).queryForObject(anyString(), eq(Integer.class));
    assertFalse(template.isNbsDocReceivedTimeEnabled());
  }

  @Test
  void setsFalseWhenQueryReturnsNull() throws Exception {
    JdbcOperations jdbcOps =
        mock(JdbcOperations.class, withSettings().extraInterfaces(InitializingBean.class));
    when(jdbcOps.queryForObject(anyString(), eq(Integer.class))).thenReturn(null);

    DataSource ds = mock(DataSource.class);

    OdseNameParamJdbcTemplate template =
        new OdseNameParamJdbcTemplate(ds) {
          @Override
          public JdbcOperations getJdbcOperations() {
            return jdbcOps;
          }
        };

    // null leads to NullPointerException inside implementation which is caught; result should be
    // false
    template.afterPropertiesSet();

    verify((InitializingBean) jdbcOps).afterPropertiesSet();
    verify(jdbcOps).queryForObject(anyString(), eq(Integer.class));
    assertFalse(template.isNbsDocReceivedTimeEnabled());
  }
}
