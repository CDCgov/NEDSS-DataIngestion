package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import gov.cdc.nbs.deduplication.seed.logger.LoggingService;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
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

  final LoggingService loggingService;

  public MpiReader(@Qualifier("mpi") DataSource dataSource, final LoggingService loggingService) throws Exception {
    this.loggingService = loggingService;
    try {
      SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
      provider.setDataSource(dataSource);
      provider.setSelectClause(SELECT);
      provider.setFromClause(FROM);
      provider.setSortKey("person_uid");

      this.setName("mpiIdReader");
      this.setDataSource(dataSource);
      PagingQueryProvider queryProvider = provider.getObject();
      if (queryProvider != null) {
        this.setQueryProvider(queryProvider);
      }
      this.setRowMapper(new DeduplicationEntryMapper());
      this.setPageSize(1000);
    } catch (Exception e) {
      loggingService.logError("MpiReader", "Error during batch reading of MPI patients.", e);
      throw e;
    }
  }
}
