package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
@WebMvcTest(ElrReportsController.class)
@ActiveProfiles("test")
class ElrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RawElrService rawELRService;
    @MockitoBean
    private CustomMetricsBuilder customMetricsBuilder;
    @MockitoBean
    private IHL7Service hl7Service;
    @Test
    void testSaveHL7Message() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "HL7";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
                        .header("msgType", messageType)
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setType(messageType);
        rawElrDto.setPayload(hl7Payload);
        rawElrDto.setValidationActive(true);
        rawElrDto.setVersion("1");
        rawElrDto.setCustomMapper("");
        verify(rawELRService).submission(rawElrDto);

    }

    @Test
    void testSaveElrXmlMessage() throws Exception {
        String xmlPayload = "testxmlmessage";
        String messageType = "HL7-XML";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
                        .header("msgType", messageType)
                        .contentType("text/plain")
                        .content(xmlPayload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setType(messageType);
        rawElrDto.setPayload(xmlPayload);
        rawElrDto.setValidationActive(true);
        rawElrDto.setVersion("1");
        rawElrDto.setCustomMapper(null);

        verify(rawELRService).submission(rawElrDto);
    }

    @Test
    void testSaveHL7Message_no_ValidationActivate() throws Exception {
        String payload = "Test payload";
        String messageType = "HL7";
        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setType(messageType);
        rawElrDto.setPayload(payload);
        rawElrDto.setVersion("1");

        when(rawELRService.submission(rawElrDto)).thenReturn("OK");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
                        .header("msgType", messageType)
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderIsEmptyType() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
                        .header("msgType", "")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testSaveHL7MessageHeaderTypeInvalid() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs")
                        .header("msgType", "AAA")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testHl7Validator() throws Exception {
        String hl7Payload = "testmessage";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs/validate")
                        .contentType("text/plain")
                        .content(hl7Payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(hl7Service).hl7Validator(hl7Payload);
    }

}