package gov.cdc.dataprocessing.service.implementation.cache;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ITokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class CacheApiService implements ICacheApiService {

    @Value("${cache.srte.cacheString}")
    protected String srteCacheString;

    @Value("${cache.srte.cacheObject}")
    protected String srteCacheObject;

    @Value("${cache.srte.cacheContain}")
    protected String srteCacheContain;

    @Value("${cache.odse.localId}")
    protected String odseLocalId;

    @Value("${cache.clientId}")
    private String clientId;

    @Value("${cache.secret}")
    private String clientSecret;

    private final ITokenService tokenService;

    private final Gson gson;

    private final RestTemplate restTemplate = new RestTemplate();

    public CacheApiService(ITokenService tokenService) {
        this.tokenService = tokenService;
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }

    public String getSrteCacheString(String objectName, String key) {
        var param = new HashMap<String, String>();

        param.put("key", key);
        return callEndpoint(srteCacheString + "/" + objectName, param, tokenService.getToken(), String.class);
    }

    public String getSrteCacheObject(String objectName, String key) {
        var param = new HashMap<String, String>();
        param.put("key", key);
        return callEndpoint(srteCacheObject + "/" + objectName, param, tokenService.getToken(), String.class);
    }

    public Boolean getSrteCacheBool(String objectName, String key) {
        var param = new HashMap<String, String>();
        param.put("key", key);
        return callEndpoint(srteCacheContain + "/" + objectName, param, tokenService.getToken(), Boolean.class);
    }

    public String getOdseLocalId(String objectName, boolean geApplied) {
        var param = new HashMap<String, String>();
        param.put("localIdClass", objectName);
        param.put("geApplied", String.valueOf(geApplied));
        return callEndpoint(odseLocalId, param, tokenService.getToken(), String.class);
    }

    protected  <T> T callEndpoint(String endpoint, Map<String, String> params, String token, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add("clientid", clientId);
        headers.add("clientsecret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            multiValueParams.add(entry.getKey(), entry.getValue());
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParams(multiValueParams)
                .build()
                .toUri();

        ResponseEntity<T> response = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);

        return response.getBody();
    }
}
