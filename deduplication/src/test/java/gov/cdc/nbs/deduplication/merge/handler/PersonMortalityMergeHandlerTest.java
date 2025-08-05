package gov.cdc.nbs.deduplication.merge.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.cdc.nbs.deduplication.SecurityTestUtil;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import gov.cdc.nbs.deduplication.merge.id.GeneratedId;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator.EntityType;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private PatientMergeAudit audit;

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

    audit = new PatientMergeAudit(new ArrayList<>());
  }

  @Test
  void handleMerge_ShouldUpdatePersonMortalityFields() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithDiffIds);

    handler.handleMerge("matchId", mockRequest, audit);

    verify(nbsTemplate, times(6)).update(anyString(), any(MapSqlParameterSource.class));
  }

  @Test
  void handleMerge_NotCalledWhenSourceSameAsSurvivor() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithSameIds);

    handler.handleMerge("matchId", mockRequest, audit);

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
    SecurityTestUtil.mockSecurityContext();

    handler.handleMerge("matchId", mockRequest, audit);

    verify(nbsTemplate, times(12)).update(anyString(), any(MapSqlParameterSource.class));
  }



  @Test
  void handleMerge_ShouldAddAuditUpdateActions() {
    when(mockRequest.survivingRecord()).thenReturn(SURVIVOR_ID);
    when(mockRequest.mortality()).thenReturn(fieldSourceWithDiffIds);

    when(nbsTemplate.queryForObject(
        eq(PersonMortalityMergeHandler.SHOULD_CREATE_POSTAL_LOCATOR),
        any(MapSqlParameterSource.class),
        eq(Boolean.class)))
        .thenReturn(true);

    when(idGenerator.getNextValidId(EntityType.NBS))
        .thenReturn(new GeneratedId(14L, "prefix", "suffix"));

    SecurityTestUtil.mockSecurityContext();

    // Mock audit query results
    Map<String, Object> cityRow = new HashMap<>();
    cityRow.put("postal_locator_uid", 100L);
    cityRow.put("city_desc_txt", "Old City");

    Map<String, Object> stateCountyRow = new HashMap<>();
    stateCountyRow.put("postal_locator_uid", 101L);
    stateCountyRow.put("state_cd", "NY");
    stateCountyRow.put("cnty_cd", "001");

    Map<String, Object> countryRow = new HashMap<>();
    countryRow.put("postal_locator_uid", 102L);
    countryRow.put("cntry_cd", "USA");

    when(nbsTemplate.queryForList(eq(PersonMortalityMergeHandler.SELECT_DEATH_CITY_FOR_AUDIT_BEFORE_UPDATE),
        any(MapSqlParameterSource.class)))
        .thenReturn(List.of(cityRow));

    when(nbsTemplate.queryForList(
        eq(PersonMortalityMergeHandler.SELECT_DEATH_STATE_AND_COUNTY_FOR_AUDIT_BEFORE_UPDATE),
        any(MapSqlParameterSource.class)))
        .thenReturn(List.of(stateCountyRow));

    when(nbsTemplate.queryForList(eq(PersonMortalityMergeHandler.SELECT_DEATH_COUNTRY_FOR_AUDIT_BEFORE_UPDATE),
        any(MapSqlParameterSource.class)))
        .thenReturn(List.of(countryRow));

    handler.handleMerge("matchId", mockRequest, audit);

    assertTrue(
        audit.getRelatedTableAudits().stream()
            .anyMatch(r -> r.tableName().equals("Postal_locator") && !r.updates().isEmpty())
    );
    assertTrue(
        audit.getRelatedTableAudits().stream()
            .anyMatch(r -> r.tableName().equals("Entity_locator_participation") && !r.inserts().isEmpty())
    );
  }


}
