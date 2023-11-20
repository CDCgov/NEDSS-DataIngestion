package gov.cdc.dataingestion.hl7.helper.integration;

import ca.uhn.hl7v2.*;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.builder.ValidationRuleBuilder;
import ca.uhn.hl7v2.validation.builder.support.DefaultValidationBuilder;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.hl7.helper.helper.hapi.MandatoryFields;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IHL7Parser;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;

import static gov.cdc.dataingestion.hl7.helper.constant.hl7.EventTrigger.ORU_01;
import static gov.cdc.dataingestion.hl7.helper.constant.hl7.MessageType.ORU;
import static gov.cdc.dataingestion.hl7.helper.helper.Mapping231To251Helper.*;

public class HL7Parser implements IHL7Parser {

    private HapiContext context;
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_WITH_CARRIER = "\n\r";
    private static final String CARRIER = "\r";

    // this is the support hl7 structure
    private static final String SUPPORTED_HL7_VERSION = "2.5.1";
    private static final String SUPPORTED_HL7_VERSION_231 = "2.3.1";
    private static final String EX_MESSAGE = "Invalid Message ";


    public HL7Parser(HapiContext context) {
        this.context = context;
    }

    public String processFhsMessage(String message) {
        message = message.replaceAll("FHS[^\\r]*\\r?", "");
        message = message.replaceAll("BHS[^\\r]*\\r?", "");
        message = message.replaceAll("BTS[^\\r]*\\r?", "");
        message = message.replaceAll("FTS[^\\r]*\\r?", "");


        return message;
    }

    public String hl7ORUValidation(String message) throws DiHL7Exception {
        /**
         * Default validation include
         * - number of sensible
         * - max length on string type
         * - format for telephone and timestamp, etc
         * */
        defaultOruR01Validator(message);

        /**
         * This goes in-dept into message structure and validate based on the custom definition
         * */
        customOruR01Validator(message);

        return message;
    }

    private void defaultOruR01Validator(String message) throws DiHL7Exception {
        this.context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        try {
            parser.parse(message);
        } catch (HL7Exception e){
            throw new DiHL7Exception(EX_MESSAGE + e.getMessage());
        }

        // Ignore sonar queue complain as this is coming from Library
        ValidationRuleBuilder builder = new DefaultValidationBuilder() { // NOSONAR
            @Override
            protected  void configure() {
                super.configure();
                forAllVersions().message(ORU, ORU_01).onlyKnownSegments();

            }
        };
        this.context.setValidationRuleBuilder(builder);
        parser = context.getPipeParser();

        try {
            parser.parse(message);
        } catch (HL7Exception e){
            throw new DiHL7Exception(EX_MESSAGE + e.getMessage());
        }
    }

    private void customOruR01Validator(String message) throws DiHL7Exception {
        MandatoryFields mandatoryFields = new MandatoryFields(ORU + "_" + ORU_01);

        // Ignore sonar queue complain as this is coming from Library
        ValidationRuleBuilder builder = new DefaultValidationBuilder() { // NOSONAR
            @Override
            protected  void configure() {
                super.configure();
                forAllVersions().message(ORU, ORU_01)
                        .inspect(mandatoryFields);

            }
        };
        this.context.setValidationRuleBuilder(builder);
        this.context.getParserConfiguration().setValidating(true);
        PipeParser parser = context.getPipeParser();

        try {
            parser.parse(message);
        } catch (HL7Exception e){
            throw new DiHL7Exception(EX_MESSAGE + e.getMessage());
        }
    }

    public String hl7MessageStringValidation(String message)  {
         if (message.contains(NEW_LINE_WITH_CARRIER) || message.contains(CARRIER) || message.contains(NEW_LINE)) {
            if (message.contains(NEW_LINE_WITH_CARRIER)) {
                message = message.replaceAll(NEW_LINE_WITH_CARRIER, CARRIER); //NOSONAR
            }
            else if (message.contains(NEW_LINE)) {
                message = message.replaceAll(NEW_LINE, CARRIER); //NOSONAR
            }
            else if (message.contains("\r\r")) {
                message = message.replaceAll("\r\r", CARRIER); //NOSONAR

            }
        } else {
            if (message.contains("\\n")) {
                message = message.replaceAll("\\\\n",CARRIER); //NOSONAR
            }
            else if (message.contains("\\r")) {
                message = message.replaceAll("\\\\r",CARRIER); //NOSONAR
            }
        }

        // make sure message only contain `\` on MSH
        message = message.replaceAll("\\\\+", "\\\\"); //NOSONAR
        return message;
    }

