package gov.cdc.nbs.deduplication.duplicates.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergePatientRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
import gov.cdc.nbs.deduplication.duplicates.service.MergePatientHandler;
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
  private MergePatientHandler mergePatientHandler;

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

  @Test
  void testUpdateGroupNoMerge() throws Exception {

    // Act & Assert
    mockMvc.perform(post("/deduplication/group-no-merge")
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
    mockMvc.perform(post("/deduplication/group-no-merge")
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
    mockMvc.perform(post("/deduplication/merge-patient")
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
    mockMvc.perform(post("/deduplication/merge-patient")
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
    mockMvc.perform(post("/deduplication/merge-patient")
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
    mockMvc.perform(get("/deduplication/matches/details/{patientId}", patientId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedPersonMergeDataJson()));

    verify(mergeGroupHandler).getPotentialMatchesDetails(patientId);
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

  private List<PersonMergeData> expectedPersonMergeData() {
    return Arrays.asList(
        new PersonMergeData(
            "2023-01-01",
            "Sample comments",
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        )
    );
  }

  private String expectedPersonMergeDataJson() {
    return """
        [
          {
            "asOfDate": "2023-01-01",
            "comments": "Sample comments",
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
