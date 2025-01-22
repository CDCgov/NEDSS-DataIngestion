package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.rawmessage.service.ReportsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ReportsController.class)
@ActiveProfiles("test")
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportsService reportsService;

    @Test
    void testGetAllRecordsWithReports() throws Exception {
        List<NbsInterfaceModel> mockReports = new ArrayList<>();
        NbsInterfaceModel mockReport = new NbsInterfaceModel();
        mockReport.setNbsInterfaceUid(12345);
        mockReports.add(mockReport);

        when(reportsService.getAllSubmissions()).thenReturn(mockReports);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-all-reports")
                        .header("clientid", "testClientId")
                        .header("clientsecret", "testClientSecret")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nbsInterfaceUid").value(12345));

        verify(reportsService).getAllSubmissions();
    }

    @Test
    void testGetAllRecordsWithNoReports() throws Exception {
        when(reportsService.getAllSubmissions()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-all-reports")
                        .header("clientid", "testClientId")
                        .header("clientsecret", "testClientSecret")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(reportsService).getAllSubmissions();
    }

}
