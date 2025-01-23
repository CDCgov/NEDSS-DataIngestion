package gov.cdc.nbs.deduplication.seed.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

public class DeduplicationEntryMapper implements RowMapper<DeduplicationEntry> {
  @Override
  @Nullable
  public DeduplicationEntry mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    return new DeduplicationEntry(
        rs.getLong("person_uid"),
        rs.getLong("person_parent_uid"),
        rs.getString("mpi_patient_uuid"),
        rs.getString("mpi_person_uuid"),
        rs.getString("status")
    );

  }
}
