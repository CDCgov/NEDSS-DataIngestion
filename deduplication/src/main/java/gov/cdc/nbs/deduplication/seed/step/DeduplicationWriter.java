package gov.cdc.nbs.deduplication.seed.step;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {

  private static final Logger logger = LoggerFactory.getLogger(DeduplicationWriter.class);

  private static final String QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
      """;

  private static final String UPDATE_LAST_PROCESSED_ID = """
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
    Long lastProcessedId = null;  // To track the last processed ID in this batch

    // Iterate through the chunk of items
    for (DeduplicationEntry entry : chunk.getItems()) {
      batchParams.add(createParameterSource(entry));
      // Track the last ID in the chunk (you could adjust this logic based on the clustering query result)
      lastProcessedId = entry.nbsPersonId();  // Assuming nbsPersonId() is the ID you're using for clustering
    }

    // Insert the batch into nbs_mpi_mapping
    template.batchUpdate(QUERY, batchParams.toArray(new SqlParameterSource[0]));

    // After processing the batch, update the lastProcessedId in the database
    if (lastProcessedId != null) {
      updateLastProcessedId(lastProcessedId);  // Use the last ID from the chunk
    }
  }


  private void updateLastProcessedId(Long lastProcessedId) {
    SqlParameterSource params = new MapSqlParameterSource()
            .addValue("lastProcessedId", lastProcessedId);

    try {
      // Assuming the last_processed_id table has one record (id = 1)
      template.update(UPDATE_LAST_PROCESSED_ID, params);
      logger.info("Successfully updated last_processed_id to: {}", lastProcessedId);
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
