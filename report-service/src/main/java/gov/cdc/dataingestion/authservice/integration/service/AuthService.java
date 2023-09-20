package gov.cdc.dataingestion.authservice.integration.service;

import jakarta.annotation.PostConstruct;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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

    @Value("${auth.roles-url}")
    private String rolesUrl;

    private String token;
    private String refreshToken;
    private static final String AUTH_ELR_CLAIM = "ELR Importer";
    private static final String AUTH_ECR_CLAIM = "ECR Importer";


    @PostConstruct
    public void generateAuthTokenDuringStartup() {
        CloseableHttpResponse apiSignOnResponse = getAuthApiResponse(getSignOnUrl(), "");
        getTokenFromApiResponse(apiSignOnResponse);

        CloseableHttpResponse apiRolesResponse = getAuthApiResponse(rolesUrl, token);
        getAuthRolesFromApiResponse(apiRolesResponse);
    }

    // Need to generate token before it's expiry (1 hour) and that is
    // why this method is scheduled every 55 minutes
    @Scheduled(fixedRate = 55*60*1000)
    public void refreshAuthTokenScheduled() {
        CloseableHttpResponse apiRefreshTokenResponse = getAuthApiResponse(tokenUrl, refreshToken);
        getTokenFromApiResponse(apiRefreshTokenResponse);

    }

    private CloseableHttpResponse getAuthApiResponse(String url, String token) {
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpClient = buildHttpClient();
            if(httpClient != null) {

                HttpPost postRequest = new HttpPost(url);
                if(!token.isEmpty() && token.length() > 0) {
                    postRequest.addHeader("Auth-Token", token);
                }
                response = httpClient.execute(postRequest);
            }
            else {
                logger.error("Http client returned as null.");
            }
        } catch (IOException e) {
            logger.error("Exception occurred while generating auth token: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return response;
    }


    private void getAuthRolesFromApiResponse(CloseableHttpResponse apiRolesResponse) {
        if(apiRolesResponse != null) {
            try {
                HttpEntity httpEntity = apiRolesResponse.getEntity();
                String responseAuthRolesString = EntityUtils.toString(httpEntity, "UTF-8");
                JSONObject jsonObj = new JSONObject(responseAuthRolesString);
                String authRole = jsonObj.getString("roles");
                if(authRole == null || authRole.isEmpty()) {
                    logger.error("Auth role is not defined, nothing to authorize.");
                }
                else {
                    logger.info("User auth role from the API is: {}", authRole);

                    boolean isUserAllowedToLoadElrData = authRole.contains(AUTH_ELR_CLAIM) || authRole.contains("allow_elr_data_loading");
                    boolean isUserAllowedToLoadEcrData = authRole.contains(AUTH_ECR_CLAIM) || authRole.contains("allow_ecr_data_loading");

                    logger.info("Is user allowed to load ELR data: {}", isUserAllowedToLoadElrData);
                    logger.info("Is user allowed to load ECR data: {}", isUserAllowedToLoadEcrData);
                }
            } catch (IOException e) {
                logger.error("Exception occurred while parsing token: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        else {
            logger.error("Auth API response is null.");
        }
    }

    private void getTokenFromApiResponse(CloseableHttpResponse apiResponse) {
        if(apiResponse != null) {
            try {
                HttpEntity httpEntity = apiResponse.getEntity();
                String responseTokensString = EntityUtils.toString(httpEntity, "UTF-8");
                JSONObject jsonObj = new JSONObject(responseTokensString);
                token = jsonObj.getString("token");
                refreshToken = jsonObj.getString("refreshToken");
            } catch (IOException e) {
                logger.error("Exception occurred while generating/refreshing token: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        else {
            logger.error("Auth API response is null.");
        }
    }

    private CloseableHttpClient buildHttpClient() {
        CloseableHttpClient httpsClient;

        try {
            // TODO: Fix the certificate issue with the NBS AUth service in AWS
            //  and change this code as like we are using in Data Ingestion CLI
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
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException occurred while building http client: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException occurred while building http client: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.error("KeyStoreException occurred while building http client: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return httpsClient;
    }

    private String getSignOnUrl() {
        String encodedUsername = new String(Base64.getEncoder().encode(nbsUsername.getBytes()));
        String encodedPassword = new String(Base64.getEncoder().encode(nbsPassword.getBytes()));

        return signOnUrl + "?user=" + encodedUsername + "&password=" + encodedPassword;
    }

    public String getToken() {
        return token;
    }
}
