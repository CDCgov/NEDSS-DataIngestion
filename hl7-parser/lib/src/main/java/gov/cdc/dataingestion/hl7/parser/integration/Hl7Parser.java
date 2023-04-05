package gov.cdc.dataingestion.hl7.parser.integration;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.hl7.parser.integration.interfaces.IHl7Parser;
import gov.cdc.dataingestion.hl7.parser.model.Hl7ParsedMessage;

public class Hl7Parser implements IHl7Parser {

    private HapiContext context;
    private final String newLine = "\n";
    private final String carrier = "\r";

    // this is the support hl7 structure
    private final String supportedHL7version = "2.5.1";

    public Hl7Parser(HapiContext context) {
        this.context = context;
    }

    public String hl7MessageStringValidation(String message) throws HL7Exception {
        if(message.contains(newLine)) {
            message = message.replaceAll(newLine, carrier);
        } else if (!message.contains(carrier)) {
            throw new HL7Exception("Incorrect raw message format");
        }
        return message;
    }

    public Hl7ParsedMessage hl7StringParser(String message) throws HL7Exception {
        context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        Message parsedMessage = parser.parse(message);

        Terser terser = new Terser(parsedMessage);

        String messageType = terser.get("/MSH-9-1");
        String messageEventTrigger = terser.get("/MSH-9-2");
        String messageVersion = parsedMessage.getVersion();

        return new Hl7ParsedMessage(messageVersion, messageType, messageEventTrigger, message);
    }

    // this will parse valid hl7 message into specific object
    // business rule is needed to specify the parsed model
    public void hl7MessageParser(Hl7ParsedMessage parsedMessage) {
        context = hl7GeneralizationContext();
        PipeParser parser = context.getPipeParser();

        switch(parsedMessage.getType()) {
            // support type goes here
        }
    }

    private HapiContext hl7GeneralizationContext() {
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory(supportedHL7version);
        context.setModelClassFactory(mcf);
        return context;
    }
}
