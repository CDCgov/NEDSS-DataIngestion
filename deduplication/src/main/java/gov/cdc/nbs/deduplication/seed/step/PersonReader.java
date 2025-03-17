package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

import java.util.HashMap;

@Component
public class PersonReader extends JdbcPagingItemReader<NbsPerson> {

  private final NbsPersonMapper mapper = new NbsPersonMapper();
  private final PagingQueryProvider queryProvider;
  private final DataSource dataSource; // Store the DataSource
  private final NamedParameterJdbcTemplate nbsNamedJdbcTemplate;
  private final DeduplicationWriter deduplicationWriter;

  public PersonReader(
          @Qualifier("nbs") DataSource dataSource,
          @Value("${lastProcessedId:0}") Long lastProcessedId,
          @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsNamedJdbcTemplate,
          DeduplicationWriter deduplicationWriter) throws Exception {

    this.dataSource = dataSource; // Store the DataSource reference
    this.nbsNamedJdbcTemplate = nbsNamedJdbcTemplate;
    this.deduplicationWriter = deduplicationWriter;

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid, person_parent_uid");
    provider.setFromClause("FROM person");

    // Dynamically create the WHERE clause
    String whereClause = "WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT'";

    if (lastProcessedId != null && lastProcessedId > 0) {
      whereClause += " AND person_uid > " + lastProcessedId;
    } else {
      whereClause += " AND person_uid > 0";
    }

    provider.setWhereClause(whereClause);
    provider.setSortKey("person_uid");

    this.setName("nbsPersonReader");
    this.setDataSource(dataSource);
    this.setPageSize(10000);
    this.queryProvider = provider.getObject();

    if (this.queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
  }

  // Expose the Query Provider for testing
  public PagingQueryProvider getQueryProviderInstance() {
    return this.queryProvider;
  }

  // Expose the DataSource for testing
  public DataSource getDataSourceInstance() {
    return this.dataSource;
  }

  // Expose the RowMapper for testing
  public NbsPersonMapper getRowMapperInstance() {
    return this.mapper;
  }

  // Method to get the smallest person_id from the NBS database
  public Long getSmallestPersonId() {
    String smallestIdSql = "SELECT MIN(person_uid) FROM person";
    try {
      return nbsNamedJdbcTemplate.queryForObject(smallestIdSql, new HashMap<>(), Long.class);
    } catch (Exception e) {
      throw new IllegalStateException("Could not retrieve the smallest person ID from the nbs.person table.", e);
    }
  }

  // Method to get the largest processed person_id after the seeding job
  public Long getLargestProcessedId() {
    String largestIdSql = "SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId";
    HashMap<String, Object> params = new HashMap<>();
    params.put("lastProcessedId", deduplicationWriter.getLastProcessedId());
    try {
      return nbsNamedJdbcTemplate.queryForObject(largestIdSql, params, Long.class);
    } catch (Exception e) {
      return null; // Handle the case where the largest ID could not be fetched
    }
  }
}
