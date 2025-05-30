package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.service.interfaces.cache.ITokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenService implements ITokenService {
    @Value("${cache.token}")
    private String tokenEndpoint;

    @Value("${cache.clientId}")
    private String clientId;

    @Value("${cache.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    private String cachedToken;
    private long lastFetchedTime = 0; // epoch millis
    private static final long TOKEN_VALIDITY_MILLIS = 20 * 60 * 1000; // 20 minutes

    public TokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public synchronized String getToken() {
        long now = System.currentTimeMillis();

        if (cachedToken == null || now - lastFetchedTime >= TOKEN_VALIDITY_MILLIS) {
            cachedToken = fetchNewToken();
            lastFetchedTime = now;
        }

        return cachedToken;
    }

    private String fetchNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("clientid", clientId);
        headers.add("clientsecret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, entity, String.class);

        return response.getBody();
    }
}