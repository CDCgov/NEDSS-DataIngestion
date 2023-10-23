package gov.cdc.dataingestion.registration.controller;

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

    private String USERNAME_PWD_REQ_MSG="Username and/or password are required.";
    private String USERNAME_PWD_MIN_LENGTH_MSG="The username and password must be eight characters in length.";
    private String USER_CREATED_MSG="User Created Successfully.";
    private String USER_ALREADY_EXIST_MSG="User already exists.Please choose another.";

    @Test
    void createUserTestSuccess() throws Exception {
        when(registrationService.createUser("newuser123", "password123")).thenReturn(true);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser123")
                        .param("password", "password123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser123"), eq("password123"));
        Assertions.assertEquals(USER_CREATED_MSG, result.getResponse().getContentAsString());
    }

    @Test
    void createUserTestSuccessSaveReturnFalse() throws Exception {
        when(registrationService.createUser("newuser123", "password123")).thenReturn(false);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser123")
                        .param("password", "password123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser123"), eq("password123"));
        Assertions.assertEquals(USER_ALREADY_EXIST_MSG, result.getResponse().getContentAsString());
    }

    @Test
    void createUserTestSuccessSaveReturnBadRequest() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "")
                        .param("password", "")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(USERNAME_PWD_REQ_MSG, result.getResponse().getContentAsString());
    }
    @Test
    void createUserTestSuccessUsernameMinLength() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser")
                        .param("password", "password456")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(USERNAME_PWD_MIN_LENGTH_MSG, result.getResponse().getContentAsString());
    }
    @Test
    void createUserTestSuccessPasswordMinLength() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser456")
                        .param("password", "pwd123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(USERNAME_PWD_MIN_LENGTH_MSG, result.getResponse().getContentAsString());
    }
}
