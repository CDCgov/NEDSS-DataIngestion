package gov.cdc.nbs.deduplication.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import gov.cdc.nbs.deduplication.auth.model.OidcLoginRequest;
import gov.cdc.nbs.deduplication.auth.model.OidcLoginResponse;

@RestController
@RequestMapping("/login")
@Profile("dev")
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
public class OidcLoginController {

  private final RestClient client;

  public OidcLoginController(@Qualifier("oidcRestClient") RestClient client) {
    this.client = client;
  }

  @PostMapping
  OidcLoginResponse login(@RequestBody OidcLoginRequest request) {
    LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "password");
    formData.add("client_id", request.clientId());
    formData.add("client_secret", request.clientSecret());
    formData.add("username", request.username());
    formData.add("password", request.password());

    return client
        .post()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(formData)
        .retrieve()
        .body(OidcLoginResponse.class);

  }

}
