package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonTableMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  private PersonTableMergeHandler handler;

  @Mock
  private PatientMergeAudit patientMergeAudit;

  @BeforeEach
  void setUp() {
    handler = new PersonTableMergeHandler(nbsTemplate, deduplicationTemplate);
  }

  @Test
  void handleMerge_shouldPerformAllPersonRelatedDatabaseOperations() {
    String matchId = "123";
    PatientMergeRequest request = getPatientMergeRequest();

    mockFetchSupersededCandidatesToReturn("superseded1", "superseded2", "superseded3");
    mockFetchChildIdsOfSupersededToReturn("supersededChild1", "supersededChild2", "supersededChild3");

    handler.handleMerge(matchId, request, patientMergeAudit);

    verifyCopyPersonToHistory();
    verifyIncrementPersonVersionNumber();
    verifyLinkSupersededChildIdsToSurvivor();
    verifyMarkSupersededRecordsAsSuperseded();
    verifyUpdateLastChangeTimeForPatients();
    verifyInsertPersonMergeRecord();
  }

  private void mockFetchSupersededCandidatesToReturn(String... ids) {
    when(deduplicationTemplate.queryForList(
        eq(QueryConstants.FETCH_SUPERSEDED_CANDIDATES),
        any(MapSqlParameterSource.class),
        eq(String.class))).thenReturn(Arrays.asList(ids));
  }

  private void mockFetchChildIdsOfSupersededToReturn(String... ids) {
    when(nbsTemplate.queryForList(
        eq(QueryConstants.CHILD_IDS_BY_PARENT_PERSON_IDS),
        any(MapSqlParameterSource.class),
        eq(String.class))).thenReturn(Arrays.asList(ids));
  }

  private void verifyCopyPersonToHistory() {
    verify(nbsTemplate).update(eq(QueryConstants.COPY_PERSON_TO_HISTORY), any(MapSqlParameterSource.class));
  }

  private void verifyIncrementPersonVersionNumber() {
    verify(nbsTemplate).update(eq(QueryConstants.INCREMENT_PERSON_VERSION_NUMBER), any(MapSqlParameterSource.class));
  }

  private void verifyLinkSupersededChildIdsToSurvivor() {
    verify(nbsTemplate).update(eq(QueryConstants.LINK_SUPERSEDED_CHILD_IDS_TO_SURVIVOR),
        any(MapSqlParameterSource.class));
  }

  private void verifyMarkSupersededRecordsAsSuperseded() {
    verify(nbsTemplate).update(eq(QueryConstants.MARK_SUPERSEDED_RECORDS), any(MapSqlParameterSource.class));
  }

  private void verifyUpdateLastChangeTimeForPatients() {
    verify(nbsTemplate).update(eq(QueryConstants.UPDATE_LAST_CHANGE_TIME_FOR_PATIENTS),
        any(MapSqlParameterSource.class));
  }

  private void verifyInsertPersonMergeRecord() {
    verify(nbsTemplate).batchUpdate(eq(QueryConstants.INSERT_PERSON_MERGE_RECORD), any(MapSqlParameterSource[].class));
  }

  private PatientMergeRequest getPatientMergeRequest() {
    return new PatientMergeRequest("survivorId1", null, null, null, null, null,
        null, null, null, null, null);
  }

}
