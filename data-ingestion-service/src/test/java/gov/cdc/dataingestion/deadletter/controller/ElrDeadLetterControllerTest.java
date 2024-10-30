package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
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
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
@WebMvcTest(ElrDeadLetterController.class)
@ActiveProfiles("test")
class ElrDeadLetterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElrDeadLetterService elrDeadLetterService;


    @Test
    void testGetAllNewErrorMessageSuccess() throws Exception {
        List<ElrDeadLetterDto> dtoList = new ArrayList<>();
        ElrDeadLetterDto dto1 = new ElrDeadLetterDto(
                "1", "topic-a", "error stack trace", 1, "ERROR", "system", "system"
        );
        ElrDeadLetterDto dto2 = new ElrDeadLetterDto(
                "2", "topic-b", "error stack trace", 1, "ERROR", "system", "system"
        );

        dtoList.add(dto1);
        dtoList.add(dto2);

        when(elrDeadLetterService.getAllErrorDltRecord()).thenReturn(dtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/elrs/error-messages")
                    .with(SecurityMockMvcRequestPostProcessors.jwt())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorMessageId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorMessageSource").value("topic-a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorStackTrace").value("error stack trace"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dltOccurrence").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dltStatus").value("ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdBy").value("system"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].updatedBy").value("system"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].errorMessageId").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].errorMessageSource").value("topic-b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].errorStackTrace").value("error stack trace"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dltOccurrence").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dltStatus").value("ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].createdBy").value("system"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].updatedBy").value("system"));

    }

    @Test
    void testMessageReInject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/elrs/1")
                .contentType("text/plain")
                .content("HL7 message")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(elrDeadLetterService).updateAndReprocessingMessage("1", "HL7 message");
    }

    @Test
    void testGetErrorMessageByIdSuccess() throws Exception {
        ElrDeadLetterDto dto = new ElrDeadLetterDto(
                "1", "topic-a", "error stack trace", 1, "ERROR", "system", "system"
        );

        when(elrDeadLetterService.getDltRecordById("1")).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/elrs/error-messages/1")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .header("clientid", "test-client-id")
                        .header("clientsecret", "test-client-secret"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessageId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessageSource").value("topic-a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorStackTrace").value("error stack trace"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dltOccurrence").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dltStatus").value("ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("system"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedBy").value("system"));

        verify(elrDeadLetterService).getDltRecordById("1");
    }
}
