package com.cdceq.nbsadapter.controllers;

import	com.cdceq.nbsadapter.api.model.ElrDataPostResponse;
import	com.cdceq.nbsadapter.api.model.Hl7DataPostResponse;

import  io.swagger.annotations.Api;
import  io.swagger.annotations.ApiOperation;
import  io.swagger.annotations.ApiResponse;
import  io.swagger.annotations.ApiResponses;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  org.springframework.http.HttpStatus;
import 	org.springframework.http.MediaType;
import  org.springframework.http.ResponseEntity;
import  org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.PostMapping;
import  org.springframework.web.bind.annotation.RestController;
import 	org.springframework.web.bind.annotation.RequestBody;

import  org.apache.camel.ProducerTemplate;
import  org.apache.camel.Produce;
import  org.apache.camel.CamelContext;
import  org.apache.camel.Exchange;
import  org.apache.camel.support.DefaultExchange;

import  javax.inject.Inject;

@RestController
@Api(value = "NBS adapter end points", produces = MediaType.APPLICATION_XML_VALUE)
public class ReportStreamController {
    private static Logger logger = LoggerFactory.getLogger(ReportStreamController.class);

    @Produce("seda:process_xml_payload")
    private final ProducerTemplate xmlProducerTemplate;

    @Produce("seda:process_hl7_payload")
    private final ProducerTemplate hl7ProducerTemplate;

    private final CamelContext context;
    private final Exchange exchange;

    @Inject
    public ReportStreamController(ProducerTemplate xmlProducerTemplate,
                                  ProducerTemplate hl7ProducerTemplate,
                                  CamelContext context) {
        this.xmlProducerTemplate = xmlProducerTemplate;
        this.hl7ProducerTemplate = hl7ProducerTemplate;
        this.context = context;
        this.exchange = new DefaultExchange(this.context);
    }

    @GetMapping(path = "nbsadapter/v1/elrs")
    @ApiOperation(value = "Get all elrs")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<String> getAll() throws Exception {
    	//serviceProvider.findAll();
    	String outMsg = "Check logs";
    	logger.info(outMsg);
        return new ResponseEntity<>(outMsg, HttpStatus.OK);
    }
    
    @PostMapping(path = "nbsadapter/v1/elr")
    @ApiOperation(value = "Post ELR data")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<ElrDataPostResponse> processElrData(
                                /* @RequestHeader("AuthToken") String authToken, */
                                @RequestBody String xmlPayload) throws Exception {
        return processXmlData(xmlPayload);
    }

    @PostMapping(path = "nbsadapter/v1/xml")
    @ApiOperation(value = "Post Xml data")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<ElrDataPostResponse> processXmlData(
            /* @RequestHeader("AuthToken") String authToken, */
            @RequestBody String xmlPayload) throws Exception {
    	/*
    	if( !isTokenValid(authToken) ) {
    		throw new Exception("Invalid auth token, please check AuthToken header value!");
    	}
    	*/

        System.out.println("xmlPayload: " + xmlPayload);

        exchange.getIn().setBody(xmlPayload);
        xmlProducerTemplate.send(exchange);

        ElrDataPostResponse edpr = new ElrDataPostResponse();
        edpr.setExecutionNotes("Saved data to the store");

        logger.info("Processed elr post request");
        return new ResponseEntity<>(edpr, HttpStatus.OK);
    }

    @PostMapping(path = "nbsadapter/v1/hl7")
    @ApiOperation(value = "Post HL7 data, persist to mongo db and publish to kafka topic")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = String.class)})
    public ResponseEntity<Hl7DataPostResponse> processHl7Data(
                /* @RequestHeader("AuthToken") String authToken, */
                @RequestBody String hl7Payload) throws Exception {
    	/*
    	if( !isTokenValid(authToken) ) {
    		throw new Exception("Invalid auth token, please check AuthToken header value!");
    	}
    	*/

        exchange.getIn().setBody(hl7Payload);
        hl7ProducerTemplate.send(exchange);

        Hl7DataPostResponse dpr = new Hl7DataPostResponse();
        dpr.setExecutionNotes("Processed inbound hl7 data");

        logger.info("Processed hl7 post request");
        return new ResponseEntity<>(dpr, HttpStatus.OK);
    }

    private boolean isTokenValid(String jwtToken) throws Exception {
    	return true;
    	
    	/*
    	if((null == jwtToken) || (jwtToken.length() <= 0)) {
    		logger.warn("Empty AuthToken header value, thus rejecting!");
    		return false;
    	}
    	*/
    }
}