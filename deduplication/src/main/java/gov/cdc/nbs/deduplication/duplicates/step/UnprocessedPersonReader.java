package gov.cdc.nbs.deduplication.duplicates.step;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UnprocessedPersonReader extends JdbcPagingItemReader<String> {

  public UnprocessedPersonReader(@Qualifier("deduplication") DataSource dataSource) throws Exception {
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
    this.setPageSize(1000);
  }
}
