package gov.cdc.dataingestion.registration.controller;

import gov.cdc.dataingestion.deadletter.controller.ElrDeadLetterController;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.registration.service.RegistrationService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(RegisterController.class)
@EnableConfigurationProperties(RsaKeyProperties.class)
public class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void createUserTestSuccess() throws Exception {
        when(registrationService.createUser("u", "p")).thenReturn(true);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "u")
                        .param("password", "p")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("u"), eq("p"));
        Assertions.assertEquals("\"CREATED\"", result.getResponse().getContentAsString());

    }

    @Test
    void createUserTestSuccessSaveReturnFalse() throws Exception {
        when(registrationService.createUser("u", "p")).thenReturn(false);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "u")
                        .param("password", "p")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("u"), eq("p"));
        Assertions.assertEquals("\"NOT_ACCEPTABLE\"", result.getResponse().getContentAsString());
    }

    @Test
    void createUserTestSuccessSaveReturnBadRequest() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "")
                        .param("password", "")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("\"BAD_REQUEST\"", result.getResponse().getContentAsString());
    }
}
