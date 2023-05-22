package gov.cdc.dataingestion.nbs.converters;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisit;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantity;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantityRelationship;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientAdditionalDemographic;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import  gov.cdc.dataingestion.hl7.helper.HL7Helper;
import  gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;

import  gov.cdc.dataingestion.nbs.jaxb.*;

import  ca.uhn.hl7v2.DefaultHapiContext;
import  ca.uhn.hl7v2.parser.PipeParser;

import  jakarta.xml.bind.JAXBContext;
import  jakarta.xml.bind.Unmarshaller;

import  java.io.File;

import  java.util.List;

import org.apache.kafka.common.protocol.types.Field;
import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

public class RhapsodysXmlToHl7Converter {
    private static Logger log = LoggerFactory.getLogger(RhapsodysXmlToHl7Converter.class);
    private static String EMPTY_STRING = "";
    private static String NEWLINE = "\n";
    private static String COLUMNS_SEPARATOR = "|";
    private static String LISTS_SEPARATOR = "~";     // Ex: Two patient identifiers, DL and LabID
    private static String ATTRIBUTES_SEPARATOR = "^";// Ex: Id, First Name, Last Name; 7654^JONES^INDIANA
    private static String INNTER_ATTRIBUTES_SEPARATOR = "&";     // EX: Lab tech comments, multiple last names

    private static RhapsodysXmlToHl7Converter instance = new RhapsodysXmlToHl7Converter();

    public static RhapsodysXmlToHl7Converter getInstance() {
        return instance;
    }

    private RhapsodysXmlToHl7Converter() {
    }

    public String convertToXl7(String xmlFile) throws Exception {
        String xmlFilename = "/Users/RameshAddanki/cdcprojects/DocumentsProject/ExistingXmlSample.txt";
        StringBuilder sb = new StringBuilder();

        JAXBContext contextObj = JAXBContext.newInstance("gov.cdc.dataingestion.nbs.jaxb");
        Unmarshaller unmarshaller = contextObj.createUnmarshaller();

        Container container = (Container) unmarshaller.unmarshal(new File(xmlFilename));

        sb.append(streamHeader(container.getHL7LabReport().getHL7MSH()));
        sb.append(NEWLINE);
        sb.append(streamPaientIdentifications(container.getHL7LabReport().getHL7PATIENTRESULT()));
        sb.append(NEWLINE);

        String hl7Str = sb.toString();
        System.out.println("hl7Str = " + hl7Str);

        return hl7Str;
    }

    private String streamOrderObservations(List<HL7OrderObservationType> orderObsTypeList) {
        StringBuilder sb = new StringBuilder();

        if(null== orderObsTypeList) return sb.toString();

        for(HL7OrderObservationType obsType : orderObsTypeList) {
            sb.append(streamHHL7OrderObservationType(obsType));
            sb.append(NEWLINE);
        }

        return sb.toString();
    }

    private String streamOrderObservationResults(PatientResultOrderObservation results) {
        StringBuilder sb = new StringBuilder();

        if(null == results) return sb.toString();

        for(HL7OBSERVATIONType obsType : results.getOBSERVATION()) {
            sb.append(streamHL7OBSERVATIONType(obsType));
            sb.append(NEWLINE);
        }

        return sb.toString();
    }

    private String streamPaientIdentifications(List<HL7PATIENTRESULTType> patientresultTypeList) {
        StringBuilder sb = new StringBuilder();

        for(HL7PATIENTRESULTType resultType : patientresultTypeList) {
            sb.append(streamHL7PATIENTType(resultType.getPATIENT()));
            sb.append(NEWLINE);
            sb.append(streamOrderObservations(resultType.getORDEROBSERVATION()));
            sb.append(NEWLINE);
        }

        return sb.toString();
    }

    private String streamHL7OBSERVATIONType(HL7OBSERVATIONType hl7ObsType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7ObsType) return sb.toString();

        sb.append("OBX");
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7OBXType(hl7ObsType.getObservationResult()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7NTETypeList(hl7ObsType.getNotesAndComments()));

        // List<HL7NTEType>
        // ramesh

