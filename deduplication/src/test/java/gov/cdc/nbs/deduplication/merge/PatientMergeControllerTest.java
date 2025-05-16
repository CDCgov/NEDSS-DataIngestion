package gov.cdc.nbs.deduplication.merge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gov.cdc.nbs.deduplication.batch.model.MergePatientRequest;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PatientMergeControllerTest {

  @Mock
  private MergeGroupHandler mergeGroupHandler;

  @Mock
  private MatchesRequiringReviewResolver matchesRequiringReviewResolver;

  @Mock
  private MergePatientHandler mergePatientHandler;

  @Mock
  private PdfBuilder pdfBuilder;

  @InjectMocks
  private PatientMergeController patientMergeController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(patientMergeController).build();
  }

  @Test
  void testUpdateGroupNoMerge() throws Exception {

    // Act & Assert
    mockMvc.perform(post("/merge/group-no-merge")
        .contentType("application/json")
        .content("{\"personOfTheGroup\": 100}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Merge status updated successfully."));

    verify(mergeGroupHandler).updateMergeStatusForGroup(100L);
  }

  @Test
  void testUpdateGroupNoMerge_Error() throws Exception {

    doThrow(new RuntimeException("Some error")).when(mergeGroupHandler).updateMergeStatusForGroup(100L);

    // Act & Assert
    mockMvc.perform(post("/merge/group-no-merge")
        .contentType("application/json")
        .content("{\"personOfTheGroup\": 100}"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error updating merge status: Some error"));

    verify(mergeGroupHandler).updateMergeStatusForGroup(100L);
  }

  @Test
  void testMergeRecords_Success() throws Exception {
    MergePatientRequest mergeRequest = new MergePatientRequest();
    mergeRequest.setSurvivorPersonId("survivor123");
    mergeRequest.setSupersededPersonIds(Arrays.asList("superseded1", "superseded2"));

    // Act & Assert
    mockMvc.perform(post("/merge/merge-patient")
        .contentType("application/json")
        .content(
            "{\"survivorPersonId\": \"survivor123\", \"supersededPersonIds\": [\"superseded1\", \"superseded2\"]}"))
        .andExpect(status().isOk());

    verify(mergePatientHandler).performMerge("survivor123", Arrays.asList("superseded1", "superseded2"));
  }

  @Test
  void testMergeRecords_BadRequest() throws Exception {
    MergePatientRequest mergeRequest = new MergePatientRequest();
    mergeRequest.setSurvivorPersonId(null); // Invalid data
    mergeRequest.setSupersededPersonIds(null); // Invalid data

    // Act & Assert
    mockMvc.perform(post("/merge/merge-patient")
        .contentType("application/json")
        .content("{\"survivorPersonId\": null, \"supersededPersonIds\": null}"))
        .andExpect(status().isBadRequest());

    verify(mergePatientHandler, never()).performMerge(any(), any());
  }

  @Test
  void testMergeRecords_InternalServerError() throws Exception {
    MergePatientRequest mergeRequest = new MergePatientRequest();
    mergeRequest.setSurvivorPersonId("survivor123");
    mergeRequest.setSupersededPersonIds(Arrays.asList("superseded1", "superseded2"));

    doThrow(new RuntimeException("Merge failed")).when(mergePatientHandler)
        .performMerge("survivor123", Arrays.asList("superseded1", "superseded2"));

    // Act & Assert
    mockMvc.perform(post("/merge/merge-patient")
        .contentType("application/json")
        .content(
            "{\"survivorPersonId\": \"survivor123\", \"supersededPersonIds\": [\"superseded1\", \"superseded2\"]}"))
        .andExpect(status().isInternalServerError());

    verify(mergePatientHandler).performMerge("survivor123", Arrays.asList("superseded1", "superseded2"));
  }

  @Test
  void testGetPotentialMatchesDetails() throws Exception {
    long patientId = 123L;
    List<PersonMergeData> mockResponse = expectedPersonMergeData();

    when(mergeGroupHandler.getPotentialMatchesDetails(patientId)).thenReturn(mockResponse);

    // Act & Assert
    mockMvc.perform(get("/merge/{patientId}", patientId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedPersonMergeDataJson()));

    verify(mergeGroupHandler).getPotentialMatchesDetails(patientId);
  }

  @Test
  void testExportMatchesAsCSV() throws Exception {
    List<MatchRequiringReview> mockMatches = Arrays.asList(
        new MatchRequiringReview("111122", "john smith", "1990-01-01", "2000-01-01", 2),
        new MatchRequiringReview("111133", "Andrew James", "1990-02-02", "2000-02-02", 4));

    when(matchesRequiringReviewResolver.resolveAll(PatientMergeController.DEFAULT_SORT)).thenReturn(mockMatches);

    mockMvc.perform(get("/merge/export/csv"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/csv"))
        .andExpect(header().string("Content-Disposition", "attachment; filename=matches_requiring_review.csv"))
        .andExpect(content().string("""
            Patient ID,Person Name,Date Created,Date Identified,Number of Matching Records
            "111122","john smith","1990-01-01","2000-01-01",2
            "111133","Andrew James","1990-02-02","2000-02-02",4
            """.replace("\n", System.lineSeparator()))); // Ensures platform-independent line endings
  }

  @Test
  void testExportMatchesAsPDF() throws Exception {
    List<MatchRequiringReview> mockMatches = List.of(
        new MatchRequiringReview("111122", "john smith", "1990-01-01", "2000-01-01", 2),
        new MatchRequiringReview("111133", "Andrew James", "1990-02-02", "2000-02-02", 4));

    when(matchesRequiringReviewResolver.resolveAll(PatientMergeController.DEFAULT_SORT)).thenReturn(mockMatches);

    // verify the interaction and status
    mockMvc.perform(get("/merge/export/pdf"))
        .andExpect(status().isOk());

    verify(matchesRequiringReviewResolver).resolveAll(PatientMergeController.DEFAULT_SORT);
    verify(pdfBuilder).build(
        any(HttpServletResponse.class),
        eq(mockMatches),
        anyString(), // timestampForFilename
        anyString() // timestampForFooter
    );
  }

  private List<PersonMergeData> expectedPersonMergeData() {
    return List.of(
        new PersonMergeData(
            "2023-01-01", // commentDate
            "test comment", // adminComments
            new PersonMergeData.Ethnicity( // Ethnicity
                "2023-01-01",
                "Hispanic or Latino",
                "Yes",
                "Unknown"),
            new PersonMergeData.SexAndBirth( // Sex & Birth
                "2023-02-01",
                "1990-01-01T00:00:00Z",
                "M",
                "Not applicable",
                "",
                "Male",
                true,
                1,
                "12345",
                "GA",
                "US",
                "Male"),
            new PersonMergeData.Mortality( // Mortality
                "2023-03-01",
                "Y",
                "2023-04-01T00:00:00Z",
                "Atlanta",
                "Georgia",
                "Fulton",
                "US"),
            new PersonMergeData.GeneralPatientInformation(
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

  private String expectedPersonMergeDataJson() {
    return """
        [
          {
            "commentDate": "2023-01-01",
            "adminComments": "test comment",
            "ethnicity": {
              "asOfDate": "2023-01-01",
              "ethnicGroupDescription": "Hispanic or Latino",
              "spanishOrigin": "Yes",
              "ethnicUnknownReason": "Unknown"
            },
            "sexAndBirth": {
              "asOfDate": "2023-02-01",
              "birthTime": "1990-01-01T00:00:00Z",
              "currentSexCode": "M",
              "sexUnknownReason": "Not applicable",
              "additionalGenderCode": "",
              "birthGenderCode": "Male",
              "multipleBirthIndicator": true,
              "birthOrderNumber": 1,
              "birthCityCode": "12345",
              "birthStateCode": "GA",
              "birthCountryCode": "US",
              "preferredGender": "Male"
            },
            "mortality": {
              "asOfDate": "2023-03-01",
              "deceasedIndicatorCode": "Y",
              "deceasedTime": "2023-04-01T00:00:00Z",
              "deathCity": "Atlanta",
              "deathState": "Georgia",
              "deathCounty": "Fulton",
              "deathCountry": "US"
            },
            "generalPatientInformation": {
              "asOfDate": "2023-05-01",
              "maritalStatusDescription": "Married",
              "mothersMaidenName": "Jane Doe",
              "adultsInHouseholdNumber": 2,
              "childrenInHouseholdNumber": 1,
              "occupationCode": "Engineer",
              "educationLevelDescription": "Bachelor's Degree",
              "primaryLanguageDescription": "English",
              "speaksEnglishCode": "Y",
              "stateHivCaseId": "123456789"
            },
            "investigations": [
              {
                "investigationId": "1",
                "startedOn": "2023-06-01T00:00:00Z",
                "condition": "Condition A"
              },
              {
                "investigationId": "2",
                "startedOn": "2023-07-01T00:00:00Z",
                "condition": "Condition B"
              }
            ],
            "address": [],
            "telecom": [],
            "name": [],
            "identifiers": [],
            "race": []
          }
        ]
        """;
  }

}