    public HL7ParsedMessage convert231To251(String message, HL7ParsedMessage preParsedMessage) throws DiHL7Exception {
        try {
            HL7ParsedMessage<OruR1> parsedMessage;
            if (preParsedMessage == null) {
                parsedMessage = hl7StringParser(message);
            } else {
                parsedMessage = preParsedMessage;
            }
            ca.uhn.hl7v2.model.v231.message.ORU_R01 parsed231Message = hl7v231StringParser(message);


            // 231 Patient Result
            var patientResult231 = parsed231Message.getPIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTIAll();
            var msh231 = parsed231Message.getMSH();

            if (parsedMessage.getOriginalVersion().equalsIgnoreCase(SUPPORTED_HL7_VERSION_231)) {
                OruR1 oru = parsedMessage.getParsedMessage();
                Ts messageHeaderDateTime = oru.getMessageHeader().getDateTimeOfMessage();

                //region Message Header Conversion
                oru.setMessageHeader(mapMsh(msh231, oru.getMessageHeader()));
                //endregion

                //region Software Segment conversion
                oru.setSoftwareSegment(mapSoftwareSegment(oru.getSoftwareSegment()));
                //endregion

                for (int a = 0; a < oru.getPatientResult().size(); a++) {
                    //region Patient Result - PATIENT - PID
                    var pid231 = patientResult231.get(a).getPIDPD1NK1NTEPV1PV2().getPID();
                    var pid = oru.getPatientResult().get(a).getPatient().getPatientIdentification();
                    oru.getPatientResult().get(a).getPatient().setPatientIdentification(
                            mapPid(pid231, pid));
                    //endregion

                    //region Patient Result - PATIENT - PD1
                    // - mapping LivingDependency - hapi
                    // - mapping PatientPrimaryFacility - hapi
                    // - mapping DuplicatePatient - hapi
                    // - mapping PatientPrimaryCareProviderNameAndIDNo - hapi
                    //endregion

                    //region Patient Result - PATIENT - NK1
                    // HAPI
                    //endregion

                    //region Patient Result - PATIENT - NTE
                    // HAPI
                    //endregion

                    //region Patient Result - PATIENT - VISIT
                    // HAPI
                    //endregion

                    //region Patient Result - ORDER OBSERVATION

                    for(int c = 0; c < oru.getPatientResult().get(a).getOrderObservation().size(); c++) {
                        //region OBSERVATION - Order - OBX
                        for (int d = 0; d < oru.getPatientResult().get(a).getOrderObservation().get(c).getObservation().size(); d++) {
                            // Mapping OBX
                            oru.getPatientResult().get(a).getOrderObservation()
                                    .get(c).getObservation().get(d).setObservationResult(
                                            mapObservationResultToObservationResult(
                                                    oru.getPatientResult().get(a).getOrderObservation().get(c).getObservation().get(d).getObservationResult(),
                                                    oru.getPatientResult().get(a).getOrderObservation().get(c).getObservation().get(d).getObservationResult()
                                            ));
                            //Mapping NTE - hapi
                        }
                        //endregion

                        //region OBSERVATION - OBR

                        var obr231 = patientResult231.get(a).getORCOBRNTEOBXNTECTIAll().get(c).getOBR();
                        oru.getPatientResult().get(a).getOrderObservation().get(c).setObservationRequest(
                                observationRequestToObservationRequest(
                                        obr231,
                                        oru.getPatientResult().get(a).getOrderObservation().get(c).getObservationRequest(),
                                        oru.getPatientResult().get(a).getOrderObservation().get(c).getObservationRequest(),
                                        messageHeaderDateTime
                                )
                        );
                        //endregion

                        //region OBSERVATION - NTE - hapi
                        //HAPI
                        //endregion

                        //region OBSERVATION - CTI - hapi
                        //HAPI
                        //endregion

                        //region OBSERVATION - OBR to SPM
                        var spc = observationRequestToSpecimen(
                                oru.getPatientResult().get(a).getOrderObservation().get(c).getObservationRequest(),
                                new Specimen());
                        oru.getPatientResult().get(a).getOrderObservation().get(c).getSpecimen().add(
                                new gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Specimen(spc)
                        );
                        //endregion
                    }

                    //region Common Order - ORC to ORC
                    var orderORC231 = patientResult231.get(a).getORCOBRNTEOBXNTECTI(0).getORC();
                    var orderOBR231 = patientResult231.get(a).getORCOBRNTEOBXNTECTI(0).getOBR();
                    var orderORC251 = oru.getPatientResult().get(a).getOrderObservation().get(0).getCommonOrder();
                    orderORC251 = mapCommonOrder(orderORC231, orderORC251);
                    oru.getPatientResult().get(a).getOrderObservation().get(0).setCommonOrder(mapOBR2and3ToORC2and3(orderOBR231, orderORC251));
                    //endregion


                    //endregion
                }

                //region DSC
                // test this
                //endregion

                parsedMessage.setParsedMessage(oru);
                return parsedMessage;
            } else {
                throw new DiHL7Exception("Unsupported message version. Please only specify HL7v2.3.1. Provided version is:\t" + parsedMessage.getOriginalVersion());
            }
        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }
    }

