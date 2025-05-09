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
  void testUpdateMergeStatus() {

    mergeGroupHandler.updateMergeStatusForGroup(100L);

    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP),
        any(MapSqlParameterSource.class));
  }

  @Test
  void testUpdateMergeStatusForPatients() {
    String survivorPersonId = "survivorId";
    List<String> personIds = List.of("person1", "person2");
    List<String> mpiPersonIds = List.of("mpi1", "mpi2");

    updateMergeStatusForPatientsMocking(mpiPersonIds);
    mergeGroupHandler.updateMergeStatusForPatients(survivorPersonId, personIds);
    updateMergeStatusForPatientsVerifying(survivorPersonId);
  }

  private void updateMergeStatusForPatientsMocking(List<String> mpiPersonIds) {
    // Mock the call of getMpiIdsByPersonIds method
    when(deduplicationTemplate.query(
        eq(QueryConstants.PATIENT_IDS_BY_PERSON_UIDS),
        argThat((MapSqlParameterSource params) -> params.getValue("personIds") != null),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(mpiPersonIds);

    // Mock the update operations
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    // Mock the markNonActiveRecordAsNoMerge call
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    // Mock the markSingleRemainingRecordAsNoMergeIfExists call
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);
  }

  @SuppressWarnings("unchecked")
  private void updateMergeStatusForPatientsVerifying(String survivorPersonId) {
    // Verify the markMergedRecordAsMerge call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS),
        argThat((MapSqlParameterSource params) -> {
          List<String> mpiIds = (List<String>) params.getValue("mpiIds");
          String personId = (String) params.getValue("personId");
          return mpiIds != null && mpiIds.size() == 2
              && personId != null && personId.equals(survivorPersonId);
        }));

    // Verify the markNonActiveRecordAsNoMerge call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS),
        argThat((MapSqlParameterSource params) -> {
          List<String> mpiIds = (List<String>) params.getValue("mpiIds");
          String personId = (String) params.getValue("personId");
          List<String> personIdsParam = (List<String>) params.getValue("personIds");
          return mpiIds != null && mpiIds.size() == 2
              && personId != null && personId.equals(survivorPersonId)
              && personIdsParam != null && personIdsParam.size() == 2;
        }));

    // Verify the markSingleRemainingRecordAsNoMergeIfExists call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        argThat((MapSqlParameterSource params) -> {
          String personId = (String) params.getValue("personUid");
          return personId != null && personId.equals(survivorPersonId);
        }));
  }

  @Test
  void testGetPotentialMatchesDetails() {
    long personId = 123L;
    List<String> possibleMatchesMpiIds = Arrays.asList("mpi1", "mpi2");
    List<String> npsPersonIds = Arrays.asList("person1", "person2");
    List<PersonMergeData> mockPersonMergeData = createMockPersonMergeData();

    mockPossibleMatchesOfPatient(personId, possibleMatchesMpiIds);
    mockPersonIdsByMpiIds(possibleMatchesMpiIds, npsPersonIds);
    mockFetchPersonsMergeData(npsPersonIds, mockPersonMergeData);

    // Act
    List<PersonMergeData> result = mergeGroupHandler.getPotentialMatchesDetails(personId);

    // Assert
    verifyAndAssertResults(result);
  }

  private List<PersonMergeData> createMockPersonMergeData() {
    return List.of(
        new PersonMergeData(
            "2023-01-01", // commentDate
            "test comment", // adminComments
            new Ethnicity( // Ethnicity
                "2023-01-01",
                "Hispanic or Latino",
                "Yes",
                "Unknown"),
            new SexAndBirth( // Sex & Birth
                "2023-02-01",
                "1990-01-01T00:00:00Z",
                "M",
                "",
                "",
                "Male",
                true,
                1,
                "12345",
                "GA",
                "US",
                "Male"),
            new Mortality( // Mortality
                "2023-03-01",
                "Y",
                "2023-04-01T00:00:00Z",
                "Atlanta",
                "Georgia",
                "Fulton",
                "US"),
            new GeneralPatientInformation(
                "2023-05-01",
                "Married",
                "Jane Doe",
                2,
                1,
                "Engineer",
                "Bachelor's Degree",
                "English",
                "Y",
                "123456789"),
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

  private void mockPersonIdsByMpiIds(List<String> possibleMatchesMpiIds, List<String> npsPersonIds) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        argThat(
            (MapSqlParameterSource params) -> Objects.equals(params.getValue("mpiPersonIds"), possibleMatchesMpiIds)),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(npsPersonIds);
  }

  private void mockFetchPersonsMergeData(List<String> npsPersonIds, List<PersonMergeData> mockPersonMergeData) {
    when(patientRecordService.fetchPersonsMergeData(npsPersonIds)).thenReturn(mockPersonMergeData);
  }

  private void verifyAndAssertResults(List<PersonMergeData> result) {
    assertNotNull(result);
    assertEquals(1, result.size());
    PersonMergeData firstResult = result.getFirst();
    assertEquals("2023-01-01", firstResult.commentDate());
    assertEquals("test comment", firstResult.adminComments());
  }

}
