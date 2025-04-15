package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

import java.util.Collections;

@Component
public class PersonReader extends JdbcPagingItemReader<NbsPerson> {

  private final NbsPersonMapper mapper = new NbsPersonMapper();
  private final NamedParameterJdbcTemplate mpiNamedJdbcTemplate;
  private final DataSource dataSource;

  private static final String LAST_SEEDED_PERSON_QUERY = "SELECT MAX(external_person_id) FROM mpi_patient";

  public PersonReader(
      @Qualifier("nbs") DataSource dataSource,
      @Qualifier("mpiNamedTemplate") NamedParameterJdbcTemplate mpiNamedJdbcTemplate
  ) throws Exception {

    this.mpiNamedJdbcTemplate = mpiNamedJdbcTemplate;
    String whereClause = buildWhereClause(null);
    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    this.dataSource = dataSource;
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid, person_parent_uid");
    provider.setFromClause("FROM person");
    provider.setWhereClause(whereClause);
    provider.setSortKey("person_uid");
    this.setName("nbsPersonReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
    this.setPageSize(1000);
  }

  @BeforeStep
  public void beforeStep() throws Exception {
    // Dynamically update the query provider
    Long lastProcessedId = getLastSeededPersonId();
    String whereClause = buildWhereClause(lastProcessedId);
    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid, person_parent_uid");
    provider.setFromClause("FROM person");
    provider.setWhereClause(whereClause);
    provider.setSortKey("person_uid");

    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    // Reset the reader's state
    this.afterPropertiesSet();
    this.setRowMapper(mapper);
    this.setPageSize(1000);
  }

  private String buildWhereClause(Long lastProcessedId) {
    String baseClause = "WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT'";
    if (lastProcessedId != null && lastProcessedId > 0) {
      return baseClause + " AND person_uid > " + lastProcessedId;
    }
    return baseClause;
  }

  private Long getLastSeededPersonId() {
    try {
      return mpiNamedJdbcTemplate.queryForObject(LAST_SEEDED_PERSON_QUERY,
          Collections.emptyMap(), Long.class);
    } catch (EmptyResultDataAccessException e) {
      return null; // If table is empty, return null
    }
  }
}
