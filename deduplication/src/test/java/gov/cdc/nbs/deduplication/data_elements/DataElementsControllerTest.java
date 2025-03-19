package gov.cdc.nbs.deduplication.data_elements;

import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DataElementsControllerTest {

    @Mock
    private DataElementsService service;

    @InjectMocks
    private DataElementsController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testSaveDataElementConfiguration_Success() throws Exception {
        // Given
        Mockito.doNothing().when(service).saveDataElementConfiguration(Mockito.any(DataElementsDTO.class));

        // Then
        mockMvc.perform(post("/api/deduplication/save-data-elements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataElements\":{\"firstName\":{\"active\":true,\"oddsRatio\":1.5,\"logOdds\":0.5,\"threshold\":0.8}}}"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveDataElementConfiguration_Failure() throws Exception {
        // Given
        Mockito.doThrow(new RuntimeException("Error saving configuration"))
                .when(service).saveDataElementConfiguration(Mockito.any(DataElementsDTO.class));

        // Then
        mockMvc.perform(post("/api/deduplication/save-data-elements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataElements\":{\"firstName\":{\"active\":true,\"oddsRatio\":1.5,\"logOdds\":0.5,\"threshold\":0.8}}}"))
                .andExpect(status().isInternalServerError());
    }
}