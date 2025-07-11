package gov.cdc.nbs.deduplication.merge.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.merge.id.GeneratedId;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator.EntityType;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@ExtendWith(MockitoExtension.class)
class PersonMortalityMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  @Mock
  private LocalUidGenerator idGenerator;

  private PersonMortalityMergeHandler handler;

  @Mock
  private PatientMergeRequest mockRequest;

  private static final String SURVIVOR_ID = "survivorId";
  private static final String SOURCE_ID = "supersededId";

  private PatientMergeRequest.MortalityFieldSource fieldSourceWithDiffIds;
  private PatientMergeRequest.MortalityFieldSource fieldSourceWithSameIds;

  @BeforeEach
  void setUp() {
    handler = new PersonMortalityMergeHandler(nbsTemplate, idGenerator);

    fieldSourceWithDiffIds = new PatientMergeRequest.MortalityFieldSource(
        SOURCE_ID,
        SOURCE_ID,
        SOURCE_ID,
        SOURCE_ID,
        SOURCE_ID,
        SOURCE_ID);

    fieldSourceWithSameIds = new PatientMergeRequest.MortalityFieldSource(
        SURVIVOR_ID,
        SURVIVOR_ID,
        SURVIVOR_ID,
        SURVIVOR_ID,
        SURVIVOR_ID,
        SURVIVOR_ID);
  }

  @Test
  void handleMerge_ShouldUpdatePersonMortalityFields() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithDiffIds);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, times(6)).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  void handleMerge_NotCalledWhenSourceSameAsSurvivor() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithSameIds);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, never()).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  void handleMerge_CreateLocator() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithDiffIds);
    when(nbsTemplate.queryForObject(
        eq(PersonMortalityMergeHandler.SHOULD_CREATE_POSTAL_LOCATOR),
        any(MapSqlParameterSource.class),
        eq(Boolean.class)))
        .thenReturn(true);

    when(idGenerator.getNextValidId(EntityType.NBS)).thenReturn(new GeneratedId(14L, "prefix", "suffix"));
    mockCurrentUser(1001L);

    handler.handleMerge("matchId", mockRequest);

    verify(nbsTemplate, times(12)).update(anyString(), any(MapSqlParameterSource.class));
  }

  private void mockCurrentUser(long userId) {
    NbsUserDetails user = Mockito.mock(NbsUserDetails.class);
    when(user.getId()).thenReturn(userId);

    Authentication auth = Mockito.mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(user);

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

}
