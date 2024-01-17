package gov.cdc.dataingestion.rawmessage;

import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.rawmessage.controller.ElrReportsController;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.*;


@EnableConfigurationProperties(RsaKeyProperties.class)
@WebMvcTest(ElrReportsController.class)
class ElrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EcrMsgQueryService ecrMsgQueryService;
    @MockBean
    private RawELRService rawELRService;
    @MockBean
    private ICdaMapper cdaMapper;
    @MockBean
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @MockBean
    private CustomMetricsBuilder customMetricsBuilder;
    @MockBean
    private IHL7Service hl7Service;

    @Test
    void testSave() throws Exception {
        String payload = "Test payload";
        String messageType = "HL7";
        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(payload);

        when(rawELRService.submission(rawERLDto)).thenReturn("OK");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .param("id", "1").with(SecurityMockMvcRequestPostProcessors.jwt())
                        .header("msgType", messageType)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        String id = "test-id";
        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setId(id);

        when(rawELRService.getById(id)).thenReturn(rawERLDto);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports/{id}", id)
                        .param("id", "1").with(SecurityMockMvcRequestPostProcessors.jwt())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
    }
}
