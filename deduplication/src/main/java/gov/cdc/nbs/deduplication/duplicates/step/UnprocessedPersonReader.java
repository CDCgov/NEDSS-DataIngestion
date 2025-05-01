package gov.cdc.nbs.deduplication.duplicates.step;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import lombok.Setter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class UnprocessedPersonReader extends JdbcPagingItemReader<String> {


  private enum ProcessingPhase {
    PREVIOUS_DAY,
    OLDER_THAN_PREVIOUS_DAY
  }

  private ProcessingPhase currentPhase = ProcessingPhase.PREVIOUS_DAY;
  private int extraPagesRead = 0;
  private final int extraPageLimit;

  private final LocalDate today = LocalDate.now();

  @Setter
  private LocalDate previousDay = today.minusDays(1);
  private final DataSource dataSource;
  private static final String PERSON_UID = "person_uid";

  public UnprocessedPersonReader(
      @Qualifier("deduplication") DataSource dataSource,
      @Value("${deduplication.batch.processing.chunk:100}") int chunkSize,
      @Value("${deduplication.batch.processing.total:10000}") int totalToProcess) throws Exception {

    this.dataSource = dataSource;
    this.extraPageLimit = (totalToProcess / chunkSize);

    setupForPreviousDay();

    this.setName("unprocessedPersonUidReader");
    this.setDataSource(dataSource);
    this.setRowMapper((rs, rowNum) -> rs.getString(PERSON_UID));
    this.setPageSize(chunkSize);
  }



  @Override
  protected void doReadPage() {
    if (currentPhase == ProcessingPhase.PREVIOUS_DAY) {
      super.doReadPage();

      // Switch phase if no more previous-day records
      if (CollectionUtils.isEmpty(results)) {
        try {
          setupForOlderThanPreviousDay();
          currentPhase = ProcessingPhase.OLDER_THAN_PREVIOUS_DAY;
          this.afterPropertiesSet();
        } catch (Exception e) {
          throw new RuntimeException(e);//NOSONAR
        }

        clearResults();
        super.doReadPage(); // read first older-than-previous-day record
      }
      return;
    }

    if (currentPhase == ProcessingPhase.OLDER_THAN_PREVIOUS_DAY) {
      if (extraPagesRead >= extraPageLimit) {
        clearResults();
        return;
      }

      super.doReadPage();

      if (!CollectionUtils.isEmpty(results)) {
        extraPagesRead++;

        if (extraPagesRead >= extraPageLimit) {
          clearResults();
        }
      }
    }
  }

  private void setupForPreviousDay() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String previousDayStr = previousDay.format(formatter);

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid");
    provider.setFromClause("FROM nbs_mpi_mapping");
    provider.setWhereClause(String.format(
        "status = 'U' AND person_uid = person_parent_uid AND CAST(person_add_time AS DATE) = '%s'", previousDayStr));
    provider.setSortKey(PERSON_UID);

    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      setQueryProvider(queryProvider);
    } else {
      throw new IllegalStateException("Failed to create PagingQueryProvider for previous day.");
    }
  }

  void setupForOlderThanPreviousDay() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String previousDayStr = previousDay.format(formatter);

    this.extraPagesRead = 0;

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid");
    provider.setFromClause("FROM nbs_mpi_mapping");
    provider.setWhereClause(String.format(
        "status = 'U' AND person_uid = person_parent_uid AND CAST(person_add_time AS DATE) < '%s'", previousDayStr));
    provider.setSortKey(PERSON_UID);

    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      setQueryProvider(queryProvider);
    } else {
      throw new IllegalStateException("Failed to create PagingQueryProvider for older than previous day.");
    }
  }

  private void clearResults() {
    if (results != null) {
      results.clear();
    }
  }

  public void resetPagesRead() {
    this.extraPagesRead = 0;
  }

  public int getPagesRead() {
    return this.extraPagesRead;
  }
}
