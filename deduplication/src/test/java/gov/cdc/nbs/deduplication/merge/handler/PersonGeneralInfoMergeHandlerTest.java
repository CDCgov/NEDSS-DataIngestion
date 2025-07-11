package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonGeneralInfoMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonGeneralInfoMergeHandler handler;

  @Mock
  private PatientMergeRequest mockRequest;

  private static final String SURVIVOR_ID = "survivorId";
  private static final String SOURCE_ID = "supersededId";

  private PatientMergeRequest.GeneralInfoFieldSource fieldSourceWithDiffIds;
  private PatientMergeRequest.GeneralInfoFieldSource fieldSourceWithSameIds;

  @BeforeEach
  void setUp() {
    handler = new PersonGeneralInfoMergeHandler(nbsTemplate);

    fieldSourceWithDiffIds = new PatientMergeRequest.GeneralInfoFieldSource(SOURCE_ID, SOURCE_ID,
        SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID);

    fieldSourceWithSameIds = new PatientMergeRequest.GeneralInfoFieldSource(SURVIVOR_ID, SURVIVOR_ID,
        SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID);
  }

  @Test
  void handleMerge_ShouldUpdatePersonGeneralInfoFields() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.generalInfo()).thenReturn(fieldSourceWithDiffIds);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, times(10)).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  void handleMerge_NotCalledWhenSourceSameAsSurvivor() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.generalInfo()).thenReturn(fieldSourceWithSameIds);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, never()).update(anyString(), any(MapSqlParameterSource.class));
  }

}
