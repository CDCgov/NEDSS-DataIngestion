package	com.vault.utils;

import org.springframework.beans.factory.annotation.Value;
import  org.springframework.boot.context.properties.ConfigurationProperties;
import  org.springframework.context.annotation.Configuration;

import  org.apache.http.HttpResponse;
import  org.apache.http.HttpEntity;
import  org.apache.http.impl.client.CloseableHttpClient;
import  org.apache.http.impl.client.HttpClients; 
import  org.apache.http.client.methods.HttpGet;
import  org.apache.http.client.methods.HttpPost;
import  org.apache.http.impl.client.HttpClientBuilder;
import  org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import  org.apache.http.conn.ssl.SSLContextBuilder;
import  org.apache.http.conn.ssl.TrustSelfSignedStrategy;

import  java.security.KeyStore;

import  com.google.gson.Gson;

import	java.util.Date;
import	java.util.HashMap;

import  java.io.BufferedReader;
import  java.io.InputStream;
import  java.io.InputStreamReader;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

@Configuration
@ConfigurationProperties(prefix="hashivault")
public class VaultValuesResolver {
	private static Logger logger = LoggerFactory.getLogger(VaultValuesResolver.class);

	private static final String PROPERTY_PKCS12_CERTIFICATE = "hvappname.p12";
	private static final String PROPERTY_CERTIFICATE_STORE_TYPE = "PKCS12";
	private static final String PROPERTY_CERTIFICATE_TOKEN = "apppwd";
	private static final String PROPERTY_HASHI_VAULT_AUTH_HEADER = "X-Vault-Namespace";
    private static final String PROPERTY_HASHI_VAULT_AUTH_NAMESPACE = "namespace";
    private static final String PROPERTY_HASHI_VAULT_SECRETS_NAMESPACE = "namespace/appname/";
	private static final String PROPERTY_HASHI_VAULT_TOKEN_HEADER = "X-Vault-Token";
    private static final String VAULT_PROPERTY_PREFIX = "vault_";

	// 50 minutes = 50 * 60 * 1000 = 3000000
	private static final long TOKEN_VALIDITY_TIME_IN_MILLISECONDS = 3000000;
    
    private static Gson gson = new Gson();
    
	private static HashMap<String, String> knownSecrets = null;
	private static VaultValuesResolver instance = null;
	
	private CloseableHttpClient httpClientForSecrets = null;

	private String savedClientToken = null;
	private long tokenTime = 0L;
	
	private String authendpoint;
    private String secretsendpoint;
    private String encryptendpoint;

    @Value("${hashivault.enabled}")
    private boolean bHashiVaultEnabled = true;

	public static VaultValuesResolver getInstance() {
		return instance;
	}
	
	public static String getVaultKeyValue(String vaultLookupKey)
	{
		String retValue = vaultLookupKey;
		
		if(null == instance) return retValue;
		
		if((null != vaultLookupKey) 
			&& (vaultLookupKey.length() > 0)
			&& (vaultLookupKey.startsWith(VAULT_PROPERTY_PREFIX))) {			
				String vaultKey = vaultLookupKey.substring(VAULT_PROPERTY_PREFIX.length());
				retValue = knownSecrets.get(vaultKey);
		}
		
		return retValue;
	}
	
	public VaultValuesResolver() {
		instance = this;
	}

    public void setAuthendpoint(String authendpoint) {
        this.authendpoint = authendpoint;
    }
	
	public void setSecretsendpoint(String secretsendpoint) {
		this.secretsendpoint = secretsendpoint;
		
		if(null != knownSecrets) {
			return;
		}

        if( !bHashiVaultEnabled ) {
            knownSecrets = getDefaulSecrets();
            return;
        }

		try {
			knownSecrets = getSecrets();
		}
		catch(Exception e)
		{
			logger.error("Error observed while obtaining the secrets", e);
		}
	}
	
