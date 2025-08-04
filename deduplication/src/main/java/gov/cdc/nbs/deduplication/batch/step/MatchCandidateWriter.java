package gov.cdc.nbs.deduplication.batch.step;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.batch.step.exception.MergeGroupInsertException;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;

@Component
public class MatchCandidateWriter implements ItemWriter<MatchCandidate> {
  private static final String PERSON_UID = "personUid";
  private static final String MERGE_GROUP = "mergeGroup";

  private final JdbcClient jdbcClient;
  private final PatientRecordService patientRecordService;

  public MatchCandidateWriter(
      @Qualifier("deduplicationJdbcClient") final JdbcClient jdbcClient,
      final PatientRecordService patientRecordService) {
    this.jdbcClient = jdbcClient;
    this.patientRecordService = patientRecordService;
  }

  static final String FIND_MATCH_GROUP_CONTAINING_TARGET = """
      SELECT
        TOP 1 group_id
      FROM
        merge_group_entries
      WHERE
        person_uid = :personUid
        AND is_merge IS NULL
      """;

  static final String DOES_GROUP_INCLUDES_PERSON_UID = """
      SELECT CASE WHEN EXISTS (
            SELECT 1
            FROM merge_group_entries
            WHERE person_uid = :personUid
            AND group_id = :mergeGroup
        ) THEN 1 ELSE 0 END
      """;

  static final String INSERT_MATCH_GROUP = """
      INSERT INTO
        merge_group (add_time)
      VALUES
        (getDate());
      """;

  static final String INSERT_MATCH_GROUP_ENTRY = """
      INSERT INTO merge_group_entries (
        group_id,
        person_uid
      )
      VALUES (
        :mergeGroup,
        :personUid
      );
      """;

  static final String INSERT_MATCH_REQUIRING_REVIEW = """
      INSERT INTO
        matches_requiring_review (
        person_uid,
        person_local_id,
        person_name,
        person_add_time,
        matched_person_uid,
        date_identified,
        merge_group)
      VALUES
        (
        :personUid,
        :personLocalId,
        :personName,
        :personAddTime,
        :matchedPersonUid,
        :identifiedDate,
        :mergeGroup
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

      // Add to, or create a new merge group
      long groupId = ensureMergeGroup(candidate.personUid(), matchedPersonUid);

      // Insert into matches_requiring_review
      insertMatch(candidate.personUid(), matchedPersonUid, groupId);
    }

    // return person_uid that was processed so status can be updated
    return candidate.personUid();
  }

  private long ensureMergeGroup(String incomingPersonUid, String matchedPersonUid) {
    // Check if a group already exists with the matched person
    Optional<Long> existingGroup = jdbcClient.sql(FIND_MATCH_GROUP_CONTAINING_TARGET)
        .param(PERSON_UID, matchedPersonUid)
        .query(Long.class)
        .optional();

    if (existingGroup.isPresent()) {
      // If so, ensure the incoming personUid is included in that group
      long groupId = existingGroup.get();
      ensurePersonInGroup(groupId, incomingPersonUid);
      return groupId;
    } else {
      // If not, create a group and add both the incoming and matched person to it
      long groupId = createMergeGroup();
      ensurePersonInGroup(groupId, incomingPersonUid);
      ensurePersonInGroup(groupId, matchedPersonUid);
      return groupId;
    }

  }

  private long createMergeGroup() {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcClient.sql(INSERT_MATCH_GROUP)
        .update(keyHolder);

    Number groupId = keyHolder.getKey();
    if (groupId == null) {
      throw new MergeGroupInsertException();
    }
    return groupId.longValue();
  }

  private void ensurePersonInGroup(long mergeGroup, String personUid) {
    boolean incomingAlreadyExists = jdbcClient.sql(DOES_GROUP_INCLUDES_PERSON_UID)
        .param(PERSON_UID, personUid)
        .param(MERGE_GROUP, mergeGroup)
        .query(Boolean.class)
        .single();

    if (!incomingAlreadyExists) {
      jdbcClient.sql(INSERT_MATCH_GROUP_ENTRY)
          .param(MERGE_GROUP, mergeGroup)
          .param(PERSON_UID, personUid)
          .update();
    }
  }

  private void insertMatch(String incomingPersonId, String matchedPersonId, long mergeGroup) {
    // Get the incoming records name, add time, and local Id
    PatientNameAndTime patientData = patientRecordService.fetchPersonNameAndAddTime(incomingPersonId);

    // Insert into matches_requiring_review table
    jdbcClient.sql(INSERT_MATCH_REQUIRING_REVIEW)
        .param(PERSON_UID, incomingPersonId)
        .param("personLocalId", patientData.personLocalId())
        .param("personName", patientData.name())
        .param("personAddTime", patientData.addTime())
        .param("matchedPersonUid", matchedPersonId)
        .param("identifiedDate", getCurrentDate())
        .param(MERGE_GROUP, mergeGroup)
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
