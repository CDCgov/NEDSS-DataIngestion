package gov.cdc.nbs.deduplication.batch.step;


import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;


@Component("previousDayReader")
public class UnprocessedPreviousDayPersonReader extends JdbcPagingItemReader<String> {

  public UnprocessedPreviousDayPersonReader(
      @Qualifier("deduplication") DataSource dataSource,
      @Value("${deduplication.batch.processing.chunk:100}") int chunkSize) throws Exception {

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid");
    provider.setFromClause("FROM nbs_mpi_mapping");
    provider.setWhereClause(buildWhereClause());
    provider.setSortKey("person_uid");

    setName("previousDayReader");
    setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      setQueryProvider(queryProvider);
    }
    setRowMapper((rs, rowNum) -> rs.getString("person_uid"));
    setPageSize(chunkSize);
  }

  private String buildWhereClause() {
    return String.format(
        "status = 'U' AND person_uid = person_parent_uid AND CAST(person_add_time AS DATE) = '%s'", getPreviousDay());
  }

  private String getPreviousDay() {
    LocalDate previousDay = LocalDate.now().minusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return previousDay.format(formatter);
  }
}

