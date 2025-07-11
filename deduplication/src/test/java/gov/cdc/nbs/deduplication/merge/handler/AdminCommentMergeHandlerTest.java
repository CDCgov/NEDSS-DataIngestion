package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminCommentMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private AdminCommentMergeHandler handler;


  @BeforeEach
  void setUp() {
    handler = new AdminCommentMergeHandler(nbsTemplate);
  }

  @Test
  void handleMerge_shouldUpdateAdministrativeCommentsWithSurvivorAndSourceIds() {
    String matchId = "123";
    PatientMergeRequest request = getPatientMergeRequest();

    handler.handleMerge(matchId, request, new PatientMergeAudit());

    verifyUpdateAdministrativeComments();
  }

  private PatientMergeRequest getPatientMergeRequest() {
    return new PatientMergeRequest("survivorId1", "adminCommentSourcePersonId",
        null, null, null, null, null, null,
        null, null, null);
  }

  private void verifyUpdateAdministrativeComments() {
    verify(nbsTemplate).update(eq(QueryConstants.UPDATE_PERSON_ADMIN_COMMENT_FROM_SOURCE),
        any(MapSqlParameterSource.class));
  }

}
