package gov.cdc.nbs.deduplication.seed.step;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonReaderTest {

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;

  @Test
  void initializesReader() throws Exception {
    // Mock primary and deduplication data sources
    DataSource nbsDataSource = mock(DataSource.class);
    DataSource deduplicationDataSource = mock(DataSource.class);

    // Mock connection and metadata for deduplication database
    Connection connection = mock(Connection.class);
    DatabaseMetaData metadata = mock(DatabaseMetaData.class);

    // Mock behaviors for deduplication data source
    when(deduplicationDataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    // Mock the method call for fetching the last high-water mark
    when(connection.prepareStatement(Mockito.anyString()))
            .thenReturn(mock(java.sql.PreparedStatement.class));

    // Initialize the reader
    final PersonReader reader = new PersonReader(nbsDataSource, deduplicationDataSource);

    // Assertions
    assertThat(reader).isNotNull();
    verify(deduplicationDataSource, times(1)).getConnection(); // Ensure deduplication data source is accessed
  }


}
