package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
@WebMvcTest(EcrReportsController.class)
@ActiveProfiles("test")
class EcrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Test
    void testSaveIncomingEcrWithRR() throws Exception {
        String payload = "<eICRXML>eicrContent</eICRXML><RRXML>rrContent</RRXML>";
        NbsInterfaceModel mockModel = new NbsInterfaceModel();
        mockModel.setNbsInterfaceUid(123456);

        when(nbsRepositoryServiceProvider.saveIncomingEcrMessageWithRR(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecrs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(payload)
                        .header("systemNm", "testSystem")
                        .header("origDocTypeEicr", "EICR")
                        .header("origDocTypeRR", "RR")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("123456"));

        verify(nbsRepositoryServiceProvider).saveIncomingEcrMessageWithRR("eicrContent", "testSystem", "EICR", "rrContent", "RR");
    }

    @Test
    void testSaveIncomingEcrWithoutRR() throws Exception {
        String payload = "<eICRXML>eicrContent</eICRXML><RRXML>null</RRXML>";
        NbsInterfaceModel mockModel = new NbsInterfaceModel();
        mockModel.setNbsInterfaceUid(123456);

        when(nbsRepositoryServiceProvider.saveIncomingEcrMessageWithoutRR(anyString(), anyString(), anyString()))
                .thenReturn(mockModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecrs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(payload)
                        .header("systemNm", "testSystem")
                        .header("origDocTypeEicr", "EICR")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("123456"));

        verify(nbsRepositoryServiceProvider).saveIncomingEcrMessageWithoutRR("eicrContent", "testSystem", "EICR");
    }

    @Test
    void testSaveIncomingEcrWithMalformedXml() throws Exception {
        String payload = "invalidXmlContent";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecrs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(payload)
                        .header("systemNm", "testSystem")
                        .header("origDocTypeEicr", "EICR")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(nbsRepositoryServiceProvider, never()).saveIncomingEcrMessageWithRR(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(nbsRepositoryServiceProvider, never()).saveIncomingEcrMessageWithoutRR(anyString(), anyString(), anyString());
    }
}