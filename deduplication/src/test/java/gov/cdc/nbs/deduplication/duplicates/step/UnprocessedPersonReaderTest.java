package gov.cdc.nbs.deduplication.duplicates.step;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;



@ExtendWith(MockitoExtension.class)
class UnprocessedPersonReaderTest {


  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;

  @Mock
  private Statement statement;

  @Mock
  private ResultSet resultSet;

  private UnprocessedPersonReader reader;

  private final LocalDate fixedPreviousDay = LocalDate.of(2024, 1, 1);

  @BeforeEach
  void setUp() throws Exception {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getDatabaseProductName()).thenReturn("sql server");
  }

  private void setupReader(int chunkSize, int totalToProcess) throws Exception {
    reader = new UnprocessedPersonReader(dataSource, chunkSize, totalToProcess);
    reader.setPreviousDay(fixedPreviousDay);
    reader.afterPropertiesSet();
  }

  @Test
  void initializesReader() throws Exception {
    setupReader(100, 10000);
    assertThat(reader).isNotNull();
  }

  @Test
  void readsFromPreviousDayFirst() throws Exception {
    when(connection.createStatement()).thenReturn(statement);
    when(resultSet.next()).thenReturn(true, false);
    when(statement.executeQuery(anyString())).thenReturn(resultSet);

    setupReader(1, 2);
    reader.doReadPage();

    assertThat(reader).isNotNull();
  }

  @Test
  void switchesToOlderWhenNoPreviousDayData() throws Exception {
    when(connection.createStatement()).thenReturn(statement);
    AtomicInteger executeQueryCount = new AtomicInteger(0);
    when(statement.executeQuery(anyString())).thenAnswer(invocation -> {
      int callNum = executeQueryCount.getAndIncrement();
      if (callNum == 0) {
        // First query: previous day - no results
        when(resultSet.next()).thenReturn(false);
        return resultSet;
      } else {
        // Second query: older than previous day - one result
        when(resultSet.next()).thenReturn(true, false);
        return resultSet;
      }
    });

    setupReader(1, 2);
    reader.doReadPage();
    reader.doReadPage();

    assertThat(reader.getPagesRead()).isEqualTo(1);
  }

  @Test
  void respectsExtraPageLimitForOlderRecords() throws Exception {
    when(connection.createStatement()).thenReturn(statement);
    AtomicInteger executeQueryCount = new AtomicInteger(0);

    // Simulate 2 pages, 1 result each
    when(statement.executeQuery(anyString())).thenAnswer(invocation -> {
      int callNum = executeQueryCount.getAndIncrement();
      if (callNum == 0) {
        // Phase 1: Previous day query - no results
        when(resultSet.next()).thenReturn(false);
        return resultSet;
      } else {
        // Phase 2: Older than previous day - simulate 3 pages
        when(resultSet.next())
            .thenReturn(true, false)
            .thenReturn(true, false)
            .thenReturn(true, false);
        return resultSet;
      }
    });

    setupReader(1, 2);

    reader.doReadPage();
    reader.doReadPage();
    reader.doReadPage();

    assertThat(reader.getPagesRead()).isEqualTo(2);
  }

  @Test
  void resetPagesRead_clearsCounter() throws Exception {
    when(connection.createStatement()).thenReturn(statement);
    when(resultSet.next()).thenReturn(true, false);
    when(statement.executeQuery(anyString())).thenReturn(resultSet);

    setupReader(1, 10);
    reader.doReadPage();
    reader.resetPagesRead();

    assertThat(reader).isNotNull();
  }

  @Test
  void throwsExceptionWhenSetupFailsInPhaseSwitch() throws Exception {
    when(connection.createStatement()).thenReturn(statement);
    when(statement.executeQuery(anyString())).thenReturn(resultSet);

    // First query - no data-
    when(resultSet.next()).thenReturn(false);

    setupReader(1, 2);

    UnprocessedPersonReader spyReader = Mockito.spy(reader);

    doThrow(new RuntimeException("Simulated failure"))
        .when(spyReader)
        .setupForOlderThanPreviousDay();

    assertThatThrownBy(spyReader::doReadPage)
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Simulated failure");
  }

}
