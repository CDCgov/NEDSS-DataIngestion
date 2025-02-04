package gov.cdc.nbs.deduplication.seed.step;

import gov.cdc.nbs.deduplication.seed.logger.LoggingService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {

  private static final String QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
      """;

  private final NamedParameterJdbcTemplate template;
  private final LoggingService loggingService;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template,
      final LoggingService loggingService) {
    this.template = template;
    this.loggingService = loggingService;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) {
    try {
      List<SqlParameterSource> batchParams = new ArrayList<>();
      for (DeduplicationEntry entry : chunk) {
        batchParams.add(createParameterSource(entry));
      }
      template.batchUpdate(QUERY, batchParams.toArray(new SqlParameterSource[0]));
    } catch (Exception e) {
      String nbsPersonIdsStr = chunk.getItems().stream()
          .map(DeduplicationEntry::nbsPersonId)
          .map(String::valueOf)
          .collect(Collectors.joining(","));
      loggingService.logError("DeduplicationWriter", "Error writing nbs_mpi mapping to the database.",
          String.join(",", nbsPersonIdsStr), e);
      throw e;
    }
  }

  SqlParameterSource createParameterSource(DeduplicationEntry entry) {
    return new MapSqlParameterSource()
        .addValue("person_uid", entry.nbsPersonId())
        .addValue("person_parent_uid", entry.nbsPersonParentId())
        .addValue("mpi_patient", entry.mpiPatientId())
        .addValue("mpi_person", entry.mpiPersonId())
        .addValue("status", "U");
  }

}
