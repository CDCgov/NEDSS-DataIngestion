package com.cdceq.nbsadapter.processors;

import  org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.stereotype.Component;

import  org.apache.camel.Exchange;
import  org.apache.camel.Processor;

import 	lombok.NoArgsConstructor;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	com.cdceq.nbsadapter.services.Hl7DataServiceProvider;

@Component
@NoArgsConstructor
public class Hl7DataPersister implements Processor {
    private static Logger logger = LoggerFactory.getLogger(Hl7DataPersister.class);

    @Autowired
    private Hl7DataServiceProvider dataServiceProvider;

    public void process(Exchange exchange) throws Exception {
        try {
            String source = (String) exchange.getIn().getHeader("HL7DATASOURCE");
            String hl7Str = exchange.getIn().getBody(String.class);
            dataServiceProvider.saveHl7Message(source, hl7Str);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}