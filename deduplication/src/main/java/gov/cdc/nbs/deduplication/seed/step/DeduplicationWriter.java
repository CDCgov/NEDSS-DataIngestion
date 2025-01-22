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

  private static final String INSERT_QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status, processed_at)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status, CURRENT_TIMESTAMP);
      """;

  private static final String UPDATE_QUERY = """
      UPDATE nbs_mpi_mapping
      SET 
        mpi_patient = :mpi_patient,
        mpi_person = :mpi_person,
        status = :status,
        processed_at = CURRENT_TIMESTAMP
      WHERE 
        person_uid = :person_uid;
      """;

  private final NamedParameterJdbcTemplate template;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    List<SqlParameterSource> updateParams = new ArrayList<>();

    for (DeduplicationEntry entry : chunk) {
      if (isExistingRecord(entry.nbsPersonId())) {
        updateParams.add(createParameterSource(entry));
      } else {
        batchParams.add(createParameterSource(entry));
      }
    }

    // Batch insert new records
    if (!batchParams.isEmpty()) {
      template.batchUpdate(INSERT_QUERY, batchParams.toArray(new SqlParameterSource[0]));
    }

    // Batch update existing records
    if (!updateParams.isEmpty()) {
      template.batchUpdate(UPDATE_QUERY, updateParams.toArray(new SqlParameterSource[0]));
    }
  }

  private boolean isExistingRecord(Long personUid) {
    String checkQuery = "SELECT COUNT(*) FROM nbs_mpi_mapping WHERE person_uid = :person_uid";
    Integer count = template.queryForObject(
            checkQuery,
            new MapSqlParameterSource("person_uid", personUid),
            Integer.class
    );
    return count != null && count > 0;
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
