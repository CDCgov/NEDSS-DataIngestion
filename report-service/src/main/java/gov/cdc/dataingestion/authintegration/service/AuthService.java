package gov.cdc.dataingestion.authintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.IOException;

import com.amazonaws.services.certificatemanager.AWSCertificateManagerClientBuilder;
import com.amazonaws.services.certificatemanager.AWSCertificateManager;
import com.amazonaws.services.certificatemanager.model.ListCertificatesRequest;
import com.amazonaws.services.certificatemanager.model.ListCertificatesResult;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;

import com.amazonaws.AmazonClientException;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@EnableScheduling
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Value("${auth.url}")
    private String url;

    // TODO: Decide and move this to singleton if needed
    private String token;

    //@EventListener(ApplicationReadyEvent.class)
    public void generateAuthTokenDuringStartup() {
        System.err.println("Generating Auth Token during startup...");
        generateToken();
        System.err.println("Token is startup..." + token);
    }
    //@Scheduled(initialDelay = 0, fixedRate = 60*60*1000)
    @Scheduled(initialDelay = 0, fixedRate = 15*1000)
    public void generateAuthTokenScheduled() {
        System.err.println("Generating Auth Token scheduled...");
//        AWSCredentials credentials = null;
//        try {
//            logger.info("Loading from creds file...");
//            credentials = new ProfileCredentialsProvider().getCredentials();
//        }
//        catch (Exception ex) {
//            throw new AmazonClientException("Cannot load the credentials from file.", ex);
//        }
//
//        // Create a client.
//        AWSCertificateManager client = AWSCertificateManagerClientBuilder.standard()
//                .withRegion(Regions.US_EAST_1)
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .build();
//
//        // Create a request object and set the parameters.
//        ListCertificatesRequest req = new ListCertificatesRequest();
//        List<String> Statuses = Arrays.asList("ISSUED", "EXPIRED", "PENDING_VALIDATION", "FAILED");
//        req.setCertificateStatuses(Statuses);
//        req.setMaxItems(10);
//
//        // Retrieve the list of certificates.
//        ListCertificatesResult result = null;
//        try {
//            result = client.listCertificates(req);
//        }
//        catch (Exception ex)
//        {
//            logger.error("exception in aws..." + ex);
//            throw ex;
//        }
//
//        // Display the certificate list.
//        System.out.println("Response from aws is...." +"\n" + result + "\n");

        // Move this to a different function and add an EventListener and call the function twice
        // during start up and scheduled
        generateToken();
        System.err.println("Token is scheduled..." + token);

    }

    private void generateToken() {
        try {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

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

            //CloseableHttpClient httpsClient = HttpClients.createDefault();
            CloseableHttpClient httpsClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();


            HttpGet getRequest = new HttpGet("https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/token");
//            HttpPost postRequest = new HttpPost("url");
            Header authHeader = new BasicScheme(StandardCharsets.UTF_8).authenticate(credentials, getRequest, null);
//            CloseableHttpResponse response = httpsClient.execute(getRequest);
            //postRequest.addHeader(authHeader);
            CloseableHttpResponse response = httpsClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            System.err.println("status code from token service is..." + statusCode);
            HttpEntity httpEntity = response.getEntity();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(EntityUtils.toString(httpEntity));

//            InputStream apiContent = response.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(apiContent));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            System.out.println("Line is..." + stringBuilder.toString());
//            while ((line = reader.readLine()) != null) {
//                System.out.println("Line is..." + line);
//                stringBuilder.append(line);
//            }

            token = node.get("token").asText();
            System.err.println("response from token service is..." + token);

        } catch (Exception e) {
            System.err.println("Exception occurred while establishing connection to NBS Auth Service: " + e);
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        return token;
    }
}
