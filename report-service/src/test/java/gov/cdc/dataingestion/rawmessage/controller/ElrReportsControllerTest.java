package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.verify;

@WebMvcTest(ElrReportsController.class)
@EnableConfigurationProperties(RsaKeyProperties.class)
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", messageType)
                        .header("validationActive", "false")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(hl7Payload);

        verify(rawELRService).submission(rawERLDto);

    }

    @Test
    void testSaveHL7MessageValidationActivated() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "HL7";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", messageType)
                        .header("validationActive", "true")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(hl7Payload);
        rawERLDto.setValidationActive(true);

        verify(rawELRService).submission(rawERLDto);
    }

    @Test
    void testSaveHL7MessageHeaderIsEmpty() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "HL7";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", messageType)
                        .header("validationActive", "")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderIsEmptyType() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", "")
                        .header("validationActive", "true")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderTypeInvalid() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", "AAA")
                        .header("validationActive", "true")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderValidationInvalid() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports")
                        .header("msgType", "HL7")
                        .header("validationActive", "AAA")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testHl7Validator() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports/validate-hl7")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(hl7Service).hl7Validator(hl7Payload);
    }

}
