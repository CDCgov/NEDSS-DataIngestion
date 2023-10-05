package gov.cdc.dataingestion.security.config;

import jakarta.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@ConfigurationProperties
public class RsaKeyProperties {
    private static Logger log = LoggerFactory.getLogger(RsaKeyProperties.class);
    @Value("${rsa.private-key-content}")
    private String privateKeyContent;

    @Value("${rsa.public-key-content}")
    private String publicKeyContent;

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    @Bean
    public RSAPrivateKey privateKey() {
        log.debug("Inside RsaKeyProperties privateKey");
        RSAPrivateKey privKey = null;
        try {
            String privateKey = privateKeyContent.replace(BEGIN_PRIVATE_KEY, "")
                    .replaceAll(LINE_SEPARATOR, "")
                    .replace(END_PRIVATE_KEY, "");
            byte[] encodedPrivateKey = DatatypeConverter.parseBase64Binary(privateKey);
            privKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
        } catch (Exception e) {
            log.error("Error in privateKey:"+e.getMessage());
        }
        return privKey;
    }

    @Bean
    public RSAPublicKey publicKey() {
        log.debug("Inside RsaKeyProperties publicKey");
        RSAPublicKey pubKey = null;
        try {
            String publicKeyPEMStr = publicKeyContent.replace(BEGIN_PUBLIC_KEY, "")
                    .replaceAll(LINE_SEPARATOR, "")
                    .replace(END_PUBLIC_KEY, "");
            byte[] encoded = DatatypeConverter.parseBase64Binary(publicKeyPEMStr.trim());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("Error in publicKey:"+e.getMessage());
        }
        return pubKey;
    }
}