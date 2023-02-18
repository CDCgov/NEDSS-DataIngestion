package com.cdceq.jwtgenerator.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import 	org.springframework.stereotype.Service;
import 	org.springframework.beans.factory.annotation.Value;

import 	lombok.NoArgsConstructor;

import  io.jsonwebtoken.Jwts;
import  io.jsonwebtoken.SignatureAlgorithm;
import  javax.crypto.spec.SecretKeySpec;
import  java.security.Key;

import java.util.Base64;
import	java.util.UUID;
import	java.util.Date;
import  java.util.HashMap;
import	java.time.Instant;
import	java.time.temporal.ChronoUnit;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	com.vault.utils.VaultValuesResolver;

@Service
@NoArgsConstructor
public class TokenGenerator {
    private static final String CLAIM_PHRASE = "phrase";
    private static final int TOKEN_MAX_VALID_PERIOD = 1;
	private static Logger logger = LoggerFactory.getLogger(TokenGenerator.class);
    private static HashMap<String, TokenInfoHolder> tokensMap = new HashMap<>();

    @Value("${jwt.secret-for-algorithm}")
    private String vaultSecretForAlgorithm;

    @Value("${jwt.claim-name}")
    private String vaultClaimName;

    @Value("${jwt.claim-email}")
    private String vaultClaimEmail;

    @Value("${jwt.claim-subject}")
    private String vaultClaimSubject;

    private long lAllowed = TOKEN_MAX_VALID_PERIOD * 3600 * 1000;

    public boolean verifyToken(String appToken, String remoteAddr) {
        boolean isValid = false;
        TokenInfoHolder tih = null;

        synchronized ( tokensMap ) {
            tih = (TokenInfoHolder) tokensMap.get(remoteAddr);
        }

        if(null == tih) {
            return isValid;
        }

        try {
            String secretForAlgorithm = VaultValuesResolver.getVaultKeyValue(vaultSecretForAlgorithm);
            Key hmacKey = new SecretKeySpec(secretForAlgorithm.getBytes(),
                    SignatureAlgorithm.HS256.getJcaName());

            Jws<Claims> jwtClaims = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(appToken);

            String actualPhrase = (String) jwtClaims.getBody().get(CLAIM_PHRASE);
            isValid = tih.getPassPhrase().equals(actualPhrase);
            if( !isValid ) {
                return isValid;
            }

            Date now = new Date();
            long lDuration = now.getTime() - tih.getExpiration().getTime();
            isValid = ((lAllowed - lDuration) > 0);
        }
        catch(Exception e) {
            logger.error("Token parsing failed, thus rejecting");
        }

        return isValid;
    }

    public String generateToken(String appPhrase, String remoteAddr) throws Exception {
        String jwtToken = null;

        try {
            String secretForAlgorithm = VaultValuesResolver.getVaultKeyValue(vaultSecretForAlgorithm);
            Key hmacKey = new SecretKeySpec(secretForAlgorithm.getBytes(),
                                    SignatureAlgorithm.HS256.getJcaName());

            String claimName = VaultValuesResolver.getVaultKeyValue(vaultClaimName);
            String claimEmail = VaultValuesResolver.getVaultKeyValue(vaultClaimEmail);
            String claimSubject = VaultValuesResolver.getVaultKeyValue(vaultClaimSubject);

            Instant now = Instant.now();
            Date expiration = Date.from(now.plus(TOKEN_MAX_VALID_PERIOD, ChronoUnit.HOURS));
            jwtToken = Jwts.builder()
                        .claim("name", claimName)
                        .claim("email", claimEmail)
                        .claim(CLAIM_PHRASE, appPhrase)
                        .setSubject(claimSubject)
                        .setId(UUID.randomUUID().toString())
                        .setIssuedAt(Date.from(now))
                        .setExpiration(expiration)
                        .signWith(hmacKey)
                        .compact();

            TokenInfoHolder tih = new TokenInfoHolder();
            tih.setRemoteAddress(remoteAddr);
            tih.setPassPhrase(appPhrase);
            tih.setToken(jwtToken);
            tih.setExpiration(expiration);

            synchronized ( tokensMap ) {
                tokensMap.put(remoteAddr, tih);
            }

            return jwtToken;
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
