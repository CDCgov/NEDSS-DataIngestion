package com.cdceq.phinadapter.controllers;

import	com.cdceq.phinadapter.api.model.ElrWorkerThreadUpdatePostResponse;
import  com.cdceq.phinadapter.services.NbsOdseServiceProvider;

import  com.vault.utils.VaultValuesResolver;

import  io.swagger.annotations.Api;
import  io.swagger.annotations.ApiOperation;
import  io.swagger.annotations.ApiResponse;
import  io.swagger.annotations.ApiResponses;

import  org.springframework.http.HttpStatus;
import 	org.springframework.http.MediaType;
import  org.springframework.http.ResponseEntity;
import  org.springframework.web.bind.annotation.PostMapping;
import  org.springframework.web.bind.annotation.RestController;
import 	org.springframework.web.bind.annotation.RequestBody;
import 	org.springframework.web.bind.annotation.RequestHeader;
import 	org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.beans.factory.annotation.Value;

import  org.json.JSONObject;

import  org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import  javax.inject.Inject;
import  javax.servlet.http.HttpServletRequest;

import  org.apache.http.HttpResponse;
import  org.apache.http.client.methods.HttpGet;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

@RestController
@Api(value = "PHIN system interfacing end points", produces = MediaType.APPLICATION_XML_VALUE)
public class PhinController {
    private static Logger logger = LoggerFactory.getLogger(PhinController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private NbsOdseServiceProvider serviceProvider;

    @Value("${jwt.enabled}")
    private String jwtEnabled;

    @Value("${jwt.endpoint}")
    private String validationEndpoint;

    @Value("${jwt.seed}")
    private String seed;

    private boolean bJwtEnabled;
    private StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    private boolean bInitialized = false;

    @Inject
    public PhinController() {
    }

    private void init() {
        if( !bInitialized ) {
            bJwtEnabled = Boolean.valueOf(VaultValuesResolver.getVaultKeyValue(jwtEnabled));
            validationEndpoint = VaultValuesResolver.getVaultKeyValue(validationEndpoint);
            seed = VaultValuesResolver.getVaultKeyValue(seed);
            encryptor.setPassword(seed);
            bInitialized = true;
        }
    }

    @PostMapping(path = "phinadapter/v1/elrwqactivator")
    @ApiOperation(value = "Update elr worker queue table for given id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<ElrWorkerThreadUpdatePostResponse> processRequest(
            @RequestHeader("APP-TOKEN") String authToken,
            @RequestBody String payload) throws Exception {
        ElrWorkerThreadUpdatePostResponse edpr = new ElrWorkerThreadUpdatePostResponse();
        StringBuffer sb = new StringBuffer();

        init();

        if( bJwtEnabled && !isTokenValid(authToken) ) {
            String msg = "Invalid token, please check APP-TOKEN header value and ensure token is valid!";
            edpr.setExecutionNotes(msg);
    		logger.warn(msg);
            return new ResponseEntity<>(edpr, HttpStatus.BAD_REQUEST);
    	}

        logger.info("Processing nbs odse request for payload = {}", payload);
        int recordId = serviceProvider.processMessage(payload, sb);

        edpr.setExecutionNotes("Updated row with recordId = " + recordId);

        logger.info("Processed nbs odse elrworkerthread table update request for recordId = {}", recordId);
        return new ResponseEntity<>(edpr, HttpStatus.OK);
    }

    private boolean isTokenValid(String jwtToken) throws Exception {
    	if((null == jwtToken) || (jwtToken.length() <= 0)) {
    		return false;
    	}

        try {
            String remoteAddr = request.getRemoteAddr();
            String encryptedRemoteAddr = encryptor.encrypt(remoteAddr);

            HttpGet validationRequest = new HttpGet(validationEndpoint);
            validationRequest.addHeader("APP-TOKEN", jwtToken);
            validationRequest.addHeader("APP-HOST-ADDRESS", encryptedRemoteAddr);

            HttpResponse response = VaultValuesResolver.initNonSslClient().execute(validationRequest);
            String resString = VaultValuesResolver.processResponse(response);

            JSONObject reply = new JSONObject(resString);
            boolean isValid = reply.getBoolean("valid");

            return isValid;
        } catch (Exception e) {
            logger.error("Jwt service error while getting secrets, will retry later, url = {}", validationEndpoint, e);
            throw e;
        }
    }
}