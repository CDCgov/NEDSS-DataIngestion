package gov.cdc.nbs.deduplication.duplicates.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergePatientRequest;
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
class MergeGroupControllerTest {

  @Mock
  private MergeGroupHandler mergeGroupHandler;

  @Mock
  private MergePatientHandler mergePatientHandler;

  @InjectMocks
  private MergeGroupController mergeGroupController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(mergeGroupController).build();
  }

  @Test
  void testGetPossibleMatchGroups() throws Exception {
    int page = 0;
    int size = 5;
    when(mergeGroupHandler.getMergeGroups(page, size)).thenReturn(expectedMergeGroupResponse());


    // Act & Assert
    mockMvc.perform(get("/deduplication/merge-groups")
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

  private List<MergeGroupResponse> expectedMergeGroupResponse() {
    return Arrays.asList(
        new MergeGroupResponse("100", "1990-01-01", "john smith", null),
        new MergeGroupResponse("200", "1990-02-02", "Andrew James", null)
    );
  }

  private String expectedMergeGroupResponseJson() {
    return "[{'personOfTheGroup':'100','dateIdentified': 1990-01-01, 'mostRecentPersonName': 'john smith'}, " +
        "{'personOfTheGroup':'200','dateIdentified': 1990-02-02, 'mostRecentPersonName': 'Andrew James'}]";
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



}
