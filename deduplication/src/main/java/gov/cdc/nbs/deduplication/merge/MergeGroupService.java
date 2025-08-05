package gov.cdc.nbs.deduplication.merge;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;

@Component
public class MergeGroupService {
  private static final String MERGE_GROUP = "mergeGroup";

  private final JdbcClient jdbcClient;
  private final PatientRecordService patientRecordService;

  public MergeGroupService(
      @Qualifier("deduplicationJdbcClient") final JdbcClient jdbcClient,
      PatientRecordService patientRecordService) {
    this.jdbcClient = jdbcClient;
    this.patientRecordService = patientRecordService;
  }

  static final String SET_ENTRY_NO_MERGE = """
      UPDATE merge_group_entries
      SET
        is_merge = 0,
        last_chg_user_id = :userId,
        last_chg_time = GETDATE()
      WHERE
        merge_group = :mergeGroup
        AND person_uid = :personUid;
      """;

  static final String SET_GROUP_TO_NO_MERGE = """
      UPDATE merge_group_entries
      SET
        is_merge = 0,
        last_chg_user_id = :userId,
        last_chg_time = GETDATE()
      WHERE
        merge_group = :mergeGroup;
      """;

  static final String SELECT_PERSON_UIDS_FROM_MERGE_GROUP = """
      SELECT
        person_uid
      FROM
        merge_group_entries
      WHERE
        merge_group = :mergeGroup
        AND is_merge IS NULL;
      """;

  // Update the specified entry in the merge group to is_merge = 0
  public void markNoMerge(long mergeGroup, long personUid) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    jdbcClient.sql(SET_ENTRY_NO_MERGE)
        .param("userId", currentUser.getId())
        .param(MERGE_GROUP, mergeGroup)
        .param("personUid", personUid)
        .update();
  }

  // Update the all entries in the group to is_merge = 0
  public void markAllNoMerge(long mergeGroup) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    jdbcClient.sql(SET_GROUP_TO_NO_MERGE)
        .param("userId", currentUser.getId())
        .param(MERGE_GROUP, mergeGroup)
        .update();
  }

  public List<PersonMergeData> getMergeGroup(long mergeGroup) {
    List<String> nbsPersonIds = jdbcClient.sql(SELECT_PERSON_UIDS_FROM_MERGE_GROUP)
        .param(MERGE_GROUP, mergeGroup)
        .query(String.class)
        .list();

    return patientRecordService.fetchPersonsMergeData(nbsPersonIds);
  }

}
