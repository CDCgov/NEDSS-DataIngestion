package gov.cdc.dataprocessing.service.implementation.cache;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import org.apache.kafka.common.protocol.types.Field;
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
    @Value("${cache.clientId}")
    private String clientId = "clientId";

    @Value("${cache.secret}")
    private String clientSecret = "clientSecret";

    @Value("${cache.srte.cacheString}")
    protected String srteCacheString;

    @Value("${cache.srte.cacheObject}")
    protected String srteCacheObject;

    @Value("${cache.srte.cacheContain}")
    protected String srteCacheContain;

    @Value("${cache.odse.localId}")
    protected String odseLocalId;



    private final Gson gson;

    private final RestTemplate restTemplate = new RestTemplate();

    public CacheApiService() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }

    public String getSrteCacheString(String objectName, String key) {
        var param = new HashMap<String, String>();
        param.put("key", key);
        return callEndpoint(srteCacheString + "/" + objectName, param, "token", String.class);
    }

    public String getSrteCacheObject(String objectName, String key) {
        var param = new HashMap<String, String>();
        param.put("key", key);
        return callEndpoint(srteCacheObject + "/" + objectName, param, "token", String.class);
    }

    public Boolean getSrteCacheBool(String objectName, String key) {
        var param = new HashMap<String, String>();
        param.put("key", key);
        return callEndpoint(srteCacheContain + "/" + objectName, param, "token", Boolean.class);
    }

    public Object getOdseLocalId(String objectName, boolean geApplied) {
        var param = new HashMap<String, String>();
        param.put("localIdClass", objectName);
        param.put("geApplied", String.valueOf(geApplied));
        return callEndpoint(odseLocalId, param, "token", Object.class);
    }

    protected  <T> T callEndpoint(String endpoint, Map<String, String> params, String token, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
       // headers.setBearerAuth(token);
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
