package gov.cdc.dataingestion.authintegration.service;

import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClients;
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

import java.util.Arrays;
import java.util.List;

@Service
@EnableScheduling
public class AuthService {
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${kafka.validation.topic}")
    private String username;

    @Value("${kafka.validation.topic}")
    private String password;

//    @Value("${auth.url}")
//    private String url;

    //@EventListener(ApplicationReadyEvent.class)
    @Scheduled(initialDelay = 0, fixedDelay = 10000) //fixedRate = 60*60*1000)
    public void generateAuthTokenScheduled() {
//        logger.info("Generating Auth Token after expiry..." + url);
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

        try {
            //UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

            CloseableHttpClient httpsClient = HttpClients.createDefault();
            HttpPost getRequest = new HttpPost("https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/token");
            CloseableHttpResponse response = httpsClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("status code from token service is..." + statusCode);
            logger.info("response from token service is..." + response);
        } catch (IOException e) {
            logger.error("Exception occurred while establishing connection to NBS Auth Service:" + e);
            throw new RuntimeException(e);
        }

    }
}
