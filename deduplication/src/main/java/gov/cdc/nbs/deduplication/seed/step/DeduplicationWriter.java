package gov.cdc.nbs.deduplication.seed.step;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {

  private static final String QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
      """;

  private final NamedParameterJdbcTemplate template;

  public DeduplicationWriter(@Qualifier("deduplicationTemplate") final JdbcTemplate template) {
    this.template = new NamedParameterJdbcTemplate(template);
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) throws Exception {
    chunk.forEach(c -> {
      SqlParameterSource parameters = new MapSqlParameterSource()
          .addValue("person_uid", c.nbsPersonId())
          .addValue("person_parent_uid", c.nbsPersonParentId())
          .addValue("mpi_patient", c.mpiPatientId())
          .addValue("mpi_person", c.mpiPersonId())
          .addValue("status", "U");
      template.update(QUERY, parameters);
    });
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