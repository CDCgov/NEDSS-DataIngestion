package gov.cdc.dataingestion.hl7.helper.integration;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IHL7Parser;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.PatientAddress;
import gov.cdc.dataingestion.hl7.helper.model.PatientIdentification;
import gov.cdc.dataingestion.hl7.helper.model.PatientName;

public class HL7Parser implements IHL7Parser {

    private HapiContext context;
    private final String newLine = "\n";
    private final String carrier = "\r";

    // this is the support hl7 structure
    private final String supportedHL7version = "2.5.1";

    public HL7Parser(HapiContext context) {
        this.context = context;
    }

    public String hl7MessageStringValidation(String message) throws DiHL7Exception {
        if(message.contains(newLine)) {
            message = message.replaceAll(newLine, carrier);
        } else if (!message.contains(carrier)) {
            throw new DiHL7Exception("Incorrect raw message format");
        }
        return message;
    }

    public HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception {

        try {
            context.setValidationContext(ValidationContextFactory.defaultValidation());
            PipeParser parser = context.getPipeParser();
            Message parsedMessage = parser.parse(message);

            Terser terser = new Terser(parsedMessage);

            String messageType = terser.get("/MSH-9-1");
            String messageEventTrigger = terser.get("/MSH-9-2");
            String messageVersion = parsedMessage.getVersion();

            PatientName patientName = new PatientName();
            PatientAddress patientAddress = new PatientAddress();
            PatientIdentification patientIdentification = new PatientIdentification();

            patientName.setFamilyName(terser.get("/PID-5-1"));
            patientName.setGivenName(terser.get("/PID-5-2"));
            patientName.setFurtherGivenName(terser.get("/PID-5-3"));
            patientName.setSuffix(terser.get("/PID-5-4"));
            patientName.setPrefix(terser.get("/PID-5-5"));
            patientName.setPrefix(terser.get("/PID-5-6"));

            patientAddress.setStreetAddress(terser.get("/PID-11-1-1"));
            patientAddress.setStreetName(terser.get("/PID-11-1-2"));
            patientAddress.setDwellingNumber(terser.get("/PID-11-1-3"));
            patientAddress.setOtherDesignation(terser.get("/PID-11-2"));
            patientAddress.setCity(terser.get("/PID-11-3"));
            patientAddress.setState(terser.get("/PID-11-4"));
            patientAddress.setZipCode(terser.get("/PID-11-5"));
            patientAddress.setCountry(terser.get("/PID-11-6"));
            patientAddress.setAddressType(terser.get("/PID-11-7"));

            patientIdentification.setpId(terser.get("/PID-1"));
            patientIdentification.setId(terser.get("/PID-2"));
            patientIdentification.setPatientName(patientName);
            patientIdentification.setPatientAddress(patientAddress);

            var parsedHL7Message = new HL7ParsedMessage(messageVersion, messageType, messageEventTrigger, message);
            parsedHL7Message.setPatientIdentification(patientIdentification);
            return parsedHL7Message;
        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }

    }

    // this will parse valid hl7 message into specific object
    // business rule is needed to specify the parsed model
    public void hl7MessageParser(HL7ParsedMessage parsedMessage) throws DiHL7Exception {

        try {
            context = hl7GeneralizationContext();
            PipeParser parser = context.getPipeParser();

            switch(parsedMessage.getType()) {
                // support type goes here
            }
        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }

    }

    private HapiContext hl7GeneralizationContext() {
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory(supportedHL7version);
        context.setModelClassFactory(mcf);
        return context;
    }
}
