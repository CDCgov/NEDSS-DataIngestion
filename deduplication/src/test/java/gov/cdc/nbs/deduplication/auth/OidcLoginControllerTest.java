package gov.cdc.nbs.deduplication.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import gov.cdc.nbs.deduplication.auth.model.OidcLoginRequest;
import gov.cdc.nbs.deduplication.auth.model.OidcLoginResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OidcLoginControllerTest {

  @Mock
  private RestClient client;

  @InjectMocks
  private OidcLoginController controller;

  @Test
  void should_return_token() {

    // mock
    RestClient.RequestBodyUriSpec bodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
    RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    when(client.post()).thenReturn(bodyUriSpec);
    when(bodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
    when(bodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodyUriSpec);
    ArgumentCaptor<LinkedMultiValueMap<String, String>> captor = ArgumentCaptor.forClass(LinkedMultiValueMap.class);
    when(bodyUriSpec.body(captor.capture())).thenReturn(bodyUriSpec);
    when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(OidcLoginResponse.class)).thenReturn(new OidcLoginResponse("token value"));

    // act
    OidcLoginResponse response = controller.login(new OidcLoginRequest(
        "clientId",
        "clientSecret",
        "username",
        "password"));

    // verify
    assertThat(response.accessToken()).isEqualTo("token value");
    assertThat(captor.getValue()).containsExactly(
        entry("grant_type", List.of("password")),
        entry("client_id", List.of("clientId")),
        entry("client_secret", List.of("clientSecret")),
        entry("username", List.of("username")),
        entry("password", List.of("password")));
  }
}
