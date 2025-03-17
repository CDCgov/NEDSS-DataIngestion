package gov.cdc.nbs.deduplication.duplicates.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.List;
import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
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
    mockMvc.perform(get("/api/deduplication/merge-groups")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedMergeGroupResponseJson()));
  }

  @Test
  void testUpdateMergeStatus() throws Exception {
    MergeStatusRequest request = new MergeStatusRequest(100L, false);

    // Act & Assert
    mockMvc.perform(post("/api/deduplication/merge-status")
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
    mockMvc.perform(post("/api/deduplication/merge-status")
            .contentType("application/json")
            .content("{\"personUid\": 100, \"status\": \"false\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error updating merge status: Some error"));

    verify(mergeGroupHandler).updateMergeStatus(request);
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


}
