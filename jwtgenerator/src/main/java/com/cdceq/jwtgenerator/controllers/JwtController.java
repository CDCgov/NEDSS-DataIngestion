package com.cdceq.jwtgenerator.controllers;

import  com.cdceq.jwtgenerator.services.TokenGenerator;
import  com.vault.utils.VaultValuesResolver;

import  io.swagger.annotations.Api;
import  io.swagger.annotations.ApiOperation;
import  io.swagger.annotations.ApiResponse;
import  io.swagger.annotations.ApiResponses;

import  org.springframework.http.HttpStatus;
import 	org.springframework.http.MediaType;
import  org.springframework.http.ResponseEntity;
import  org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.RestController;
import 	org.springframework.web.bind.annotation.RequestHeader;
import 	org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.beans.factory.annotation.Value;

import  javax.servlet.http.HttpServletRequest;

import  org.json.JSONObject;
import  org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import  javax.inject.Inject;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

@RestController
@Api(value = "JWT Operations Controller", produces = MediaType.APPLICATION_XML_VALUE)
public class JwtController {
    private static Logger logger = LoggerFactory.getLogger(JwtController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Value("${jwt.seed}")
    private String jwtSeed;

    private StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
    private boolean bInitialized = false;

    @Inject
    public JwtController() {
    }

    private void init() {
        if( !bInitialized ) {
            jwtSeed = VaultValuesResolver.getVaultKeyValue(jwtSeed);
            decryptor.setPassword(jwtSeed);
            bInitialized = true;
        }
    }

    @GetMapping(path = "jwt/v1/token")
    @ApiOperation(value = "Generate new token")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<String> processRequest(
            @RequestHeader(value="APP-PASSPHRASE") String appPassPhrase) throws Exception {
        if((null == appPassPhrase) || (appPassPhrase.length() <= 0)) {
            logger.info("Rejecting request");
            return new ResponseEntity<>("Missing http header APP-PASSPHRASE", HttpStatus.BAD_REQUEST);
        }

        init();

        String remoteAddr = request.getRemoteAddr();
        String token = tokenGenerator.generateToken(appPassPhrase, remoteAddr);

        JSONObject reply = new JSONObject();
        reply.put("token", token);

        return new ResponseEntity<>(reply.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "jwt/v1/verify")
    @ApiOperation(value = "Validate provided token")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<String> verifyToken(
            @RequestHeader(value="APP-TOKEN") String appToken,
            @RequestHeader(value="APP-HOST-ADDRESS") String appHostAddress) throws Exception {
        if((null == appToken) || (appToken.length() <= 0)) {
            logger.info("Invalid token, rejecting request");
            return new ResponseEntity<>("Missing http header APP-TOKEN", HttpStatus.BAD_REQUEST);
        }

        init();
        String remoteAddr = request.getRemoteAddr();;

        if((null != appHostAddress) && (appHostAddress.length() > 0)) {
            remoteAddr = decryptor.decrypt(appHostAddress);
        }

        boolean isValid = tokenGenerator.verifyToken(appToken, remoteAddr);

        JSONObject reply = new JSONObject();
        reply.put("valid", isValid);

        logger.debug("Completed verification, and returning to the caller");

        return new ResponseEntity<>(reply.toString(), HttpStatus.OK);
    }
}
