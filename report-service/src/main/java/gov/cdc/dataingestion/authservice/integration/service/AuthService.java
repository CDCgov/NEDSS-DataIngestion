package gov.cdc.dataingestion.authservice.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    private final String encodedSignOnUrl =  getSignOnUrl();


    @EventListener(ApplicationReadyEvent.class)
    public void generateAuthTokenDuringStartup() {
        System.err.println("Generating Auth Token during startup...");
        generateToken(encodedSignOnUrl);
        System.err.println("Token at startup..." + token);

        String authRoleName = getAuthRoleClaim(token);
        if(authRoleName == null || authRoleName.isEmpty()) {
            logger.error("Auth roles not defined, nothing to authorize.");
            return;
        }

        logger.info("Auth role claim is: " + authRoleName);

        boolean isUserAllowedToLoadElrData = authRoleName.contains(AUTH_ELR_CLAIM);
        boolean isUserAllowedToLoadEcrData = authRoleName.contains(AUTH_ECR_CLAIM);

        logger.info("Is allowed to load ELR data: " + isUserAllowedToLoadElrData);
        logger.info("Is allowed to load ECR data: " + isUserAllowedToLoadEcrData);
    }

    // Need to generate token before it's expiry and that is why 55 minutes
    //@Scheduled(initialDelay = 0, fixedRate = 55*60*1000)
    @Scheduled(initialDelay = 0, fixedRate = 15*1000)
    public void generateAuthTokenScheduled() {
        System.err.println("Generating Auth Token scheduled...");
        getNewToken();
        System.err.println("Token from scheduled..." + token);
    }

    private void generateToken(String url) {
        try {
            CloseableHttpClient httpsClient = buildHttpClient();
            if(httpsClient != null) {
                HttpPost postRequest = new HttpPost(url);
                if(url.contains("token")) {
                    postRequest.addHeader("Auth-Token", token);
                }
                CloseableHttpResponse response = httpsClient.execute(postRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                System.err.println("status code from token service is..." + statusCode);

                // TODO: Lookup the right code for the token expiration error. Check if this is really needed
                if(statusCode == 503) {
                    System.err.println("Token expired. Signing on again...");
                    generateToken(encodedSignOnUrl);
                }

                HttpEntity httpEntity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(EntityUtils.toString(httpEntity));
                token = node.get("token").asText();
                System.err.println("response from token service is..." + token);
            }
            else {
                logger.error("Https client returned as null.");
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while establishing connection to NBS Auth Service: " + e);
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
            System.err.println("Exception occurred while building HTTPS client: " + e);
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

    private void getNewToken() {
        generateToken(tokenUrl);
    }

    private String getSignOnUrl() {
        String encodedUsername = new String(Base64.getEncoder().encode(nbsUsername.getBytes()));
        String encodedPassword = new String(Base64.getEncoder().encode(nbsPassword.getBytes()));

        String nbsSignOnEncodedUrl = signOnUrl + "?user=" + encodedUsername + "&password=" + encodedPassword;
        logger.info("Formed sign on URL is: " + nbsSignOnEncodedUrl);
        return nbsSignOnEncodedUrl;
    }

    public String getToken() {
        return token;
    }
}
