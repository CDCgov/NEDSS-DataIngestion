package gov.cdc.nbs.deduplication.merge.handler;


import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonEthnicityMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  @Mock
  private PatientMergeRequest patientMergeRequest;

  private PersonEthnicityMergeHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PersonEthnicityMergeHandler(nbsTemplate);
    when(patientMergeRequest.survivingRecord()).thenReturn("survivorId");
    when(patientMergeRequest.ethnicitySource()).thenReturn("supersededId");
  }

  @Test
  void handleMerge_WhenEthnicityIndicatorsAreEqual_ShouldNotPerformMerge() {
    // Ethnicity indicators are equal
    mockFetchEthnicityIndicators("2135-3", "2135-3");

    handler.handleMerge("matchId", patientMergeRequest);

    verify(nbsTemplate, never()).update(any(String.class), any(MapSqlParameterSource.class));
  }

  @Test
  void handleMerge_WhenEthnicityIndicatorsAreDifferent_ShouldPerformMerge() {
    // Ethnicity indicators are different
    mockFetchEthnicityIndicators("2135-2", "2135-5");

    when(nbsTemplate.queryForObject(
        eq(PersonEthnicityMergeHandler.FETCH_SUPERSEDED_PERSON_ETHNICITY_IND),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("2135-5");

    handler.handleMerge("matchId", patientMergeRequest);

    verify(nbsTemplate).update(eq(PersonEthnicityMergeHandler.UPDATE_PERSON_ETHNICITY_IND),
        any(MapSqlParameterSource.class));
    verify(nbsTemplate).update(eq(PersonEthnicityMergeHandler.UPDATE_PRE_EXISTING_ENTRIES_TO_INACTIVE),
        any(MapSqlParameterSource.class));
    verify(nbsTemplate).update(eq(PersonEthnicityMergeHandler.COPY_SUPERSEDED_ETHNIC_GROUPS_TO_SURVIVING),
        any(MapSqlParameterSource.class));
  }

  private void mockFetchEthnicityIndicators(String survivorIndicator, String supersededIndicator) {
    Map<String, Object> indicatorResult = new HashMap<>();
    indicatorResult.put("survivorIndicator", survivorIndicator);
    indicatorResult.put("supersededIndicator", supersededIndicator);

    when(nbsTemplate.queryForMap(
        eq(PersonEthnicityMergeHandler.FETCH_ETHNICITY_INDICATORS_FOR_COMPARISON),
        any(MapSqlParameterSource.class)))
        .thenReturn(indicatorResult);
  }
}
