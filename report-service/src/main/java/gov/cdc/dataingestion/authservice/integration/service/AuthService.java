package gov.cdc.dataingestion.authservice.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.security.Key;
import java.util.Base64;

@Service
@EnableScheduling
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.username}")
    private String nbsUsername;

    @Value("${auth.password}")
    private String nbsPassword;

    @Value("${auth.sign-on-url}")
    private String signOnUrl;

    @Value("${auth.token-url}")
    private String tokenUrl;

    @Value("${auth.salt-for-algorithm}")
    private String saltForAlgorithm;

    private String token;

    private static final String AUTH_ELR_CLAIM = "ELR Importer";
    private static final String AUTH_ECR_CLAIM = "ECR Importer";

    private String encodedSignOnUrl;


    @PostConstruct
    public void generateAuthTokenDuringStartup() {
        encodedSignOnUrl = getSignOnUrl();
        generateToken(encodedSignOnUrl);

        String authRoleName = getAuthRoleClaim(token);
        if(authRoleName == null || authRoleName.isEmpty()) {
            logger.error("Auth roles not defined, nothing to authorize.");
            return;
        }

        logger.info("Auth role claim is: " + authRoleName);

        boolean isUserAllowedToLoadElrData = authRoleName.contains(AUTH_ELR_CLAIM);
        boolean isUserAllowedToLoadEcrData = authRoleName.contains(AUTH_ECR_CLAIM);

        logger.info("Is user allowed to load ELR data: " + isUserAllowedToLoadElrData);
        logger.info("Is user allowed to load ECR data: " + isUserAllowedToLoadEcrData);
    }

    // Need to generate token before it's expiry (1 hour) and that is
    // why this method is scheduled every 55 minutes
    @Scheduled(fixedRate = 55*60*1000)
    public void generateAuthTokenScheduled() {
        generateToken(tokenUrl);
    }

    private void generateToken(String url) {
        try {
            CloseableHttpClient httpClient = buildHttpClient();
            if(httpClient != null) {
                CloseableHttpResponse response;
                if(url.contains("token")) {
                    HttpGet getRequest = new HttpGet(url);
                    getRequest.addHeader("Auth-Token", token);
                    response = httpClient.execute(getRequest);
                }
                else {
                    HttpPost postRequest = new HttpPost(url);
                    response = httpClient.execute(postRequest);
                }
                int statusCode = response.getStatusLine().getStatusCode();

                // TODO: Lookup the right code for the token expiration error.
                //  Need to create a story to add this code logic in the Auth service
                if(statusCode == 503) {
                    logger.info("Token expired. Signing on again...");
                    generateToken(encodedSignOnUrl);
                }

                HttpEntity httpEntity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(EntityUtils.toString(httpEntity));
                token = node.get("token").asText();
            }
            else {
                logger.error("Http client returned as null.");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while generating auth token: " + e);
            throw new RuntimeException(e);
        }
    }

    private CloseableHttpClient buildHttpClient() {
        CloseableHttpClient httpsClient;
        try {
            // TODO: Fix the certificate issue with the NBS AUth service in AWS
            //  And change this code as like we are using in Data Ingestion CLI
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory> create()
                            .register("https", sslsf)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();

            BasicHttpClientConnectionManager connectionManager =
                    new BasicHttpClientConnectionManager(socketFactoryRegistry);

            httpsClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();
        } catch (Exception e) {
            logger.error("Exception occurred while building http client: " + e);
            throw new RuntimeException(e);
        }
        return httpsClient;
    }

    private String getAuthRoleClaim(String token) {
        Key hmacKey = new SecretKeySpec(saltForAlgorithm.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        Claims jwtClaims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String AUTH_ROLE_CLAIM = "auth_role_nm";
        return (String) jwtClaims.get(AUTH_ROLE_CLAIM);
    }

    private String getSignOnUrl() {
        String encodedUsername = new String(Base64.getEncoder().encode(nbsUsername.getBytes()));
        String encodedPassword = new String(Base64.getEncoder().encode(nbsPassword.getBytes()));

        String nbsSignOnEncodedUrl = signOnUrl + "?user=" + encodedUsername + "&password=" + encodedPassword;
        return nbsSignOnEncodedUrl;
    }

    public String getToken() {
        return token;
    }
}
