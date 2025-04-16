package gov.cdc.nbs.deduplication.duplicates.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.service.PatientMergeHandler;
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
  private PatientMergeHandler mergeGroupHandler;

  @InjectMocks
  private PatientMergeController patientMergeController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(patientMergeController).build();
  }

  @Test
  void testGetPossibleMatchGroups() throws Exception {
    int page = 0;
    int size = 5;
    when(mergeGroupHandler.getPotentialMatches(page, size)).thenReturn(expectedMergeGroupResponse());


    // Act & Assert
    mockMvc.perform(get("/deduplication/matches/requiring-review")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedMergeGroupResponseJson()));
  }

  private List<MatchesRequireReviewResponse> expectedMergeGroupResponse() {
    return Arrays.asList(
        new MatchesRequireReviewResponse("111122", "john smith", "1990-01-01", "2000-01-01", 2),
        new MatchesRequireReviewResponse("111133", "Andrew James", "1990-02-02", "2000-02-02", 4)
    );
  }

  private String expectedMergeGroupResponseJson() {
    return """
        [
          {
            "patientId": "111122",
            "patientName": "john smith",
            "createdDate": "1990-01-01",
            "identifiedDate": "2000-01-01",
            "numOfMatchingRecords": 2
          },
          {
            "patientId": "111133",
            "patientName": "Andrew James",
            "createdDate": "1990-02-02",
            "identifiedDate": "2000-02-02",
            "numOfMatchingRecords": 4
          }
        ]
        """;
  }

  @Test
  void testUpdateMergeStatus() throws Exception {
    MergeStatusRequest request = new MergeStatusRequest(100L, false);

    // Act & Assert
    mockMvc.perform(post("/deduplication/merge-status")
            .contentType("application/json")
            .content("{\"personUid\": 100, \"status\": \"false\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Merge status updated successfully."));

    verify(mergeGroupHandler).updateMergeStatus(request);
  }

  @Test
  void testUpdateMergeStatus_Error() throws Exception {
    MergeStatusRequest request = new MergeStatusRequest(100L, false);

    doThrow(new RuntimeException("Some error")).when(mergeGroupHandler).updateMergeStatus(request);

    // Act & Assert
    mockMvc.perform(post("/deduplication/merge-status")
            .contentType("application/json")
            .content("{\"personUid\": 100, \"status\": \"false\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error updating merge status: Some error"));
    verify(mergeGroupHandler).updateMergeStatus(request);
  }



}
