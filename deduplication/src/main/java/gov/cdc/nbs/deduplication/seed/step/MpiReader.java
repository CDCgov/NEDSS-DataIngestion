package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import gov.cdc.nbs.deduplication.seed.mapper.DeduplicationEntryMapper;
import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

@Component
public class MpiReader extends JdbcPagingItemReader<DeduplicationEntry> {

  private static final String SELECT = """
      SELECT
        patient.external_patient_id person_uid,
        patient.external_person_id person_parent_uid,
        patient.reference_id mpi_patient_uuid,
        person.reference_id mpi_person_uuid
  """;

  private static final String FROM = """
      FROM
        mpi_patient patient
      JOIN mpi_person person ON patient.person_id = person.id
  """;

  private final DeduplicationEntryMapper mapper = new DeduplicationEntryMapper();
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public MpiReader(
          @Qualifier("mpi") DataSource dataSource,
          @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {

    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause(SELECT);
    provider.setFromClause(FROM);

    // Dynamically set the WHERE clause based on the lastProcessedId
    Integer lastProcessedId = getLastProcessedId();
    if (lastProcessedId != null) {
      provider.setWhereClause("WHERE patient.external_patient_id > :lastProcessedId");
    } else {
      // First run logic (if no lastProcessedId exists)
      provider.setWhereClause("");
    }

    provider.setSortKey("person_uid");

    this.setName("mpiIdReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
    this.setPageSize(1000);
  }

  private Integer getLastProcessedId() {
    return namedParameterJdbcTemplate.queryForObject(
            "SELECT last_processed_id FROM last_processed ORDER BY id DESC LIMIT 1",
            new MapSqlParameterSource(),
            Integer.class
    );
  }
}
