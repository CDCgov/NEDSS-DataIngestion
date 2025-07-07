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
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonBirthAndSexMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonBirthAndSexMergeHandler handler;

  @Mock
  private PatientMergeRequest mockRequest;

  private static final String SURVIVOR_ID = "survivorId";
  private static final String SOURCE_ID = "supersededId";

  private PatientMergeRequest.SexAndBirthFieldSource fieldSourceWithDiffIds;
  private PatientMergeRequest.SexAndBirthFieldSource fieldSourceWithSameIds;

  @BeforeEach
  void setUp() {
    handler = new PersonBirthAndSexMergeHandler(nbsTemplate);

    fieldSourceWithDiffIds = new PatientMergeRequest.SexAndBirthFieldSource(
        SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID, SOURCE_ID,
        SOURCE_ID, SOURCE_ID, SOURCE_ID);

    fieldSourceWithSameIds = new PatientMergeRequest.SexAndBirthFieldSource(
        SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID,
        SURVIVOR_ID, SURVIVOR_ID, SURVIVOR_ID);
  }

  // @Test -- TODO incorrect handling of fields
  // void handleMerge_ShouldUpdatePersonBirthAndSexFields() {
  // when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
  // when(mockRequest.sexAndBirth()).thenReturn(fieldSourceWithDiffIds);

  // handler.handleMerge("matchId", mockRequest);

  // verify(nbsTemplate, times(10)).update(anyString(),
  // any(MapSqlParameterSource.class));
  // }

  @Test
  void handleMerge_NotCalledWhenSourceSameAsSurvivor() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.sexAndBirth()).thenReturn(fieldSourceWithSameIds);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, never()).update(anyString(), any(MapSqlParameterSource.class));
  }

}
