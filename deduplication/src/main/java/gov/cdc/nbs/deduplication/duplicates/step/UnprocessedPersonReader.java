package gov.cdc.nbs.deduplication.duplicates.step;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UnprocessedPersonReader extends JdbcPagingItemReader<String> {

  private int pagesRead = 0;
  private final int pageLimit;

  public UnprocessedPersonReader(
      @Qualifier("deduplication") DataSource dataSource,
      @Value("${deduplication.batch.processing.chunk:100}") int chunkSize,
      @Value("${deduplication.batch.processing.total:10000}") int totalToProcess) throws Exception {
    this.pageLimit = totalToProcess / chunkSize;

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid");
    provider.setFromClause("FROM nbs_mpi_mapping");
    provider.setWhereClause("WHERE status = 'U' AND person_uid=person_parent_uid");
    provider.setSortKey("person_uid");

    this.setName("unprocessedPersonUidReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper((rs, rowNum) -> rs.getString("person_uid"));
    this.setPageSize(chunkSize);
  }

  @Override
  protected void doReadPage() {
    // Limit number of pages read per job execution
    if (pagesRead >= pageLimit) {
      if (results == null) {
        results = new CopyOnWriteArrayList<>();
      } else {
        results.clear();
      }
      return;
    }
    pagesRead++;
    super.doReadPage();
  }

  public void resetPagesRead() {
    this.pagesRead = 0;
  }
}
