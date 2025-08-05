package gov.cdc.nbs.deduplication.merge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class PatientMergeControllerTest {

  @Mock
  private MergeGroupService mergeGroupService;

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
  void testExportMatchesAsPDF() throws Exception {
    List<MatchRequiringReview> mockMatches = List.of(
        new MatchRequiringReview(1l, "111122", "444", "john smith", "1990-01-01", "2000-01-01", 2),
        new MatchRequiringReview(2l, "111133", " 333", "Andrew James", "1990-02-02", "2000-02-02", 4));

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
        new MatchRequiringReview(1l, "111122", "444", "John Smith", "2023-01-01T10:00:00Z", "2023-01-05T15:00:00Z", 2),
        new MatchRequiringReview(2l, "111133", "333", "Andrew James", "2023-02-02T11:00:00Z", "2023-02-06T16:30:00Z",
            4));

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
                "444","John Smith","01/01/2023 10:00 AM","01/05/2023 03:00 PM",2
                "333","Andrew James","02/02/2023 11:00 AM","02/06/2023 04:30 PM",4
                """));

    verify(matchesRequiringReviewResolver).resolveAll(PatientMergeController.DEFAULT_SORT);
    verify(pdfBuilder, times(4)).formatDateTime(anyString());
  }

  @Test
  void markNoMergeTest() {

    patientMergeController.markNoMerge(1l, 2l);

    verify(mergeGroupService, times(1)).markNoMerge(1l, 2l);
  }

  @Test
  void markAllNoMergeTest() {

    patientMergeController.markAllNoMerge(1l);

    verify(mergeGroupService, times(1)).markAllNoMerge(1l);
  }

  @Test
  void getPotentialMatchesTest() {
    patientMergeController.getPotentialMatches(0, 25, "name,desc");

    verify(matchesRequiringReviewResolver, times(1)).resolve(0, 25, "name,desc");
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
