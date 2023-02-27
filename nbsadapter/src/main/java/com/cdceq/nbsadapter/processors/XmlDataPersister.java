package com.cdceq.nbsadapter.processors;

import  org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.stereotype.Component;

import  org.apache.camel.Exchange;
import  org.apache.camel.Processor;

import 	lombok.NoArgsConstructor;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  ca.uhn.hl7v2.model.Message;
import 	ca.uhn.hl7v2.HapiContext;
import 	ca.uhn.hl7v2.DefaultHapiContext;
import 	ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import 	ca.uhn.hl7v2.parser.PipeParser;
import 	ca.uhn.hl7v2.parser.XMLParser;
import 	ca.uhn.hl7v2.parser.DefaultXMLParser;

import	com.cdceq.nbsadapter.services.ElrDataServiceProvider;

@Component
@NoArgsConstructor
public class XmlDataPersister implements Processor {
    private static Logger logger = LoggerFactory.getLogger(XmlDataPersister.class);

    @Autowired
    private ElrDataServiceProvider dataServiceProvider;

    public void process(Exchange exchange) throws Exception {
        try {
            String xmlStr = exchange.getIn().getBody(String.class);
            dataServiceProvider.saveXmlMessage(xmlStr);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}