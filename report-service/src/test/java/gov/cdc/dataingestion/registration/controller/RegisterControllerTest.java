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

    private static final String userNamePwdReqMsg="Username and/or password are required.";
    private static final String userNameMinLength="The username must be atleast six characters in length.";
    private static final String pwdMinLength="The password must be atleast eight characters in length.";
    private static final String userCreatedMsg="User Created Successfully.";
    private static final String userAlreadyExistMsg="User already exists.Please choose another.";

    @Test
    void createUserTestSuccess() throws Exception {
        when(registrationService.createUser("newuser", "password123")).thenReturn(true);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser"), eq("password123"));
        Assertions.assertEquals(userCreatedMsg, result.getResponse().getContentAsString());
    }

    @Test
    void createUserTestSuccessSaveReturnFalse() throws Exception {
        when(registrationService.createUser("newuser", "password123")).thenReturn(false);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser"), eq("password123"));
        Assertions.assertEquals(userAlreadyExistMsg, result.getResponse().getContentAsString());
    }

    @Test
    void createUserTestSuccessSaveReturnBadRequest() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "")
                        .param("password", "")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(userNamePwdReqMsg, result.getResponse().getContentAsString());
    }
    @Test
    void createUserTestSuccessUsernameMinLength() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "user")
                        .param("password", "password456")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(userNameMinLength, result.getResponse().getContentAsString());
    }
    @Test
    void createUserTestSuccessPasswordMinLength() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newuser")
                        .param("password", "pwd123")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(pwdMinLength, result.getResponse().getContentAsString());
    }
}
