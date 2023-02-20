package com.cdceq.phinadapter.controllers;

import 	com.fasterxml.jackson.databind.ObjectMapper;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  org.springframework.http.HttpStatus;
import  org.springframework.http.ResponseEntity;
import  org.springframework.web.bind.annotation.RestController;
import	org.springframework.web.bind.annotation.DeleteMapping;
import	org.springframework.web.bind.annotation.GetMapping;
import	org.springframework.web.bind.annotation.PatchMapping;
import	org.springframework.web.bind.annotation.PostMapping;
import  org.springframework.web.bind.annotation.RequestParam;

import 	java.util.Date;

class ApiError {
	public String status = "Bad request";
}

class ShortHealthCheck {
	public String status = "Ok";
}

class DetailedHealthCheck extends ShortHealthCheck {
	public Date currentTime = new Date();
}


@RestController
public class HealthCheckController {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    
    private static String FORMAT_SHORT = "short";
    private static String FORMAT_LONG = "long";
    
    private static ShortHealthCheck shortHealthCheck = new ShortHealthCheck();
    private static ApiError apiError = new ApiError();
    private static ObjectMapper mapper = new ObjectMapper();

    public HealthCheckController() {
    }
    
    @GetMapping(path = "/status")
    public ResponseEntity<String> getStatus(@RequestParam(required = false) String format) throws Exception {
    	String resStr = null;
    	
    	logger.debug("Processing getStatus");
    	
    	if(  FORMAT_SHORT.toLowerCase().equals(format) )
    	{
    		resStr = mapper.writeValueAsString(shortHealthCheck);
    	}
    	else if( FORMAT_LONG.toLowerCase().equals(format) )
    	{
    		resStr = mapper.writeValueAsString(new DetailedHealthCheck());
    	}
    	else
    	{
    		resStr = mapper.writeValueAsString(shortHealthCheck);
    	}
    	
        return new ResponseEntity<>(resStr, HttpStatus.OK);
    }
    
    @PostMapping(path = "/status")
    public ResponseEntity<String> postStatus() throws Exception {
        return new ResponseEntity<>(mapper.writeValueAsString(apiError), HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping(path = "/status")
    public ResponseEntity<String> deleteStatus() throws Exception {
        return new ResponseEntity<>(mapper.writeValueAsString(apiError), HttpStatus.BAD_REQUEST);
    }
    
    @PatchMapping(path = "/status")
    public ResponseEntity<String> patchStatus() throws Exception {
        return new ResponseEntity<>(mapper.writeValueAsString(apiError), HttpStatus.BAD_REQUEST);
    }    
}