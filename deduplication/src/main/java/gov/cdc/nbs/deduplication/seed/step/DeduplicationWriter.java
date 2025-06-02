package gov.cdc.nbs.deduplication.seed.step;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DeduplicationWriter implements ItemWriter<DeduplicationEntry> {


  private final NamedParameterJdbcTemplate template;
  private final PatientRecordService patientRecordService;

  public DeduplicationWriter(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template,
      final PatientRecordService patientRecordService) {
    this.template = template;
    this.patientRecordService = patientRecordService;
  }

  @Override
  public void write(@NonNull Chunk<? extends DeduplicationEntry> chunk) {
    List<SqlParameterSource> batchParams = new ArrayList<>();
    Map<String, LocalDateTime> patientNameAndTimeMap = patientRecordService.fetchPersonAddTimeMap(
        chunk.getItems().stream().map(i -> i.nbsPersonId().toString()).toList());

    for (DeduplicationEntry entry : chunk) {
      batchParams.add(
          createParameterSource(entry, patientNameAndTimeMap.get(entry.nbsPersonId().toString())));
    }
    template.batchUpdate(QueryConstants.NBS_MPI_QUERY, batchParams.toArray(new SqlParameterSource[0]));
  }

  SqlParameterSource createParameterSource(DeduplicationEntry entry, LocalDateTime personAddTime) {
    return new MapSqlParameterSource()
        .addValue("person_uid", entry.nbsPersonId())
        .addValue("person_parent_uid", entry.nbsPersonParentId())
        .addValue("mpi_patient", entry.mpiPatientId())
        .addValue("mpi_person", entry.mpiPersonId())
        .addValue("status", "U")
        .addValue("person_add_time", personAddTime);
  }


}
