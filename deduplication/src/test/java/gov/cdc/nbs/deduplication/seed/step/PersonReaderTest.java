package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
class PersonReaderTest {

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;

  @Mock
  private NamedParameterJdbcTemplate mpiNamedJdbcTemplate;

  private PersonReader reader;

  @BeforeEach
  void setUp() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");
    reader = new PersonReader(dataSource, mpiNamedJdbcTemplate);
  }

  @Test
  void initializesReader() {
    assertThat(reader).isNotNull();
  }

  @Test
  void beforeStep_UpdatesQueryProvider_WhenNoLastProcessedId() throws Exception {
    doReturn(null).when(mpiNamedJdbcTemplate).queryForObject(
        "SELECT MAX(external_person_id) FROM mpi_patient",
        Collections.emptyMap(),
        Long.class
    );

    reader.beforeStep();
    assertQueryProviderNotNull();
  }

  @Test
  void beforeStep_UpdatesQueryProvider_WithLastProcessedId() throws Exception {
    Long lastProcessedId = 10014306L;
    doReturn(lastProcessedId).when(mpiNamedJdbcTemplate).queryForObject(
        "SELECT MAX(external_person_id) FROM mpi_patient",
        Collections.emptyMap(),
        Long.class
    );

    reader.beforeStep();
    assertQueryProviderNotNull();
  }

  @Test
  void beforeStep_HandlesEmptyResultDataAccessException_WhenTableIsEmpty() throws Exception {
    doThrow(new EmptyResultDataAccessException(1)).when(mpiNamedJdbcTemplate).queryForObject(
        "SELECT MAX(external_person_id) FROM mpi_patient",
        Collections.emptyMap(),
        Long.class
    );

    reader.beforeStep();
    assertQueryProviderNotNull();
  }

  private void assertQueryProviderNotNull() throws Exception {
    Field queryProviderField = JdbcPagingItemReader.class.getDeclaredField("queryProvider");
    queryProviderField.setAccessible(true);
    PagingQueryProvider queryProvider = (PagingQueryProvider) queryProviderField.get(reader);
    assertThat(queryProvider).isNotNull();
  }

}
