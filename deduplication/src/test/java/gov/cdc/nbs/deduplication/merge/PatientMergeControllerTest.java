package gov.cdc.nbs.deduplication.merge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.AdminComments;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PatientMergeControllerTest {

  @Mock
  private MergeGroupHandler mergeGroupHandler;

  @Mock
  private MatchesRequiringReviewResolver matchesRequiringReviewResolver;

  @Mock
  MergeService mergeService;

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
  void testUnMergeAll() throws Exception {
    Long patientId = 100L;

    mockMvc.perform(delete("/merge/{patientId}", patientId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(mergeGroupHandler).unMergeAll(patientId);
  }

  @Test
  void testUnMergeAll_Error() throws Exception {
    Long patientId = 100L;

    doThrow(new RuntimeException("Some error")).when(mergeGroupHandler).unMergeAll(patientId);

    mockMvc.perform(delete("/merge/{patientId}", patientId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());

    verify(mergeGroupHandler).unMergeAll(patientId);
  }

  @Test
  void testUnMergeSinglePerson() throws Exception {
    Long patientId = 100L;
    Long removePatientId = 111L;

    mockMvc.perform(delete("/merge/{patientId}/{removePatientId}", patientId, removePatientId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(mergeGroupHandler).unMergeSinglePerson(patientId, removePatientId);
  }

  @Test
  void testUnMergeSinglePerson_Error() throws Exception {
    Long patientId = 100L;
    Long removePatientId = 111L;

    doThrow(new RuntimeException("Some error")).when(mergeGroupHandler)
        .unMergeSinglePerson(patientId, removePatientId);

    mockMvc.perform(delete("/merge/{patientId}/{removePatientId}", patientId, removePatientId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());

    verify(mergeGroupHandler).unMergeSinglePerson(patientId, removePatientId);
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
  void testMergePatients_Success() throws Exception {
    Long matchId = 123L;
    String requestBody = createPatientMergeRequestJson();

    mockMvc.perform(post("/merge/{matchId}", matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(mergeService).performMerge(eq(matchId), any(PatientMergeRequest.class));
  }

  @Test
  void testMergePatients_ServiceThrowsException() throws Exception {
    Long matchId = 123L;
    String requestBody = createPatientMergeRequestJson();

    doThrow(new RuntimeException("Merge failed"))
        .when(mergeService).performMerge(eq(matchId), any(PatientMergeRequest.class));

    mockMvc.perform(post("/merge/{matchId}", matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isInternalServerError());

    verify(mergeService).performMerge(eq(matchId), any(PatientMergeRequest.class));
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

  @Test
  void testExportMatchesAsCSV() throws Exception {
    List<MatchRequiringReview> mockMatches = List.of(
        new MatchRequiringReview("111122", "John Smith", "2023-01-01T10:00:00Z", "2023-01-05T15:00:00Z", 2),
        new MatchRequiringReview("111133", "Andrew James", "2023-02-02T11:00:00Z", "2023-02-06T16:30:00Z", 4));

    when(matchesRequiringReviewResolver.resolveAll(PatientMergeController.DEFAULT_SORT)).thenReturn(mockMatches);
    when(pdfBuilder.formatDateTime("2023-01-01T10:00:00Z")).thenReturn("01/01/2023 10:00 AM");
    when(pdfBuilder.formatDateTime("2023-01-05T15:00:00Z")).thenReturn("01/05/2023 03:00 PM");
    when(pdfBuilder.formatDateTime("2023-02-02T11:00:00Z")).thenReturn("02/02/2023 11:00 AM");
    when(pdfBuilder.formatDateTime("2023-02-06T16:30:00Z")).thenReturn("02/06/2023 04:30 PM");

    mockMvc.perform(get("/merge/export/csv"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "text/csv"))
        .andExpect(header().string("Content-Disposition", "attachment; filename=matches_requiring_review.csv"))
        .andExpect(content().string(
            """
                Patient ID,Person Name,Date Created,Date Identified,Number of Matching Records
                "111122","John Smith","01/01/2023 10:00 AM","01/05/2023 03:00 PM",2
                "111133","Andrew James","02/02/2023 11:00 AM","02/06/2023 04:30 PM",4
                """));

    verify(matchesRequiringReviewResolver).resolveAll(PatientMergeController.DEFAULT_SORT);
    verify(pdfBuilder, times(4)).formatDateTime(anyString());
  }

  private List<PersonMergeData> expectedPersonMergeData() {
    return List.of(
        new PersonMergeData(
            "1",
            new AdminComments(
                "2023-01-01", // commentDate
                "test comment"), // adminComments
            new PersonMergeData.Ethnicity( // Ethnicity
                "2023-01-01",
                "Hispanic or Latino",
                "Unknown reason",
                "Cuban"),
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
            "personUid": "1",
            "adminComments": {"date": "2023-01-01", "comment":  "test comment"},
            "ethnicity": {
              "asOf": "2023-01-01",
              "ethnicity": "Hispanic or Latino",
              "spanishOrigin": "Cuban",
              "reasonUnknown": "Unknown reason"
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
            "addresses": [],
            "phoneEmails": [],
            "names": [],
            "identifications": [],
            "races": []
          }
        ]
        """;
  }

  private String createPatientMergeRequestJson() {
    return """
        {
            "survivingRecord": "surviving1",
            "adminCommentsSource": "superseded-1",
            "names": null,
            "addresses": null,
            "phoneEmails": null,
            "identifications": null,
            "races": null
        }
        """;
  }

}
