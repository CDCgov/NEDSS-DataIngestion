package gov.cdc.nbs.deduplication.batch.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnprocessedPersonReaderTest {

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;


  @Test
  void initializesReader() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    final UnprocessedPersonReader reader = new UnprocessedPersonReader(dataSource, 10, 100);
    assertThat(reader).isNotNull();
  }

  @Test
  void shouldRead() throws Exception {
    Statement stmt = Mockito.mock(Statement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");
    when(connection.createStatement()).thenReturn(stmt);
    when(stmt.executeQuery(
        "SELECT TOP 1 person_uid FROM nbs_mpi_mapping WHERE status = 'U' AND person_uid=person_parent_uid ORDER BY person_uid ASC"))
        .thenReturn(resultSet);

    final UnprocessedPersonReader reader = new UnprocessedPersonReader(dataSource, 1, 2);
    reader.afterPropertiesSet();

    // chunk size 1, total to process of 2 means 2 pages should be read
    reader.doReadPage();
    assertThat(reader.getPagesRead()).isEqualTo(1);

    reader.doReadPage();
    assertThat(reader.getPagesRead()).isEqualTo(2);

    // third page read will skip reading
    reader.doReadPage();
    assertThat(reader.getPagesRead()).isEqualTo(2);
  }

  @Test
  void shouldResetPagesRead() throws Exception {
    Statement stmt = Mockito.mock(Statement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");
    when(connection.createStatement()).thenReturn(stmt);
    when(stmt.executeQuery(
        "SELECT TOP 1 person_uid FROM nbs_mpi_mapping WHERE status = 'U' AND person_uid=person_parent_uid ORDER BY person_uid ASC"))
        .thenReturn(resultSet);

    final UnprocessedPersonReader reader = new UnprocessedPersonReader(dataSource, 1, 2);
    reader.afterPropertiesSet();

    assertThat(reader.getPagesRead()).isZero();
    reader.doReadPage();
    assertThat(reader.getPagesRead()).isEqualTo(1);

    reader.resetPagesRead();
    assertThat(reader.getPagesRead()).isZero();
  }

  @Test
  void shouldNotRead() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    final UnprocessedPersonReader reader = new UnprocessedPersonReader(dataSource, 1, 0);
    reader.afterPropertiesSet();

    reader.doReadPage();
    assertThat(reader.getPagesRead()).isZero();
  }
}
