package gov.cdc.nbs.deduplication.batch.step;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;

@Component
public class MatchCandidateWriter implements ItemWriter<MatchCandidate> {

  private final JdbcClient jdbcClient;
  private final PatientRecordService patientRecordService;

  public MatchCandidateWriter(
      @Qualifier("deduplicationJdbcClient") final JdbcClient jdbcClient,
      final PatientRecordService patientRecordService) {
    this.jdbcClient = jdbcClient;
    this.patientRecordService = patientRecordService;
  }

  static final String INSERT_MATCH_REQUIRING_REVIEW = """
      INSERT INTO
        matches_requiring_review (
        person_uid,
        person_local_id,
        person_name,
        person_add_time,
        matched_person_uid,
        date_identified)
      VALUES
        (
        :personUid,
        :personLocalId,
        :personName,
        :personAddTime,
        :matchedPersonUid,
        :identifiedDate
        );
      """;

  static final String SELECT_PERSON_UID_BY_MPI_ID = """
          SELECT TOP 1 person_uid
          FROM nbs_mpi_mapping
          WHERE mpi_person IN (:mpiId)
          AND person_uid = person_parent_uid
      """;

  static final String UPDATE_STATUS_TO_P = """
      UPDATE nbs_mpi_mapping
      SET status = 'P'
      WHERE person_uid IN (:personIds)
      """;

  @Override
  public void write(Chunk<? extends MatchCandidate> chunk) {
    List<String> processedPersonIds = chunk.getItems()
        .stream()
        .map(this::processMatchCandidate)
        .toList();

    updateStatus(processedPersonIds);
  }

  private String processMatchCandidate(MatchCandidate candidate) {
    // If the candidate has possible matches
    if (candidate.possibleMatchList() != null && !candidate.possibleMatchList().isEmpty()) {
      // Get the matched person's uid. Only supports single match
      String matchedPersonUid = getPersonIdByMpiIds(candidate.possibleMatchList().getFirst());

      // Insert into matches_requiring_review
      insertMatch(candidate.personUid(), matchedPersonUid);
    }

    // return person_uid that was processed so status can be updated
    return candidate.personUid();
  }

  private void insertMatch(String incomingPersonId, String matchedPersonId) {
    // Get the incoming records name, add time, and local Id
    PatientNameAndTime patientData = patientRecordService.fetchPersonNameAndAddTime(incomingPersonId);

    // Insert into matches_requiring_review table
    jdbcClient.sql(INSERT_MATCH_REQUIRING_REVIEW)
        .param("personUid", incomingPersonId)
        .param("personLocalId", patientData.personLocalId())
        .param("personName", patientData.name())
        .param("personAddTime", patientData.addTime())
        .param("matchedPersonUid", matchedPersonId)
        .param("identifiedDate", getCurrentDate())
        .update();
  }

  private String getPersonIdByMpiIds(String mpiId) {
    return jdbcClient.sql(SELECT_PERSON_UID_BY_MPI_ID)
        .param("mpiId", mpiId)
        .query(String.class)
        .single();
  }

  private void updateStatus(List<String> personIds) {
    if (!personIds.isEmpty()) {
      jdbcClient.sql(UPDATE_STATUS_TO_P)
          .param("personIds", personIds)
          .update();
    }
  }

  private String getCurrentDate() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

}
