package com.cdceq.nbsadapter.processors;

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

@Component
@NoArgsConstructor
public class Hl7ToXmlTransformer implements Processor {
    private static Logger logger = LoggerFactory.getLogger(Hl7ToXmlTransformer.class);

    private static String HEADER = "MSH|^~\\&|||||20080925161613||ADT^A05||P|2.6|";

    public void process(Exchange exchange) throws Exception {
        try {
            String hl7MessageStr = exchange.getIn().getBody(String.class);
            hl7MessageStr = HEADER + "\n" + hl7MessageStr;

    		HapiContext context = new DefaultHapiContext();
            context.getParserConfiguration().setValidating(false);
            CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.6");
            context.setModelClassFactory(mcf);

            PipeParser parser = context.getPipeParser();
            Message hl7Message = parser.parse(hl7MessageStr);
            
            XMLParser xmlParser = new DefaultXMLParser();
            String hl7AsXml = xmlParser.encode(hl7Message);

            exchange.getIn().setBody(hl7AsXml);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}