package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ElrReportsController.class)
@ActiveProfiles("test")
class ElrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RawELRService rawELRService;
    @MockBean
    private ICdaMapper cdaMapper;
    @MockBean
    private IEcrMsgQueryService ecrMsgQueryService;
    @MockBean
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    @MockBean
    private CustomMetricsBuilder customMetricsBuilder;
    @MockBean
    private IHL7Service hl7Service;
    @Test
    void testSaveHL7Message() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "HL7";
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/data-ingestion")
                        .header("msgType", messageType)
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(hl7Payload);
        rawERLDto.setValidationActive(true);

        verify(rawELRService).submission(rawERLDto, "1");

    }

    @Test
    void testSaveHL7Message_no_ValidationActivate() throws Exception {
        String payload = "Test payload";
        String messageType = "HL7";
        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(payload);

        when(rawELRService.submission(rawERLDto, "1")).thenReturn("OK");
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/data-ingestion")
                        .param("id", "1").with(SecurityMockMvcRequestPostProcessors.jwt())
                        .header("msgType", messageType)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    void testSaveHL7MessageHeaderIsEmpty() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/data-ingestion")
                        .header("msgType", messageType)
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderIsEmptyType() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/data-ingestion")
                        .header("msgType", "")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderTypeInvalid() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/data-ingestion")
                        .header("msgType", "AAA")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testHl7Validator() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/elr/validation")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(hl7Service).hl7Validator(hl7Payload);
    }

}