    /**
     * This should only take in 231
     * then return hapi 231 ORU_R01 object
     * */
    public  ca.uhn.hl7v2.model.v231.message.ORU_R01 hl7v231StringParser(String message) throws DiHL7Exception {
        try {
            var contextLocal = hl7InitContext(this.context, this.SUPPORTED_HL7_VERSION_231);
            PipeParser parser = contextLocal.getPipeParser();
            ca.uhn.hl7v2.model.v231.message.ORU_R01 msg = (ca.uhn.hl7v2.model.v231.message.ORU_R01) parser.parse(message);
            return msg;
        }catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }
    }

    public HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception{
        try {
            HL7ParsedMessage<OruR1> parsedMessage = new HL7ParsedMessage<>();
            var genericParsedMessage = hl7StringParseHelperWithTerser(message);
            parsedMessage.setMessage(message);
            parsedMessage.setType(genericParsedMessage.getType());
            parsedMessage.setEventTrigger(genericParsedMessage.getEventTrigger());
            parsedMessage.setOriginalVersion(genericParsedMessage.getOriginalVersion());

            var contextLocal = hl7InitContext(this.context, this.SUPPORTED_HL7_VERSION);
            PipeParser parser = contextLocal.getPipeParser();


            if (genericParsedMessage.getOriginalVersion().equalsIgnoreCase(this.SUPPORTED_HL7_VERSION_231) ||
                    genericParsedMessage.getOriginalVersion().equalsIgnoreCase(this.SUPPORTED_HL7_VERSION)) {
                switch(genericParsedMessage.getType()) {
                    case  ORU:
                        switch (genericParsedMessage.getEventTrigger()){
                            case ORU_01:
                                ORU_R01 msg = (ca.uhn.hl7v2.model.v251.message.ORU_R01) parser.parse(genericParsedMessage.getMessage());
                                OruR1 oru = new OruR1(msg);
                                parsedMessage.setParsedMessage(oru);

                                if (genericParsedMessage.getOriginalVersion().equalsIgnoreCase(this.SUPPORTED_HL7_VERSION_231)) {
                                    parsedMessage = convert231To251(genericParsedMessage.getMessage(), parsedMessage);
                                }
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
    private HL7ParsedMessage<OruR1> hl7StringParseHelperWithTerser(String message) throws DiHL7Exception {
        try {
            Message parsedMessage = getMessageFromValidationAndParserContext(message, context);
            Terser terser = new Terser(parsedMessage);

            String messageType = terser.get("/MSH-9-1");
            String messageEventTrigger = terser.get("/MSH-9-2");
            String messageVersion = parsedMessage.getVersion();

            HL7ParsedMessage<OruR1> model = new HL7ParsedMessage<>();
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
        context.setValidationContext(new NoValidation());
        PipeParser parser = context.getPipeParser();
        Message parsedMessage = parser.parse(message);
        return parsedMessage;
    }

    // Context for parser with model factory
    private HapiContext hl7InitContext(HapiContext context, String supportedVersion) {
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory(supportedVersion);
        context.setModelClassFactory(mcf);
        return context;
    }
}


