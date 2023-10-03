package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import org.junit.jupiter.api.Assertions;
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
public class ElrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RawELRService rawELRService;

    @Test
    public void testSaveHL7Message() throws Exception {
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

}
