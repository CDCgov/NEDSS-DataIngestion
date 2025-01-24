package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

@Component
public class PersonReader extends JdbcPagingItemReader<NbsPerson> {

  private final NbsPersonMapper mapper = new NbsPersonMapper();

  public PersonReader(
          @Qualifier("nbs") DataSource dataSource,
          @Value("${lastProcessedId:0}") Long lastProcessedId) throws Exception {

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid, person_parent_uid");
    provider.setFromClause("FROM person");

    // Dynamically create the WHERE clause
    String whereClause = "WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT'";

    if (lastProcessedId != null && lastProcessedId > 0) {
      whereClause += " AND person_uid > " + lastProcessedId;
    }

    provider.setWhereClause(whereClause);
    provider.setSortKey("person_uid");

    this.setName("nbsPersonReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
    this.setPageSize(10000);
  }
}