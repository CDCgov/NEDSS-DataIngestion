package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ElrDeadLetterController.class)
public class ElrDeadLetterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElrDeadLetterService elrDeadLetterService;

    @Test
    public void testGetAllNewErrorMessageSuccess() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports-dlt/get-error-messages"))
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
    public void testGetErrorMessageSuccess() throws Exception {
        ElrDeadLetterDto dto1 = new ElrDeadLetterDto(
                "1", "topic-a", "error stack trace", 1, "ERROR", "system", "system"
        );

        when(elrDeadLetterService.getDltRecordById("1")).thenReturn(dto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports-dlt/get-message")
                        .param("id", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessageId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessageSource").value("topic-a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorStackTrace").value("error stack trace"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dltOccurrence").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dltStatus").value("ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("system"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedBy").value("system"));

    }

    @Test
    public void testMessageReInject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports-dlt/update-inject")
                .param("id", "1")
                .contentType("text/plain")
                .content("HL7 message"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(elrDeadLetterService).updateAndReprocessingMessage(eq("1"), eq("HL7 message"));
    }
}