        return sb.toString();
    }

    private String streamHHL7OrderObservationType(HL7OrderObservationType obsType) {
        StringBuilder sb = new StringBuilder();

        if(null == obsType) return sb.toString();;

        sb.append(streamCommonOrders(obsType.getCommonOrder()));
        sb.append(NEWLINE);
        sb.append(streamObservationRequests(obsType.getObservationRequest())); // HL7OBRType
        sb.append(NEWLINE);
        sb.append(streamOrderObservationResults(obsType.getPatientResultOrderObservation()));
        sb.append(NEWLINE);
        sb.append(streamHL7NTETypeList(obsType.getNotesAndComments()));
        sb.append(NEWLINE);
        //sb.append(obsType.getPatientResultOrderSPMObservation()); // HL7PatientResultSPMType

        return sb.toString();
    }

    private String streamHL7OBXType(HL7OBXType hl7OBXType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7OBXType) return sb.toString();

        sb.append(streamHL7SIType(hl7OBXType.getSetIDOBX()));
        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7OBXType.getValueType()) {
            sb.append(hl7OBXType.getValueType());
        }

        sb.append(COLUMNS_SEPARATOR);
        if(null != hl7OBXType.getObservationIdentifier()) {
            sb.append(streamHL7CWEType(hl7OBXType.getObservationIdentifier()));
        }

        sb.append(COLUMNS_SEPARATOR);
        if(null != hl7OBXType.getObservationSubID()) {
            sb.append(hl7OBXType.getObservationSubID());
        }

        sb.append(COLUMNS_SEPARATOR);
        if(null != hl7OBXType.getObservationValue()) {
            for(String s : hl7OBXType.getObservationValue()) {
                sb.append(s);
                sb.append(ATTRIBUTES_SEPARATOR);
            }
        }

        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.6 - Units: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append(hl7OBXType.getReferencesRange());
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CWETypeList(hl7OBXType.getAbnormalFlags()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.9 - Probability: NM
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.10 - Nature of Abnormal Test	2: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append(hl7OBXType.getObservationResultStatus());
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.12 - Effective Date of Reference Range: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.13 - User Defined Access Checks: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.14 - Date/Time of the Observation: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CWEType(hl7OBXType.getProducersReference()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.16 - Responsible Observer: XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.17 - Observation Method: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.18 - Equipment Instance Identifier: EI
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(hl7OBXType.getDateTimeOftheAnalysis()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.20 - Reserved for harmonization with V2.6: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.21 - Reserved for harmonization with V2.6: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // OBX.22 - Reserved for harmonization with V2.6: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XONType(hl7OBXType.getPerformingOrganizationName()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XADType(hl7OBXType.getPerformingOrganizationAddress()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XCNType(hl7OBXType.getPerformingOrganizationMedicalDirector()));
        sb.append(COLUMNS_SEPARATOR);

        return sb.toString();
    }

    private String streamObservationRequests(HL7OBRType hl7OBRType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7OBRType) return sb.toString();

        sb.append("OBR");
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7SIType(hl7OBRType.getSetIDOBR()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.2 - Placer Order Number: EI
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7EIType(hl7OBRType.getFillerOrderNumber()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CWEType(hl7OBRType.getUniversalServiceIdentifier()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.5 - Priority - OBR: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.6 - Requested Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(hl7OBRType.getObservationDateTime()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.8 - Observation End Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.9 - Collection Volume: CQ
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.10 - Collector Identifier: XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.11 - Specimen Action Code: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.12 - Danger Code: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.13 - Relevant Clinical Information: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.14 - Specimen Received Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.15 - Specimen Source: SPS
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XCNTypeList(hl7OBRType.getOrderingProvider()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XTNTypeList(hl7OBRType.getOrderCallbackPhoneNumber()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.18 - Placer Field 1: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.19 - Placer Field 2: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.20 - Filler Field 1: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.21 - Filler Field 2: ST
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.22 - Results Rpt/Status Chng - Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.23 - Charge to Practice: MOC
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.24 - Diagnostic Serv Sect ID: ID
        sb.append(COLUMNS_SEPARATOR);

        sb.append(hl7OBRType.getResultStatus());
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.26 - Parent Result: PRL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.27 - Quantity/Timing: TQ
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.28 - Result Copies To: XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.29 - Parent: EIP
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.30 - Transportation Mode: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.31 - Reason for Study: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.32 - Principal Result Interpreter: NDL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.33 - Assistant Result Interpreter: NDL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.34 - Technician: NDL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.35 - Transcriptionist: NDL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.36 - Scheduled Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);

        if((null != hl7OBRType.getNumberofSampleContainers())
                && (null != hl7OBRType.getNumberofSampleContainers().getHL7Numeric())) {
            sb.append(hl7OBRType.getNumberofSampleContainers().getHL7Numeric().intValue());
        }

        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.38 - Transport Logistics of Collected Sample: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.39 - Collector's Comment: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.40 - Transport Arrangement Responsibility: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.41 - Transport Arranged: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.42 - Escort Required: ID
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.43 - Planned Patient Transport Comment: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.44 - Procedure Code: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.45 - Procedure Code Modifier: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.46 - Placer Supplemental Service Information: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.47 - Filler Supplemental Service Information: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.48 - Medically Necessary Duplicate Procedure Reason.: CWE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.49 - Result Handling: IS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                        // OBR.50 - Parent Universal Service Identifier: CWE
        sb.append(COLUMNS_SEPARATOR);

        return sb.toString();
    }

/*
ORC.1 - Order Control	2	ID	R 	-	0119
ORC.2 - Placer Order Number	22	EI	C 	-
ORC.3 - Filler Order Number	22	EI	C 	-
ORC.4 - Placer Group Number	22	EI	O 	-
ORC.5 - Order Status	2	ID	O 	-	0038
ORC.6 - Response Flag
ORC.7 - Quantity/Timing	200	TQ	B 	∞
ORC.8 - Parent Order	200	EIP	O 	-
ORC.9 - Date/Time of Transaction	26	TS	O 	-
ORC.10 - Entered By	250	XCN	O 	∞
ORC.11 - Verified By	250	XCN	O 	∞
ORC.12 - Ordering Provider	250	XCN	O 	∞
ORC.13 - Enterer's Location	80	PL	O 	-
ORC.14 - Call Back Phone Number	250	XTN	O 	2
ORC.15 - Order Effective Date/Time	26	TS	O 	-
ORC.16 - Order Control Code Reason	250	CE	O 	-
ORC.17 - Entering Organization	250	CE	O 	-
ORC.18 - Entering Device	250	CE	O 	-
ORC.19 - Action By	250	XCN	O 	∞
ORC.20 - Advanced Beneficiary Notice Code	250	CE	O 	-	0339


ORC.21 - Ordering Facility Name	250	XON	O 	∞
ORC.22 - Ordering Facility Address	250	XAD	O 	∞
ORC.23 - Ordering Facility Phone Number	250	XTN	O 	∞
ORC.24 - Ordering Provider Address	250	XAD	O 	∞
ORC.25 - Order Status Modifier	250	CWE	O 	-
ORC.26 - Advanced Beneficiary Notice Override Reason	60	CWE	C 	-	0552
ORC.27 - Filler's Expected Availability Date/Time	26	TS	O 	-
ORC.28 - Confidentiality Code	250	CWE	O 	-	0177
ORC.29 - Order Type	250	CWE	O 	-	0482
ORC.30 - Enterer Authorization Mode	250	CNE	O 	-	0483
ORC.31 - Parent Universal Service Identifier

   | 1  | 2 |     3        | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 | 20 |                  21            |                     22                   |     23      |                  24                      | 25 | 26 |
ORC| RE |   |34411541099348|   |   |   |   |   |   |    |    |    |    |    |    |    |    |    |    |    | Simon-Williamson Cl-Princeton^ |
ORC| RE |   |34411541099348|   |   |   |   |   |   |    |    |    |    |    |    |    |    |    |    |    | Simon-Williamson Cl-Princeton^ | 832 Princeton Ave SW^Birmingham^GA^30342 | 205^2068375 | 832 Princeton Ave SW^Birmingham^GA^30342 |    |    ||||||
*/
    private String streamCommonOrders(HL7ORCType hl7ORCType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7ORCType) return sb.toString();

        sb.append("ORC");
        sb.append(COLUMNS_SEPARATOR);
        sb.append(hl7ORCType.getOrderControl());
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7EIType(hl7ORCType.getPlacerOrderNumber()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7EIType(hl7ORCType.getFillerOrderNumber()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7EIType(hl7ORCType.getPlacerGroupNumber()));
        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7ORCType.getOrderStatus()) {
            sb.append(hl7ORCType.getOrderStatus());
        }

        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7ORCType.getResponseFlag()) {
            sb.append(hl7ORCType.getResponseFlag());
        }

        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.7 - Quantity/Timing	: TQ
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.8 - Parent Order	    : EIP
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(hl7ORCType.getDateTimeOfTransaction())); // ORC.9 - Date/Time of Transaction: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.10 - Entered By	    : XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.11 - Verified By	    : XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XCNTypeList(hl7ORCType.getOrderingProvider())); // ORC.12 - Ordering Provider : XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.13 - Enterer's Location: PL
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.14 - Call Back Phone Number: XTN
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(hl7ORCType.getOrderEffectiveDateTime())); // ORC.15 - Order Effective Date/Time : TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.16 - Order Control Code Reason: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.17 - Entering Organization: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.18 - Entering Device: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.19 - Action By: XCN
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.20 - Advanced Beneficiary Notice Code: CE
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XONTypeList(hl7ORCType.getOrderingFacilityName())); // ORC.21 - Ordering Facility Name : XON
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XADTypeList(hl7ORCType.getOrderingFacilityAddress())); // ORC.22 - Ordering Facility Address: XAD
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XTNTypeList(hl7ORCType.getOrderingFacilityPhoneNumber())); // ORC.23 - Ordering Facility Phone Number: XTN
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XADTypeList(hl7ORCType.getOrderingProviderAddress())); // ORC.24 - Ordering Provider Address: XAD
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.25 - Order Status Modifier: CWE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.26 - Advanced Beneficiary Notice Override Reason: CWE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.27 - Filler's Expected Availability Date/Time: TS
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.28 - Confidentiality Code: CWE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // RC.29 - Order Type: CWE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.30 - Enterer Authorization Mode: CNE
        sb.append(COLUMNS_SEPARATOR);
        sb.append("");                  // ORC.31 - Parent Universal Service Identifier: ??
        sb.append(COLUMNS_SEPARATOR);

        return sb.toString();
    }

    private String streamHeader(HL7MSHType hl7MSHType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7MSHType) return sb.toString();

        sb.append("MSH");
        sb.append(COLUMNS_SEPARATOR);
        sb.append(hl7MSHType.getEncodingCharacters());
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7HDType(hl7MSHType.getSendingApplication()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7HDType(hl7MSHType.getSendingFacility()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7HDType(hl7MSHType.getReceivingApplication()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7HDType(hl7MSHType.getReceivingFacility()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(hl7MSHType.getDateTimeOfMessage(), true));
        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7MSHType.getSecurity()) {
            sb.append(hl7MSHType.getSecurity());
        }

        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7MSHType.getMessageType()) {
            sb.append(streamHL7MSGType(hl7MSHType.getMessageType()));
        }

        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7MSHType.getMessageControlID()) {
            sb.append(hl7MSHType.getMessageControlID());
        }

        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7MSHType.getProcessingID()) {
            sb.append(streamHL7PTType(hl7MSHType.getProcessingID()));
        }

        sb.append(COLUMNS_SEPARATOR);

        if(null != hl7MSHType.getVersionID()) {
            sb.append(streamHL7VIDType(hl7MSHType.getVersionID()));
        }

        sb.append(COLUMNS_SEPARATOR);

        /*
        MSH.13 - Sequence Number	15	NM	O 	-
        MSH.14 - Continuation Pointer	180	ST	O 	-
        MSH.15 - Accept Acknowledgment Type	2	ID	O 	-	0155
        MSH.16 - Application Acknowledgment Type	2	ID	O 	-	0155
        MSH.17 - Country Code	3	ID	O 	-	0399
        MSH.18 - Character Set	16	ID	O 	∞	0211
        MSH.19 - Principal Language Of Message	250	CE	O 	-
        MSH.20 - Alternate Character Set Handling Scheme	20	ID	O 	-	0356
        MSH.21 - Message Profile Identifier
        */

        return sb.toString();
    }

    private String streamHL7SADType(HL7SADType hl7SADType) {
        StringBuilder sb = new StringBuilder();

        if (null == hl7SADType) return sb.toString();

        sb.append(hl7SADType.getHL7StreetOrMailingAddress());

        if(null != hl7SADType.getHL7StreetName()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7SADType.getHL7StreetName());
        }

        if(null != hl7SADType.getHL7DwellingNumber()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7SADType.getHL7DwellingNumber());
        }

        return sb.toString();
    }

    private String streamHL7XADType(HL7XADType hl7XADType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7XADType) return sb.toString();

        sb.append(streamHL7SADType(hl7XADType.getHL7StreetAddress()));

        if(null != hl7XADType.getHL7OtherDesignation()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XADType.getHL7OtherDesignation());
        }

        if(null != hl7XADType.getHL7City()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XADType.getHL7City());
        }

        if(null != hl7XADType.getHL7StateOrProvince()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XADType.getHL7StateOrProvince());
        }

        if(null != hl7XADType.getHL7ZipOrPostalCode()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XADType.getHL7ZipOrPostalCode());
        }

        if(null != hl7XADType.getHL7Country()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XADType.getHL7Country());
        }

        /*
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.7 - Address Type: ID
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.8 - Other Geographic Designation: ST
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.9 - County/Parish Code: IS
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.10 - Census Tract: IS
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.11 - Address Representation Code: ID
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.12 - Address Validity Range: DR
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.13 - Effective Date: TS
        sb.append(ATTRIBUTES_SEPARATOR); // XAD.14 - Expiration Date: TS
        */

        return sb.toString();
    }

    private String streamHL7EIType(HL7EIType hl7EIType) {
        StringBuilder sb = new StringBuilder();
        if(null == hl7EIType) return sb.toString();

        sb.append(hl7EIType.getHL7EntityIdentifier());

        if(null != hl7EIType.getHL7NamespaceID()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7EIType.getHL7NamespaceID());
        }

        if(null != hl7EIType.getHL7UniversalID()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7EIType.getHL7UniversalID());
        }

        if(null != hl7EIType.getHL7UniversalIDType()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7EIType.getHL7UniversalIDType());
        }

        return sb.toString();
    }

    // PID|1|01844|01D0301471^^^^PN^LABC~^^^^PI^NE CLINIC&24D1040593&CLIA||TEST112^FIRSTMIN1
    private String streamHL7PATIENTType(HL7PATIENTType patient) {
        StringBuilder sb = new StringBuilder();

        sb.append("PID");
        sb.append(COLUMNS_SEPARATOR);

        sb.append(streamHL7PIDType(patient.getPatientIdentification()));
        sb.append(COLUMNS_SEPARATOR);

        return sb.toString();
    }

    private String streamHL7PIDType(HL7PIDType pid) {
        StringBuilder sb = new StringBuilder();

        if(null == pid) return sb.toString();

        sb.append(streamHL7SITypeList(pid.getSetIDPID()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CXType(pid.getPatientID()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CXTypeList(pid.getPatientIdentifierList()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CXTypeList(pid.getAlternatePatientIDPID()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XPNTypeList(pid.getPatientName()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7XPNTypeList(pid.getMothersMaidenName()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7TSType(pid.getDateTimeOfBirth()));
        sb.append(COLUMNS_SEPARATOR);
        sb.append(pid.getAdministrativeSex());
        sb.append(COLUMNS_SEPARATOR);

        /*
        PID.9 - Patient Alias	250	XPN	B 	∞
        PID.10 - Race	250	CE	O 	∞	0005
        PID.11 - Patient Address	250	XAD	O 	∞
        PID.12 - County Code	4	IS	B 	-	0289
        PID.13 - Phone Number - Home	250	XTN	O 	∞
        PID.14 - Phone Number - Business	250	XTN	O 	∞
        PID.15 - Primary Language	250	CE	O 	-	0296
        PID.16 - Marital Status	250	CE	O 	-	0002
        PID.17 - Religion	250	CE	O 	-	0006
        PID.18 - Patient Account Number	250	CX	O 	-
        PID.19 - SSN Number - Patient	16	ST	B 	-
        PID.20 - Driver's License Number - Patient	25	DLN	B 	-
        PID.21 - Mother's Identifier	250	CX	O 	∞
        PID.22 - Ethnic Group	250	CE	O 	∞	0189
        PID.23 - Birth Place	250	ST	O 	-
        PID.24 - Multiple Birth Indicator	1	ID	O 	-	0136
        PID.25 - Birth Order	2	NM	O 	-
        PID.26 - Citizenship	250	CE	O 	∞	0171
        PID.27 - Veterans Military Status	250	CE	O 	-	0172
        PID.28 - Nationality	250	CE	B 	-	0212
        PID.29 - Patient Death Date and Time	26	TS	O 	-
        PID.30 - Patient Death Indicator	1	ID	O 	-	0136
        PID.31 - Identity Unknown Indicator	1	ID	O 	-	0136
        PID.32 - Identity Reliability Code	20	IS	O 	∞	0445
        PID.33 - Last Update Date/Time	26	TS	O 	-
        PID.34 - Last Update Facility	241	HD	O 	-
        PID.35 - Species Code	250	CE	C 	-	0446
        PID.36 - Breed Code	250	CE	C 	-	0447
        PID.37 - Strain	80	ST	O 	-
        PID.38 - Production Class Code	250	CE	O 	2	0429
        PID.39 - Tribal Citizenship
         */

        return sb.toString();
    }

    private String streamHL7PTType(HL7PTType hl7PTType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7PTType) return sb.toString();

        sb.append(hl7PTType.getHL7ProcessingID());

        if(null != hl7PTType.getHL7ProcessingMode()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7PTType.getHL7ProcessingMode());
        }

        return sb.toString();
    }

    private String streamHL7MSGType(HL7MSGType hl7MSGType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7MSGType) return sb.toString();

        if(null != hl7MSGType.getMessageCode()) {
            sb.append(hl7MSGType.getMessageCode());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if(null != hl7MSGType.getTriggerEvent()) {
            sb.append(hl7MSGType.getTriggerEvent());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if(null != hl7MSGType.getMessageStructure()) {
            sb.append(hl7MSGType.getMessageStructure());
        }

        return sb.toString();
    }

    private String streamHL7VIDType(HL7VIDType hl7VIDType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7VIDType) return sb.toString();
        sb.append(hl7VIDType.getHL7VersionID());
        return sb.toString();
    }


    private String streamHL7TSType(HL7TSType hl7TSType) {
        return streamHL7TSType(hl7TSType, false);
    }

    private String streamHL7TSType(HL7TSType hl7TSType, boolean shortForm) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7TSType) return sb.toString();

        sb.append(String.format("%04d%02d%02d",
                hl7TSType.getYear(), hl7TSType.getMonth(), hl7TSType.getDay()));

        if (null != hl7TSType.getHours()) {
            sb.append(String.format("%02d", hl7TSType.getHours()));
        }
        else {
            sb.append("00");
        }

        if (null != hl7TSType.getMinutes()) {
            sb.append(String.format("%02d", hl7TSType.getMinutes()));
        }
        else {
            sb.append("00");
        }

        if( !shortForm ) {
            if (null != hl7TSType.getSeconds()) {
                sb.append(String.format("%02d", hl7TSType.getSeconds()));
            } else {
                sb.append("00");
            }
        }

        return sb.toString();
    }

    private String streamHL7XTNType(HL7XTNType hl7XTNType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7XTNType) return sb.toString();

        if(null != hl7XTNType.getHL7TelephoneNumber()) {
            sb.append(hl7XTNType.getHL7TelephoneNumber());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if(null != hl7XTNType.getHL7TelecommunicationUseCode()) {
            sb.append(hl7XTNType.getHL7TelecommunicationUseCode());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if(null != hl7XTNType.getHL7TelecommunicationEquipmentType()) {
            sb.append(hl7XTNType.getHL7TelecommunicationEquipmentType());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if(null != hl7XTNType.getHL7EmailAddress()) {
            sb.append(hl7XTNType.getHL7EmailAddress());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if((null != hl7XTNType.getHL7CountryCode()) && (null != hl7XTNType.getHL7CountryCode().getHL7Numeric())) {
            sb.append(hl7XTNType.getHL7CountryCode().getHL7Numeric().intValue());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if((null != hl7XTNType.getHL7AreaCityCode()) && (null != hl7XTNType.getHL7AreaCityCode().getHL7Numeric())) {
            sb.append(hl7XTNType.getHL7AreaCityCode().getHL7Numeric().intValue());
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        if((null != hl7XTNType.getHL7LocalNumber()) && (null != hl7XTNType.getHL7LocalNumber().getHL7Numeric())) {
            sb.append(hl7XTNType.getHL7LocalNumber().getHL7Numeric().intValue());

        }

        if((null != hl7XTNType.getHL7Extension()) && (null != hl7XTNType.getHL7Extension().getHL7Numeric())) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XTNType.getHL7Extension().getHL7Numeric().intValue());
        }

        if(null != hl7XTNType.getHL7AnyText()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XTNType.getHL7AnyText());
        }

        if(null != hl7XTNType.getHL7ExtensionPrefix()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XTNType.getHL7ExtensionPrefix());
        }

        if(null != hl7XTNType.getHL7SpeedDialCode()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XTNType.getHL7SpeedDialCode());
        }

        if(null != hl7XTNType.getHL7UnformattedTelephonenumber()) {
            sb.append(ATTRIBUTES_SEPARATOR);
            sb.append(hl7XTNType.getHL7UnformattedTelephonenumber());
        }

        return sb.toString();
    }

    private String streamHL7NTEType(HL7NTEType nteType) {
        StringBuilder sb = new StringBuilder();

        if(null == nteType) return sb.toString();

        sb.append("NTE");
        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7SIType(nteType.getHL7SetIDNTE()));
        sb.append(COLUMNS_SEPARATOR);

        if(null != nteType.getHL7SourceOfComment()) {
            sb.append(nteType.getHL7SourceOfComment());
        }

        sb.append(COLUMNS_SEPARATOR);
        for(String s : nteType.getHL7Comment()) {
            sb.append(s);
            sb.append(ATTRIBUTES_SEPARATOR);
        }

        sb.append(COLUMNS_SEPARATOR);
        sb.append(streamHL7CWEType(nteType.getHL7CommentType()));
        sb.append(COLUMNS_SEPARATOR);

        return sb.toString();
    }

    private String streamHL7NTETypeList(List<HL7NTEType> nteTypeList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == nteTypeList) return sb.toString();

        for(HL7NTEType nteType : nteTypeList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7NTEType(nteType));
        }

        return sb.toString();
    }

    private String streamHL7XTNTypeList(List<HL7XTNType> xtnTypeList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == xtnTypeList) return sb.toString();

        for(HL7XTNType xtnType : xtnTypeList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7XTNType(xtnType));
        }

        return sb.toString();
    }

    private String streamHL7XADTypeList(List<HL7XADType> xadTypeList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == xadTypeList) return sb.toString();

        for(HL7XADType xadType : xadTypeList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7XADType(xadType));
        }

        return sb.toString();
    }

    private String streamHL7XCNTypeList(List<HL7XCNType> xcnTypeList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == xcnTypeList) return sb.toString();

        for(HL7XCNType xcnType : xcnTypeList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7XCNType(xcnType));
        }

        return sb.toString();
    }

    private String streamHL7XONTypeList(List<HL7XONType> xonList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == xonList) return sb.toString();

        for(HL7XONType xonType : xonList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7XONType(xonType));
        }

        return sb.toString();
    }

    private String streamHL7XPNTypeList(List<HL7XPNType> xpnList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == xpnList) return sb.toString();

        for(HL7XPNType xpnType : xpnList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7XPNType(xpnType));
        }

        return sb.toString();
    }

    private String streamHL7CXTypeList(List<HL7CXType> cxList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == cxList) return sb.toString();

        for(HL7CXType ciType : cxList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7CXType(ciType));

        }

        return sb.toString();
    }

    private String streamHL7SITypeList(List<HL7SIType> siList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == siList) return sb.toString();

        for(HL7SIType siType : siList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7SIType(siType));
        }

        return sb.toString();
    }

    private String streamHL7CWETypeList(List<HL7CWEType> cweList) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();

        if(null == cweList) return sb.toString();

        for(HL7CWEType cweType : cweList) {
            if( isFirst ) {
                isFirst = !isFirst;
            }
            else {
                sb.append(LISTS_SEPARATOR);
            }

            sb.append(streamHL7CWEType(cweType));
        }

        return sb.toString();
    }

    private String streamHL7XCNType(HL7XCNType xcnType) {
        StringBuilder sb = new StringBuilder();

        if(null == xcnType) return sb.toString();

        sb.append(xcnType.getHL7IDNumber());
        sb.append(ATTRIBUTES_SEPARATOR);
        sb.append(streamHL7FNType(xcnType.getHL7FamilyName()));
        sb.append(ATTRIBUTES_SEPARATOR);
        sb.append(xcnType.getHL7GivenName());
        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xcnType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof()) {
            sb.append(xcnType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xcnType.getHL7Suffix()) {
            sb.append(xcnType.getHL7Suffix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xcnType.getHL7Prefix()) {
            sb.append(xcnType.getHL7Prefix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xcnType.getHL7Degree()) {
            sb.append(xcnType.getHL7Degree());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xcnType.getHL7SourceTable()) {
            sb.append(xcnType.getHL7SourceTable());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        /*
        XCN.9 - Assigning Authority	227	HD	O 	-	0363
        XCN.10 - Name Type Code	1	ID	O 	-	0200
        XCN.11 - Identifier Check Digit	1	ST	O 	-
        XCN.12 - Check Digit Scheme	3	ID	C 	-	0061
        */

        if(null != xcnType.getHL7IdentifierTypeCode()) {
            sb.append(xcnType.getHL7IdentifierTypeCode());
        }

        sb.append(ATTRIBUTES_SEPARATOR);


        /*
        XCN.14 - Assigning Facility	227	HD	O 	-
        XCN.15 - Name Representation Code	1	ID	O 	-	0465
        XCN.16 - Name Context	483	CE	O 	-	0448
        XCN.17 - Name Validity Range	53	DR	B 	-
        XCN.18 - Name Assembly Order	1	ID	O 	-	0444
        XCN.19 - Effective Date	26	TS	O 	-
        XCN.20 - Expiration Date	26	TS	O 	-
        XCN.21 - Professional Suffix	199	ST	O 	-
        XCN.22 - Assigning Jurisdiction	705	CWE	O 	-
        XCN.23 - Assigning Agency Or Department
        */

        return sb.toString();
    }

    private String streamHL7XONType(HL7XONType xonType) {
        StringBuilder sb = new StringBuilder();

        if(null == xonType) return sb.toString();

        sb.append(xonType.getHL7OrganizationName());
        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xonType.getHL7OrganizationNameTypeCode() && (xonType.getHL7OrganizationNameTypeCode().length() > 0)) {
            sb.append(xonType.getHL7OrganizationNameTypeCode());
        }


        /*
        XON.3 - Id Number	4	NM	B 	-
        XON.4 - Check Digit	1	NM	O 	-
        XON.5 - Check Digit Scheme	3	ID	O 	-	0061
        XON.6 - Assigning Authority	227	HD	O 	-	0363
        XON.7 - Identifier Type Code	5	ID	O 	-	0203
        XON.8 - Assigning Facility	227	HD	O 	-
        XON.9 - Name Representation Code	1	ID	O 	-	0465
        XON.10 - Organization Identifier
        */

        return sb.toString();
    }

    private String streamHL7XPNType(HL7XPNType xpnType) {
        StringBuilder sb = new StringBuilder();

        if(null == xpnType) return sb.toString();

        sb.append(streamHL7FNType(xpnType.getHL7FamilyName()));
        sb.append(ATTRIBUTES_SEPARATOR);
        sb.append(xpnType.getHL7GivenName());
        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xpnType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof()) {
            sb.append(xpnType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xpnType.getHL7Suffix()) {
            sb.append(xpnType.getHL7Suffix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xpnType.getHL7Prefix()) {
            sb.append(xpnType.getHL7Prefix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xpnType.getHL7Degree()) {
            sb.append(xpnType.getHL7Degree());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != xpnType.getHL7NameTypeCode()) {
            sb.append(xpnType.getHL7NameTypeCode());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        /*
			<xs:element name="HL7NameRepresentationCode" type="nbs:HL7IDType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="1"/-->
			<xs:element name="HL7NameContext" type="nbs:HL7CEType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="483"/-->
			<xs:element name="HL7NameValidityRange" type="nbs:HL7DRType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="53"/-->
			<xs:element name="HL7NameAssemblyOrder" type="nbs:HL7IDType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="1"/-->
			<xs:element name="HL7EffectiveDate" type="nbs:HL7TSType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="26"/-->
			<xs:element name="HL7ExpirationDate" type="nbs:HL7TSType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="26"/-->
			<xs:element name="HL7ProfessionalSuffix" type="nbs:HL7STType" minOccurs="0" maxOccurs="1"/>
			<!--maxLength="199"/-->
         */


        return sb.toString();
    }

    private String streamHL7FNType(HL7FNType fnType) {
        StringBuilder sb = new StringBuilder();

        if(null == fnType) return sb.toString();

        sb.append(fnType.getHL7Surname());
        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != fnType.getHL7OwnSurnamePrefix()) {
            sb.append(fnType.getHL7OwnSurnamePrefix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != fnType.getHL7OwnSurnamePrefix()) {
            sb.append(fnType.getHL7OwnSurnamePrefix());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        if(null != fnType.getHL7SurnamePrefixFromPartnerSpouse()) {
            sb.append(fnType.getHL7SurnamePrefixFromPartnerSpouse());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        if(null != fnType.getHL7SurnameFromPartnerSpouse()) {
            sb.append(fnType.getHL7SurnameFromPartnerSpouse());
        }

        return sb.toString();
    }

    private String streamHL7SIType(HL7SIType siType) {
        StringBuilder sb = new StringBuilder();

        if(null == siType) return sb.toString();

        sb.append(siType.getHL7SequenceID());

        return sb.toString();
    }

    public String streamHL7CXType(HL7CXType hl7CXType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7CXType) return sb.toString();

        if(null != hl7CXType.getHL7IDNumber()) {
            sb.append(hl7CXType.getHL7IDNumber());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        if(null != hl7CXType.getHL7CheckDigit()) {
            sb.append(hl7CXType.getHL7CheckDigit());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        if(null != hl7CXType.getHL7CheckDigitScheme()) {
            sb.append(hl7CXType.getHL7CheckDigitScheme());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        sb.append(streamHL7HDType(hl7CXType.getHL7AssigningAuthority()));

        sb.append(ATTRIBUTES_SEPARATOR);
        if( null != hl7CXType.getHL7IdentifierTypeCode()) {
            sb.append(hl7CXType.getHL7IdentifierTypeCode());
        }

        sb.append(ATTRIBUTES_SEPARATOR);
        sb.append(streamHL7HDType(hl7CXType.getHL7AssigningFacility()));
        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != hl7CXType.getHL7EffectiveDate()) {
            sb.append(hl7CXType.getHL7EffectiveDate());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != hl7CXType.getHL7ExpirationDate()) {
            sb.append(hl7CXType.getHL7ExpirationDate());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != hl7CXType.getHL7AssigningJurisdiction()) {
            sb.append(streamHL7CWEType(hl7CXType.getHL7AssigningJurisdiction()));
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != hl7CXType.getHL7AssigningAgencyOrDepartment()) {
            sb.append(streamHL7CWEType(hl7CXType.getHL7AssigningAgencyOrDepartment()));
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        return sb.toString();
    }

    private String streamHL7CWEType(HL7CWEType hl7CWEType) {
        StringBuilder sb = new StringBuilder();

        if (null == hl7CWEType) return sb.toString();

        if (null != hl7CWEType.getHL7Identifier()) {
            sb.append(hl7CWEType.getHL7Identifier());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7Text()) {
            sb.append(hl7CWEType.getHL7Text());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7NameofCodingSystem()) {
            sb.append(hl7CWEType.getHL7NameofCodingSystem());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7AlternateIdentifier()) {
            sb.append(hl7CWEType.getHL7AlternateIdentifier());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7AlternateText()) {
            sb.append(hl7CWEType.getHL7AlternateText());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7NameofAlternateCodingSystem()) {
            sb.append(hl7CWEType.getHL7NameofAlternateCodingSystem());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7CodingSystemVersionID()) {
            sb.append(hl7CWEType.getHL7CodingSystemVersionID());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if (null != hl7CWEType.getHL7AlternateCodingSystemVersionID()) {
            sb.append(hl7CWEType.getHL7AlternateCodingSystemVersionID());
        }

        sb.append(ATTRIBUTES_SEPARATOR);

        if(null != hl7CWEType.getHL7OriginalText()) {
            sb.append(hl7CWEType.getHL7OriginalText());
        }

        return sb.toString();
    }

    private String streamHL7HDType(HL7HDType hl7HDType) {
        StringBuilder sb = new StringBuilder();

        if(null == hl7HDType) return sb.toString();

        if(null != hl7HDType.getHL7NamespaceID()) {
            sb.append(hl7HDType.getHL7NamespaceID());
        }

        sb.append(INNTER_ATTRIBUTES_SEPARATOR);
        if(null != hl7HDType.getHL7UniversalID()) {
            sb.append(hl7HDType.getHL7UniversalID());
        }

        sb.append(INNTER_ATTRIBUTES_SEPARATOR);
        if(null != hl7HDType.getHL7UniversalIDType()) {
            sb.append(hl7HDType.getHL7UniversalIDType());
        }

        return sb.toString();
    }

    private MessageHeader buildMessageHeader() {
        MessageHeader mh = new MessageHeader();

        return mh;
    }
}
