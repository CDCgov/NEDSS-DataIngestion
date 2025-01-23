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

  public static final String INSERT_QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status, processed_at)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status, CURRENT_TIMESTAMP);
      """;

  public static final String UPDATE_QUERY = """
      UPDATE nbs_mpi_mapping
      SET 
        mpi_patient = :mpi_patient,
        mpi_person = :mpi_person,
        status = :status,
        processed_at = CURRENT_TIMESTAMP
      WHERE 
        person_uid = :person_uid;
      """;

  public static final String UPDATE_WATERMARK_QUERY = """
      UPDATE deduplication_watermark
      SET last_processed_id = :last_processed_id, updated_at = CURRENT_TIMESTAMP
      WHERE id = 1;
      """;

  private final NamedParameterJdbcTemplate template;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    List<SqlParameterSource> updateParams = new ArrayList<>();
    Long maxProcessedId = 0L; // To track the highest person_uid processed in this chunk
    System.out.println("Processing chunk with " + chunk.size() + " entries.");

    for (DeduplicationEntry entry : chunk) {
      System.out.println("Processing entry: " + entry);

      // Check if the record exists once per entry
      boolean exists = isExistingRecord(entry.nbsPersonId());
      String status = exists ? "S" : "U";
      System.out.println("Determined status: " + status);

      // Add to the correct batch list based on existence
      if (exists) {
        updateParams.add(createParameterSource(entry, status));
      } else {
        batchParams.add(createParameterSource(entry, status));
      }

      // Update the max processed ID
      maxProcessedId = Math.max(maxProcessedId, entry.nbsPersonId());
    }

    // Execute batch inserts if there are new records
    if (!batchParams.isEmpty()) {
      System.out.println("Executing batch insert...");
      template.batchUpdate(INSERT_QUERY, batchParams.toArray(new SqlParameterSource[0]));
    } else {
      System.out.println("No new records to insert.");
    }

    // Execute batch updates if there are existing records
    if (!updateParams.isEmpty()) {
      System.out.println("Executing batch update...");
      template.batchUpdate(UPDATE_QUERY, updateParams.toArray(new SqlParameterSource[0]));
    } else {
      System.out.println("No existing records to update.");
    }

    // Update the high-water mark if needed
    updateHighWaterMark(maxProcessedId);
  }


  private boolean isExistingRecord(Long personUid) {
    System.out.println("Checking if record exists for person_uid: " + personUid);

    String checkQuery = "SELECT COUNT(*) FROM nbs_mpi_mapping WHERE person_uid = :person_uid";
    System.out.println("Executing SQL: " + checkQuery);
    System.out.println("Parameters: " + personUid);
    Integer count = template.queryForObject(
            checkQuery,
            new MapSqlParameterSource("person_uid", personUid),
            Integer.class
    );
    if (count == null) {
      System.out.println("Error: null count received for person_uid: " + personUid);
    } else {
      System.out.println("Record count for person_uid " + personUid + ": " + count);
    }
    return count != null && count > 0;
  }

  private void updateHighWaterMark(Long lastProcessedId) {
    System.out.println("Executing high-water mark update with lastProcessedId: " + lastProcessedId);
    template.update(
            UPDATE_WATERMARK_QUERY,
            new MapSqlParameterSource("last_processed_id", lastProcessedId)
    );
  }

  SqlParameterSource createParameterSource(DeduplicationEntry entry, String status) {
    return new MapSqlParameterSource()
            .addValue("person_uid", entry.nbsPersonId())
            .addValue("person_parent_uid", entry.nbsPersonParentId())
            .addValue("mpi_patient", entry.mpiPatientId())
            .addValue("mpi_person", entry.mpiPersonId())
            .addValue("status", status);
  }
}