    private HashMap<String, String> getSecrets() throws Exception {
        SecretsReplyHolder srh = null;
        
        if(null == httpClientForSecrets) {
        	httpClientForSecrets = (secretsendpoint.startsWith("http:") ? initNonSslClient() : initSslClient());
        }

        String returnedClientToken = getClientToken();

        try {
        	logger.info("Will obtain secrets, url = {}", secretsendpoint);

            HttpGet secretsRequest = new HttpGet(secretsendpoint);
            secretsRequest.addHeader(PROPERTY_HASHI_VAULT_AUTH_HEADER, PROPERTY_HASHI_VAULT_SECRETS_NAMESPACE);
            secretsRequest.addHeader(PROPERTY_HASHI_VAULT_TOKEN_HEADER, returnedClientToken);

            HttpResponse response = httpClientForSecrets.execute(secretsRequest);
            String resString = processResponse(response);
            logger.info("!!! resString = {}", resString);
            
            srh = gson.fromJson(resString, SecretsReplyHolder.class);
            
            logger.info("Processed hashi corp reply for secrets");
        } catch (Exception e) {
        	logger.error("Hashi vault store error while getting secrets, will retry later, url = {}", secretsendpoint, e);
            throw e;
        }

        return srh.data.data;
    }
    
    private String processResponse(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();

        String statusMsg = response.getStatusLine().getReasonPhrase();
        if (statusCode != 200) {
            String msg = String.format("HashiCorp vault returned non 200 http status during auth, statusCode = %d, statusMsg = %s",
                                statusCode,
                                statusMsg);
            throw new Exception(msg);
        }

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }

        String resString = sb.toString();
        is.close();

        return resString;
    }    
    
	private String getFixedClientToken() throws Exception {
		return "nj101";
	}    

    private String getClientToken() throws Exception {
		if( secretsendpoint.startsWith("http:") ) {
			return getFixedClientToken();
		}

        String existingClientToken = null;
        AuthReplyHolder arh = null;

        synchronized( this ) {
            existingClientToken = savedClientToken;
        }

        Date now = new Date();

        boolean bHaveValidToken = ((tokenTime > 0) && ((now.getTime() - tokenTime) < TOKEN_VALIDITY_TIME_IN_MILLISECONDS));
        if ( bHaveValidToken ) {
            long elapsedMinutes = (now.getTime() - tokenTime) / 60000;
            logger.info("Will use existing token {}, elapsedMinutes = {}",
                            existingClientToken,
                            elapsedMinutes);

            return existingClientToken;
        }

        if (null == httpClientForSecrets) {
			httpClientForSecrets = initSslClient();
        }

        try {
            HttpPost httpAuthRequest = new HttpPost(authendpoint);
            httpAuthRequest.addHeader(PROPERTY_HASHI_VAULT_AUTH_HEADER, PROPERTY_HASHI_VAULT_AUTH_NAMESPACE);

            HttpResponse response = httpClientForSecrets.execute(httpAuthRequest);
            String resString = processResponse(response);
            arh = gson.fromJson(resString, AuthReplyHolder.class);
        } catch (Exception e) {
			logger.error("Hashi vault error during signon", e);
            throw new Exception(e);
        }

        String newClientToken = arh.auth.client_token;
        Date now1 = new Date();
        tokenTime = now1.getTime();

        logger.info("Obtained client token = {}, tokenTime = {}, HashiCorp service url = {}",
                        newClientToken,
                        tokenTime,
                        authendpoint);

        return newClientToken;
    }
	
	private CloseableHttpClient initSslClient() throws Exception {
		CloseableHttpClient httpClient = null;

		try {
			InputStream certStream = VaultValuesResolver.class.getClassLoader().getResourceAsStream(PROPERTY_PKCS12_CERTIFICATE);
			KeyStore keyStore = KeyStore.getInstance(PROPERTY_CERTIFICATE_STORE_TYPE);
			keyStore.load(certStream, PROPERTY_CERTIFICATE_TOKEN.toCharArray());

			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
					new SSLContextBuilder()
							.loadTrustMaterial(null, new TrustSelfSignedStrategy())
							.loadKeyMaterial(keyStore, PROPERTY_CERTIFICATE_TOKEN.toCharArray()).build());

			httpClient = HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();

			logger.info("Initialized ssl client to interface with HashiCorp vault");

			return httpClient;
		} catch (Exception e) {
			logger.error("SSL store error", e);
			throw new Exception(e);
		}
	}
	
	private CloseableHttpClient initNonSslClient() throws Exception {
        CloseableHttpClient httpClient = null;

        try {
            httpClient = HttpClients.createDefault();
            logger.info("Initialized non-ssl client to interface with HashiCorp vault");
            return httpClient;
        }
        catch (Exception e) {
        	logger.error("Non-ssl client initialization error", e);
            throw e;
        }
    }

    private HashMap<String, String> getDefaulSecrets() {
        HashMap<String, String> localSecrets = new HashMap<String, String>();
        return localSecrets;
    }
}
