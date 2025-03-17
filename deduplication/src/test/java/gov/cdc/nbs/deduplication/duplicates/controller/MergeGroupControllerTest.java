package gov.cdc.nbs.deduplication.duplicates.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
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
    // Arrange
    int page = 0;
    int size = 5;
    List<MergeGroupResponse> expectedResponses = Arrays.asList(
        new MergeGroupResponse("1990-01-01", "john smith", null),
        new MergeGroupResponse("1990-02-02", "Andrew James", null)
    );

    when(mergeGroupHandler.getMergeGroups(page, size)).thenReturn(expectedResponses);

    // Act & Assert
    mockMvc.perform(get("/api/deduplication/merge-groups")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().json("[{'dateIdentified': 1990-01-01, 'mostRecentPersonName': 'john smith'}, " +
            "{'dateIdentified': 1990-02-02, 'mostRecentPersonName': 'Andrew James'}]"));
  }


}
