package gov.cdc.nbs.deduplication.batch.step;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnprocessedPreviousDayPersonReaderTest {

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

    final UnprocessedPreviousDayPersonReader reader = new UnprocessedPreviousDayPersonReader(dataSource, 10);
    assertThat(reader).isNotNull();
    assertThat(reader.getName()).isEqualTo("previousDayReader");
    assertThat(reader.getPageSize()).isEqualTo(10);
  }

}
