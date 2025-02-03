package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;

@ExtendWith(MockitoExtension.class)
class PersonReaderTest {

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;

  @Test
  void initializesReaderWithDefaultLastProcessedId() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    PersonReader reader = new PersonReader(dataSource, 0L);

    assertThat(reader).isNotNull();
    assertThat(reader.getPageSize()).isEqualTo(10000);
    assertThat(reader.getDataSourceInstance()).isEqualTo(dataSource);
    assertThat(reader.getRowMapperInstance()).isNotNull();
  }

  @Test
  void testWhereClauseWithLastProcessedIdZero() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    PersonReader reader = new PersonReader(dataSource, 0L);
    PagingQueryProvider queryProvider = reader.getQueryProviderInstance();

    assertThat(queryProvider).isNotNull();
    String query = queryProvider.generateFirstPageQuery(10);
    assertThat(query).contains("WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT' AND person_uid > 0");
  }

  @Test
  void initializesReaderWithNonZeroLastProcessedId() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    long lastProcessedId = 500L;
    PersonReader reader = new PersonReader(dataSource, lastProcessedId);

    assertThat(reader).isNotNull();
  }

  @Test
  void testWhereClauseWithDifferentLastProcessedIdValues() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    long lastProcessedId = 100L;
    PersonReader reader = new PersonReader(dataSource, lastProcessedId);

    PagingQueryProvider queryProvider = reader.getQueryProviderInstance();
    assertThat(queryProvider).isNotNull();

    String query = queryProvider.generateFirstPageQuery(10); // Generate SQL for the first page
    assertThat(query).contains("WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT' AND person_uid > 100");
  }

  @Test
  void testSortKeyIsApplied() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    PersonReader reader = new PersonReader(dataSource, 50L);
    PagingQueryProvider queryProvider = reader.getQueryProviderInstance();

    assertThat(queryProvider).isNotNull();
    assertThat(queryProvider.getSortKeys()).containsKey("person_uid");
  }

  @Test
  void testRowMapperIsSet() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");

    PersonReader reader = new PersonReader(dataSource, 100L);

    assertThat(reader.getRowMapperInstance()).isInstanceOf(NbsPersonMapper.class);
  }

}
