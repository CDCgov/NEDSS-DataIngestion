package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonRacesMergeHandlerTest {

  private PersonRacesMergeHandler handler;

  private static final String SURVIVOR_ID = "survivor-100";
  private static final String SUPERSEDED_ID = "superseded-200";
  private static final String SURVIVOR_RACE_CODE = "RACE_001";
  private static final String SUPERSEDED_RACE_CODE = "RACE_002";
  private static final String EXISTING_CATEGORY = "EXISTING_CATEGORY";
  private static final String NEW_CATEGORY = "NEW_CATEGORY";

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  @Captor
  private ArgumentCaptor<Map<String, Object>> paramMapCaptor;

  @Captor
  private ArgumentCaptor<MapSqlParameterSource> paramSourceCaptor;


  @BeforeEach
  void setUp() {
    handler = new PersonRacesMergeHandler(nbsTemplate);
  }

  @Test
  void shouldAddDetailedRace_whenSurvivorContainsTheCategory() {
    List<PatientMergeRequest.RaceId> races = Arrays.asList(
        new PatientMergeRequest.RaceId(SURVIVOR_ID, SURVIVOR_RACE_CODE),
        new PatientMergeRequest.RaceId(SUPERSEDED_ID, SUPERSEDED_RACE_CODE)
    );

    PatientMergeRequest request = getPatientMergeRequest(races);

    mockSurvivorRaceCategories(List.of(EXISTING_CATEGORY));
    mockRaceCategoryCd(EXISTING_CATEGORY);

    handler.handleMerge("match-123", request);

    verifyUnselectedSurvivingRacesMarkedInactive();
    verifyNewDetailedRaceInserted();
    verify(nbsTemplate, never()).update(eq(PersonRacesMergeHandler.COPY_RACE_FROM_SUPERSEDED_TO_SURVIVOR), anyMap());
  }

  @Test
  void shouldMoveRace_whenSurvivorDoesNotContainTheCategory() {
    List<PatientMergeRequest.RaceId> races = Arrays.asList(
        new PatientMergeRequest.RaceId(SURVIVOR_ID, SURVIVOR_RACE_CODE),
        new PatientMergeRequest.RaceId(SUPERSEDED_ID, SUPERSEDED_RACE_CODE)
    );

    PatientMergeRequest request = getPatientMergeRequest(races);

    mockSurvivorRaceCategories(Collections.singletonList(EXISTING_CATEGORY));
    mockRaceCategoryCd(NEW_CATEGORY);

    handler.handleMerge("match-123", request);

    verifyUnselectedSurvivingRacesMarkedInactive();
    verifyNewRaceCategoryCopiedToSurvivor();
    verify(nbsTemplate, never()).update(eq(PersonRacesMergeHandler.COPY_RACE_DETAIL_IF_NOT_EXISTS), anyMap());
  }

  private void mockSurvivorRaceCategories(List<String> categories) {
    when(nbsTemplate.queryForList(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
        .thenReturn(categories);
  }

  //Mock that a race_cd maps to the given category
  private void mockRaceCategoryCd(String categoryCd) {
    when(nbsTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
        .thenReturn(categoryCd);
  }

  private void verifyUnselectedSurvivingRacesMarkedInactive() {
    verify(nbsTemplate).update(eq(PersonRacesMergeHandler.UPDATE_SELECTED_EXCLUDED_RACES_INACTIVE),
        paramMapCaptor.capture());
  }

  private void verifyNewDetailedRaceInserted() {
    verify(nbsTemplate).update(eq(PersonRacesMergeHandler.COPY_RACE_DETAIL_IF_NOT_EXISTS),
        paramSourceCaptor.capture());
  }

  private void verifyNewRaceCategoryCopiedToSurvivor() {
    verify(nbsTemplate).update(eq(PersonRacesMergeHandler.COPY_RACE_FROM_SUPERSEDED_TO_SURVIVOR),
        paramSourceCaptor.capture());

    verify(nbsTemplate).update(eq(PersonRacesMergeHandler.COPY_RACE_DETAIL_FROM_SUPERSEDED_TO_SURVIVOR),
        paramSourceCaptor.capture());
  }

  private PatientMergeRequest getPatientMergeRequest(List<PatientMergeRequest.RaceId> races) {
    return new PatientMergeRequest(SURVIVOR_ID, null, null, null, null, null, races, null, null, null, null);
  }

}
