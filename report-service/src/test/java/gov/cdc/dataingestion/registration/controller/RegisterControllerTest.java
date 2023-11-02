package gov.cdc.dataingestion.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataingestion.registration.dto.RegistrationRequestDTO;
import gov.cdc.dataingestion.registration.service.RegistrationService;
import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import org.junit.jupiter.api.Assertions;
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
    void testCreateUserSuccess() throws Exception {
        String requestBody = "{\"username\":\"newuser\", \"password\":\"password123\"}";

        when(registrationService.createUser("newuser", "password123")).thenReturn(true);

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser"), eq("password123"));
        Assertions.assertEquals(userCreatedMsg, result.getResponse().getContentAsString());
    }

    @Test
    void testCreateUserSuccessSaveReturnFalse() throws Exception {
        String requestBody = "{\"username\":\"newuser\", \"password\":\"password123\"}";

        when(registrationService.createUser("newuser", "password123")).thenReturn(false);

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(registrationService).createUser(eq("newuser"), eq("password123"));
        Assertions.assertEquals(userAlreadyExistMsg, result.getResponse().getContentAsString());
    }

    @Test
    void testCreateUserSuccessSaveReturnBadRequest() throws Exception {
        String requestBody = "{\"username\":\"\", \"password\":\"\"}";

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(userNamePwdReqMsg, result.getResponse().getContentAsString());
    }

    @Test
    void testCreateUserSuccessUsernameMinLength() throws Exception {
        String requestBody = "{\"username\":\"user\", \"password\":\"password456\"}";

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(userNameMinLength, result.getResponse().getContentAsString());
    }

    @Test
    void testCreateUserSuccessPasswordMinLength() throws Exception {
        String requestBody = "{\"username\":\"newuser\", \"password\":\"pwd123\"}";

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(pwdMinLength, result.getResponse().getContentAsString());
    }

    @Test
    void testCreateUserSuccessSpecialCharacterInPassword() throws Exception {
        String requestBody = "{\"username\":\"newuser123\", \"password\":\"pwd#123&\"}";

        when(registrationService.createUser("newuser123", "pwd#123&")).thenReturn(true);

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals(userCreatedMsg, result.getResponse().getContentAsString());
    }
}
