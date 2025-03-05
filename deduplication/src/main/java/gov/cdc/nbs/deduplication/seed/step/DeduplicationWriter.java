package gov.cdc.nbs.deduplication.seed.step;

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

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {

  private static final String QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
      """;

  public static final String UPDATE_LAST_PROCESSED_ID = """
      UPDATE last_processed_id
      SET last_processed_id = :lastProcessedId
      WHERE id = 1
      """;

  private final NamedParameterJdbcTemplate template;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    Long largestProcessedId = null;

    for (DeduplicationEntry entry : chunk.getItems()) {
      batchParams.add(createParameterSource(entry));
      if (largestProcessedId == null || entry.nbsPersonId() > largestProcessedId) {
        largestProcessedId = entry.nbsPersonId();
      }
    }

    template.batchUpdate(QUERY, batchParams.toArray(new SqlParameterSource[0]));

    if (largestProcessedId != null) {
      updateLastProcessedId(largestProcessedId);
    }
  }

  public void updateLastProcessedId(Long lastProcessedId) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("lastProcessedId", lastProcessedId);

    try {
      template.update(UPDATE_LAST_PROCESSED_ID, params);
    } catch (Exception e) {
      logger.error("Error updating last_processed_id: {}", e.getMessage());
    }
  }

  SqlParameterSource createParameterSource(DeduplicationEntry entry) {
    return new MapSqlParameterSource()
            .addValue("person_uid", entry.nbsPersonId())
            .addValue("person_parent_uid", entry.nbsPersonParentId())
            .addValue("mpi_patient", entry.mpiPatientId())
            .addValue("mpi_person", entry.mpiPersonId())
            .addValue("status", "P");
  }
}
