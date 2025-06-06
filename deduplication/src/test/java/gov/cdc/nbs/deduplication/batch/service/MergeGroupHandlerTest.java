package gov.cdc.nbs.deduplication.batch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Ethnicity;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.GeneralPatientInformation;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Mortality;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.SexAndBirth;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.MergeGroupHandler;
import gov.cdc.nbs.deduplication.merge.PdfBuilder;

@ExtendWith(MockitoExtension.class)
class MergeGroupHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private JdbcTemplate template;

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private PdfBuilder mergeGroupService;

  @InjectMocks
  private MergeGroupHandler mergeGroupHandler;

  @Test
  void testUnMergeAll() {
    mergeGroupHandler.unMergeAll(100L);

    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UN_MERGE_ALL_GROUP),
        any(MapSqlParameterSource.class));
  }

  @Test
  void testUnMergeSinglePerson() {
    mergeGroupHandler.unMergeSinglePerson(100L, 111l);
    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UN_MERGE_SINGLE_PERSON),
        any(MapSqlParameterSource.class));
  }

  @Test
  void testUpdateMergeStatusForPatients() {
    String survivorPersonId = "111";
    List<String> personIds = List.of("222", "333");

    updateMergeStatusForPatientsMocking();
    mergeGroupHandler.resolvePatientMergeStatuses(survivorPersonId, personIds);
    updateMergeStatusForPatientsVerifying(survivorPersonId);
  }

  private void updateMergeStatusForPatientsMocking() {

    // Mock the update operations
    when(deduplicationTemplate.update(
        eq(QueryConstants.MARK_PATIENTS_AS_MERGED),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    when(deduplicationTemplate.update(
        eq(QueryConstants.SET_IS_MERGE_TO_FALSE_FOR_EXCLUDED_PATIENTS),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);
  }

  @SuppressWarnings("unchecked")
  private void updateMergeStatusForPatientsVerifying(String survivorPersonId) {
    Long expectedPersonUid = Long.valueOf(survivorPersonId);

    // Verify markMergedRecordAsMerge
    verify(deduplicationTemplate).update(
        eq(QueryConstants.MARK_PATIENTS_AS_MERGED),
        argThat((MapSqlParameterSource params) -> {
          List<Long> potentialIds = (List<Long>) params.getValue("potentialIds");
          Long personUid = (Long) params.getValue("personUid");
          return potentialIds != null && potentialIds.size() == 3 &&
              personUid != null && personUid.equals(expectedPersonUid);
        }));

    // Verify markExcludedRecordAsNoMerge
    verify(deduplicationTemplate).update(
        eq(QueryConstants.SET_IS_MERGE_TO_FALSE_FOR_EXCLUDED_PATIENTS),
        argThat((MapSqlParameterSource params) -> {
          List<Long> potentialUids = (List<Long>) params.getValue("potentialUids");
          Long personUid = (Long) params.getValue("personUid");
          return potentialUids != null && potentialUids.size() == 2 &&
              personUid != null && personUid.equals(expectedPersonUid);
        }));

    // Verify markSingleRemainingRecordAsNoMergeIfExists
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        argThat((MapSqlParameterSource params) -> {
          String personUid = (String) params.getValue("personUid");
          return personUid != null && personUid.equals(survivorPersonId);
        }));
  }

  @Test
  void testGetPotentialMatchesDetails() {
    long personId = 123L;
    List<String> nbsPersonIds = Arrays.asList("person1", "person2");
    List<PersonMergeData> mockPersonMergeData = createMockPersonMergeData();

    mockPossibleMatchesOfPatient(personId, nbsPersonIds);
    mockFetchPersonsMergeData(nbsPersonIds, mockPersonMergeData);

    // Act
    List<PersonMergeData> result = mergeGroupHandler.getPotentialMatchesDetails(personId);

    // Assert
    verifyAndAssertResults(result);
  }

  private List<PersonMergeData> createMockPersonMergeData() {
    return List.of(
        new PersonMergeData(
            "person_local_id",
            "person_id", // commentDate
            "2003-01-01",
            new PersonMergeData.AdminComments("2023-01-01", "test comment"), // adminComments
            new Ethnicity( // Ethnicity
                "2023-01-01",
                "Hispanic or Latino",
                "Yes",
                "Unknown"),
            new SexAndBirth(
                "2025-05-27T00:00:00",
                "2025-05-12T00:00:00",
                "Male",
                "Refused",
                "Did not ask",
                "Add Gender",
                "Male",
                "No",
                "1",
                "Birth City",
                "Tennessee",
                "Some County",
                "United States"),
            new Mortality( // Mortality
                "2023-03-01",
                "Y",
                "2023-04-01T00:00:00Z",
                "Atlanta",
                "Georgia",
                "Fulton",
                "US"),
            new GeneralPatientInformation(
                "2025-05-27T00:00:00",
                "Annulled",
                "MotherMaiden",
                "2",
                "0",
                "Mining",
                "10th grade",
                "Eastern Frisian",
                "Yes",
                "123"),
            List.of( // Investigations
                new PersonMergeData.Investigation("1", "2023-06-01T00:00:00Z", "Condition A"),
                new PersonMergeData.Investigation("2", "2023-07-01T00:00:00Z", "Condition B")),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()));
  }

  private void mockPossibleMatchesOfPatient(long personId, List<String> possibleMatchesMpiIds) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_IDS_BY_PATIENT_ID),
        argThat((MapSqlParameterSource params) -> Objects.equals(params.getValue("personUid"), personId)),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(possibleMatchesMpiIds);
  }

  private void mockFetchPersonsMergeData(List<String> npsPersonIds, List<PersonMergeData> mockPersonMergeData) {
    when(patientRecordService.fetchPersonsMergeData(npsPersonIds)).thenReturn(mockPersonMergeData);
  }

  private void verifyAndAssertResults(List<PersonMergeData> result) {
    assertNotNull(result);
    assertEquals(1, result.size());
    PersonMergeData firstResult = result.getFirst();
    assertEquals("2023-01-01", firstResult.adminComments().date());
    assertEquals("test comment", firstResult.adminComments().comment());
  }

}
