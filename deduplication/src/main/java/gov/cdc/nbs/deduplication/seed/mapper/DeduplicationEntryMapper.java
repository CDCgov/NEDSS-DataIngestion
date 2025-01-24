package gov.cdc.nbs.deduplication.seed.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

public class DeduplicationEntryMapper implements RowMapper<DeduplicationEntry> {
  @Override
  @Nullable
  public DeduplicationEntry mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    Timestamp timestamp = rs.getTimestamp("processed_at");
    LocalDateTime processedAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;

    return new DeduplicationEntry(
        rs.getLong("person_uid"),
        rs.getLong("person_parent_uid"),
        rs.getString("mpi_patient_uuid"),
        rs.getString("mpi_person_uuid"),
        rs.getString("status"),
        processedAt
    );

  }
}
