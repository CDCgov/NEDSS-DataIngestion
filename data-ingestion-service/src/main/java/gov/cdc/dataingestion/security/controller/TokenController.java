package gov.cdc.dataingestion.security.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TokenController {
    @Value("${auth.token-uri}")
    String authTokenUri;
    private final CustomMetricsBuilder customMetricsBuilder;
    private RestTemplate restTemplate;
    public TokenController( @Qualifier("restTemplate") RestTemplate restTemplate, CustomMetricsBuilder customMetricsBuilder) {
        this.restTemplate=restTemplate;
        this.customMetricsBuilder = customMetricsBuilder;
    }
    @Bean(name = "restTemplate")
    public static RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @PostMapping("/token")
    public ResponseEntity token(@RequestHeader("clientid") String clientId, @RequestHeader("clientsecret") String clientSecret) {
        log.info("Token URL : " + authTokenUri);
        String accessToken = null;
        String postBody = "grant_type=client_credentials" +
                "&client_id=" + clientId
                + "&client_secret=" + clientSecret;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(postBody, headers);
        try{
            customMetricsBuilder.incrementTokensRequested();
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
            log.error("Error message in token endpoint : " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        return ResponseEntity.ok(accessToken);
    }
}
