package gov.cdc.nbs.deduplication.seed.step;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
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


  private final NamedParameterJdbcTemplate template;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    for (DeduplicationEntry entry : chunk) {
      batchParams.add(createParameterSource(entry));
    }
    template.batchUpdate(QueryConstants.NBS_MPI_QUERY, batchParams.toArray(new SqlParameterSource[0]));
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
