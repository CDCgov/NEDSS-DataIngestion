package gov.cdc.dataingestion.hl7.helper.integration;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.hl7.helper.constant.hl7.EventTrigger;
import gov.cdc.dataingestion.hl7.helper.constant.hl7.MessageType;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IHL7Parser;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Hd;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xon;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;

import java.util.ArrayList;
import java.util.List;

public class HL7Parser implements IHL7Parser {

    private HapiContext context;
    private final String newLine = "\n";
    private final String newLineWithCarrier = "\n\r";
    private final String carrier = "\r";

    // this is the support hl7 structure
    private final String supportedHL7version = "2.5.1";
    private final String supportedHL7version231 = "2.3.1";

    public HL7Parser(HapiContext context) {
        this.context = context;
    }

    public String hl7MessageStringValidation(String message) throws DiHL7Exception {
        if(message.contains(newLine)) {
            message = message.replaceAll(newLine, carrier);
        } else if (message.contains(newLineWithCarrier)) {
            message = message.replaceAll(newLineWithCarrier, carrier);
        } else {
            if (message.contains("\\n")) {
                message = message.replaceAll("\\\\n","\r");
            }
        }

        // make sure message only contain `\` on MSH
        message = message.replaceAll("\\\\+", "\\\\");
        return message;
    }

    public HL7ParsedMessage convert231To251(String message) throws DiHL7Exception {
       try {
           HL7ParsedMessage parsedMessage = hl7StringParser(message);

           if (parsedMessage.getOriginalVersion().equalsIgnoreCase(supportedHL7version231)) {

               OruR1 oru = (OruR1) parsedMessage.getParsedMessage();
               Xon softwareVendorOrg = new Xon();
               softwareVendorOrg.setOrganizationName("Rhapsody");
               softwareVendorOrg.setOrganizationNameTypeCode("L");
               Hd assignAuthority = new Hd();
               assignAuthority.setUniversalId("Rhapsody OID");
               assignAuthority.setUniversalIdType("ISO");
               softwareVendorOrg.setAssignAuthority(assignAuthority);
               softwareVendorOrg.setIdentifierTypeCode("XX");
               softwareVendorOrg.setOrganizationIdentifier("Rhapsody Organization Identifier");


               SoftwareSegment sft = new SoftwareSegment();
               sft.setSoftwareVendorOrganization(softwareVendorOrg);
               sft.setSoftwareCertifiedVersionOrReleaseNumber("4.1.1");
               sft.setSoftwareProductName("Rhapsody");
               sft.setSoftwareBinaryId("Rhapsody Binary ID");

               var listSoftwareSegment = new ArrayList<SoftwareSegment>();
               listSoftwareSegment.add(sft);
               oru.setSoftwareSegment(listSoftwareSegment);
               return parsedMessage;
           } else {
               throw new DiHL7Exception("Unsupported message version. Please only specify HL7v2.3.1. Provided version is:\t" + parsedMessage.getOriginalVersion());
           }
       } catch (Exception e) {
           throw new DiHL7Exception(e.getMessage());
       }
    }

    public HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception{
        try {
            HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
            var genericParsedMessage = hl7StringParseHelperWithTerser(message);
            parsedMessage.setMessage(message);
            parsedMessage.setType(genericParsedMessage.getType());
            parsedMessage.setEventTrigger(genericParsedMessage.getEventTrigger());
            parsedMessage.setOriginalVersion(genericParsedMessage.getOriginalVersion());

            var context = hl7GeneralizationContext(this.context);
            PipeParser parser = context.getPipeParser();


            if (genericParsedMessage.getOriginalVersion().equalsIgnoreCase(this.supportedHL7version231) ||
            genericParsedMessage.getOriginalVersion().equalsIgnoreCase(this.supportedHL7version)) {
                switch(genericParsedMessage.getType()) {
                    case  MessageType.ORU:
                        switch (genericParsedMessage.getEventTrigger()){
                            case EventTrigger.ORU_01:
                                ORU_R01 msg = (ca.uhn.hl7v2.model.v251.message.ORU_R01) parser.parse(genericParsedMessage.getMessage());
                                OruR1 oru = new OruR1(msg);
                                parsedMessage.setParsedMessage(oru);
                                break;
                            default:
                                throw new DiHL7Exception("Unsupported Event Trigger\t\t" + genericParsedMessage.getEventTrigger());
                        }
                        break;
                    default:
                        throw new DiHL7Exception("Unsupported Message Type\t\t" + genericParsedMessage.getType());
                }

                return parsedMessage;
            } else {
                throw new DiHL7Exception("Unsupported HL7 Version, please only specify either 2.3.1 or 2.5.1. Provided version is: \t\t" + genericParsedMessage.getOriginalVersion());
            }

        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }
    }

    // parse message with terser so we can get type, event trigger
    private HL7ParsedMessage hl7StringParseHelperWithTerser(String message) throws DiHL7Exception {
        try {
            Message parsedMessage = getMessageFromValidationAndParserContext(message, context);
            Terser terser = new Terser(parsedMessage);

            String messageType = terser.get("/MSH-9-1");
            String messageEventTrigger = terser.get("/MSH-9-2");
            String messageVersion = parsedMessage.getVersion();

            HL7ParsedMessage model = new HL7ParsedMessage();
            model.setType(messageType);
            model.setEventTrigger(messageEventTrigger);
            model.setOriginalVersion(messageVersion);
            model.setMessage(message);
            return  model;
        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }
    }

    // Context for terser
    private Message getMessageFromValidationAndParserContext(String message, HapiContext context) throws HL7Exception {
        context.setModelClassFactory(new DefaultModelClassFactory());
        context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        Message parsedMessage = parser.parse(message);
        return parsedMessage;
    }

    // Context for parser with model factory
    private HapiContext hl7GeneralizationContext(HapiContext context) {
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory(supportedHL7version);
        context.setModelClassFactory(mcf);
        return context;
    }
}
