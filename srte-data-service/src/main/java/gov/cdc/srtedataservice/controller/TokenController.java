package gov.cdc.srtedataservice.controller;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController

public class TokenController {
    @Value("${auth.token-uri}")
    String authTokenUri;
    private RestTemplate restTemplate;
    public TokenController(@Qualifier("restTemplate") RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }
    @Bean(name = "restTemplate")
    public static RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Operation(
            summary = "Get Access Token",
            description = "Generates an access token using client credentials. The client ID and client secret must be provided in the request headers.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client ID for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @PostMapping("/api/auth/token")
    public ResponseEntity<String> token(@RequestHeader("clientid") String clientId, @RequestHeader("clientsecret") String clientSecret) {
        String accessToken = null;
        String postBody = "grant_type=client_credentials" +
                "&client_id=" + clientId
                + "&client_secret=" + clientSecret;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(postBody, headers);
        try{
            ResponseEntity<String> exchange =
                    restTemplate.exchange(
                            authTokenUri,
                            HttpMethod.POST,
                            request,
                            String.class);
            String response = exchange.getBody();
            JsonElement jsonElement = JsonParser.parseString(response);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            accessToken = jsonObject.get("access_token").getAsString();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        return ResponseEntity.ok(accessToken);
    }
}
