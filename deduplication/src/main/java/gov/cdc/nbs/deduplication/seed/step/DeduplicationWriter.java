package gov.cdc.nbs.deduplication.seed.step;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;
import gov.cdc.nbs.deduplication.seed.service.FailedStatusUpdaterService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {

  private static final String QUERY = """
        INSERT INTO nbs_mpi_mapping
          (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
        VALUES
          (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
    """;

  private static final String UPDATE_LAST_PROCESSED_ID_QUERY = """
        UPDATE last_processed_id_table
        SET last_processed_id = :lastProcessedId;
    """;

  private final NamedParameterJdbcTemplate template;
  private final FailedStatusUpdaterService failedStatusUpdaterService;

  public DeduplicationWriter(
          @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate template,
          FailedStatusUpdaterService failedStatusUpdaterService
  ) {
    this.template = template;
    this.failedStatusUpdaterService = failedStatusUpdaterService;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    List<Long> failedPersonIds = new ArrayList<>();
    long lastProcessedId = -1;

    for (DeduplicationEntry entry : chunk) {
      try {
        batchParams.add(createParameterSource(entry));
        lastProcessedId = entry.nbsPersonId(); // Track the last processed ID
      } catch (Exception ex) {
        failedPersonIds.add(entry.nbsPersonId());
      }
    }

    // Insert records into the nbs_mpi_mapping table
    template.batchUpdate(QUERY, batchParams.toArray(new SqlParameterSource[0]));

    // Update the last processed ID
    updateLastProcessedId(lastProcessedId);

    // Update failed statuses using the service
    if (!failedPersonIds.isEmpty()) {
      failedStatusUpdaterService.updateFailedStatus(failedPersonIds);
    }
  }

  private void updateLastProcessedId(long lastProcessedId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("lastProcessedId", lastProcessedId);

    String query = """
            IF EXISTS (SELECT 1 FROM last_processed_id WHERE id = 1)
            BEGIN
                UPDATE last_processed_id
                SET last_processed_id = :lastProcessedId
                WHERE id = 1;
            END
            ELSE
            BEGIN
                INSERT INTO last_processed_id (id, last_processed_id)
                VALUES (1, :lastProcessedId);
            END
        """;
    template.update(query, params);
  }

  private SqlParameterSource createParameterSource(DeduplicationEntry entry) {
    return new MapSqlParameterSource()
            .addValue("person_uid", entry.nbsPersonId())
            .addValue("person_parent_uid", entry.nbsPersonParentId())
            .addValue("mpi_patient", entry.mpiPatientId())
            .addValue("mpi_person", entry.mpiPersonId())
            .addValue("status", "P");
  }
}
