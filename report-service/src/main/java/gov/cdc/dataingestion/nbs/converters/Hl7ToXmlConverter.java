package gov.cdc.dataingestion.nbs.converters;

import  ca.uhn.hl7v2.model.Message;
import 	ca.uhn.hl7v2.HapiContext;
import 	ca.uhn.hl7v2.DefaultHapiContext;
import 	ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import 	ca.uhn.hl7v2.parser.PipeParser;
import 	ca.uhn.hl7v2.parser.XMLParser;
import 	ca.uhn.hl7v2.parser.DefaultXMLParser;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

import java.util.Optional;


public class Hl7ToXmlConverter {
    private static final String HEADER = "MSH|^~\\&|||||20080925161613||ADT^A05||P|2.6|";
    private static final Hl7ToXmlConverter instance = new Hl7ToXmlConverter();

    public static Hl7ToXmlConverter getInstance() {
        return instance;
    }

    private Hl7ToXmlConverter() {
    }

    public String convertXl7ToXml(String hl7) throws Exception {
        String hl7MessageStr = HEADER + "\n" + hl7;

        HapiContext context = new DefaultHapiContext();
        context.getParserConfiguration().setValidating(false);
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.6");
        context.setModelClassFactory(mcf);

        PipeParser parser = context.getPipeParser();
        Message hl7Message = parser.parse(hl7MessageStr);

        XMLParser xmlParser = new DefaultXMLParser();
        String hl7AsXml = xmlParser.encode(hl7Message);

        return hl7AsXml;
    }
}

