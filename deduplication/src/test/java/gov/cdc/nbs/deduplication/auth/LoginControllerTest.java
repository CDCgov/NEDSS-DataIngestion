package gov.cdc.nbs.deduplication.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import gov.cdc.nbs.deduplication.auth.model.LoginRequest;
import gov.cdc.nbs.deduplication.auth.model.LoginResponse;
import gov.cdc.nbs.deduplication.config.auth.nbs.NbsTokenConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsToken;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

  @Mock
  private SecurityProperties securityProperties;

  @Mock
  private NbsUserDetailsService userService;

  @Mock
  private NbsTokenCreator tokenCreator;

  @InjectMocks
  private LoginController controller;

  @Test
  void shouldLogin() {
    // mock
    when(userService.loadUserByUsername("username")).thenReturn(new NbsUserDetails(
        0,
        "username",
        "John",
        "Doe",
        Set.of(new SimpleGrantedAuthority("VIEW-PATIENT")),
        true));

    when(tokenCreator.forUser("username")).thenReturn(new NbsToken("myTokenValue"));

    HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);

    // act
    LoginResponse response = controller.login(new LoginRequest("username", "password"), mockResponse);

    // assert
    assertThat(response).isNotNull();
    assertThat(response.username()).isEqualTo("username");
    assertThat(response.token()).isEqualTo("myTokenValue");
  }

}
