package gov.cdc.dataingestion.nbs.converters;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.FinancialTransaction;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ClinicalTrialIdentification;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ContactData;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisit;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantity;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantityRelationship;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Jcc;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ei;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cne;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cwe;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Pl;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Hd;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xon;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xpn;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xad;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xtn;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Pln;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Sps;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Moc;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Mo;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Prl;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ndl;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ts;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cnn;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xcn;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Tq;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cq;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Rpt;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Eip;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Fn;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Dr;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cp;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cx;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Fc;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Dld;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Dln;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ri;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Vid;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Osd;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Sad;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Pt;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientAdditionalDemographic;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.OrderObservation;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Specimen;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Observation;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.TimingQty;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Patient;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Visit;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.PatientResult;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import  gov.cdc.dataingestion.hl7.helper.HL7Helper;
import  gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;

import  gov.cdc.dataingestion.nbs.jaxb.HL7PATIENTType;
import  gov.cdc.dataingestion.nbs.jaxb.Container;
import  gov.cdc.dataingestion.nbs.jaxb.HL7LabReportType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PATIENTRESULTType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7OrderObservationType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PatientResultSPMType;
import  gov.cdc.dataingestion.nbs.jaxb.PatientResultOrderObservation;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CTIType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TIMINGQuantiyType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TIMINGQTYType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SPECIMENType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7OBSERVATIONType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CNEType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CWEType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PLType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7XONType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7VisitType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7NTEType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CTDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PLNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SIType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SPSType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7MOCType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7MOType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PRLType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7NDLType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CNNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7OBRType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TQ2Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7XCNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TQ1Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7RPTType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7OBXType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SPMType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CPType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7FT1Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PIV2Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TMType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7DLDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7NK1Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7JCCType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PID1Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PIDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7ORCType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7ContinuationPointerType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7EIPType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7DLNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TQType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7OSDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TXType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7RIType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CQType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7XTNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7XADType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SADType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7XPNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7DRType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7FNType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CXType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7SoftwareSegmentType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7MSHType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7EIType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7NMType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7VIDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7CEType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PTType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7MSGType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7TSType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7HDType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7DTType;
import  gov.cdc.dataingestion.nbs.jaxb.HL7PIV1Type;
import  gov.cdc.dataingestion.nbs.jaxb.HL7FCType;

import  jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import  jakarta.xml.bind.Marshaller;

import  jakarta.xml.bind.annotation.XmlElement;
import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  java.io.ByteArrayOutputStream;
import  java.io.File;
import  java.io.IOException;
import  java.math.BigInteger;
import  java.time.format.DateTimeFormatter;
import  java.time.LocalDateTime;
import  java.util.List;
import  java.util.ArrayList;
import  org.apache.commons.lang3.StringUtils;

public class Hl7ToRhapsodysXmlConverter {
    private static final Logger log = LoggerFactory.getLogger(Hl7ToRhapsodysXmlConverter.class);
    private static final Hl7ToRhapsodysXmlConverter instance = new Hl7ToRhapsodysXmlConverter();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter formatterWithZone = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
    private static final String OBX_VALUE_TYPE_SN = "SN";
    private static final String EMPTY_STRING = "";
    private static final String CARET_SEPARATOR = "\\^";
    private static final int TS_FMT_ALL = 0;
    private static final int TS_FMT_DATE_ONLY = 1;
    private static final int TS_FMT_DATE_HOUR_ONLY = 2;
    private static final int TS_FMT_DATE_HOUR_MINUTE_ONLY = 3;

    public static Hl7ToRhapsodysXmlConverter getInstance() {
        return instance;
    }

    // For Unit Test
    public Hl7ToRhapsodysXmlConverter() {
    }


    public String convert(String raw_message_id, String hl7Msg) throws JAXBException, IOException, DiHL7Exception {
        String rhapsodyXml = "";

        HL7Helper hl7Helper = new HL7Helper();
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Msg);

        Container c = new Container();

        HL7LabReportType lbt = buildHL7LabReportType(hl7ParsedMsg);
        c.setHL7LabReport(lbt);

        JAXBContext contextObj = JAXBContext.newInstance(Container.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshallerObj.marshal(c, baos);
        baos.flush();

        rhapsodyXml = baos.toString();

        // add raw_message_id as a comment
        rhapsodyXml = rhapsodyXml + "\n" + buildTrailingComments(raw_message_id);

        //saveXmlToTempFile(rhapsodyXml);     // Ramesh

        return rhapsodyXml;
    }

    //TODO: DEAD CODE
    private void saveXmlToTempFile(String rhapsodyXml) {
        String pathToHome = System.getenv("HOME");
        String fileFullpath = pathToHome + File.separator + "gen101.xml";

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileFullpath);
            java.nio.file.Files.writeString(path, rhapsodyXml, java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException ex) {
            log.error("Invalid path: " + fileFullpath);
        }
    }

    private String buildTrailingComments(String raw_message_id) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!-- ");
        sb.append("raw_message_id = ");
        sb.append(raw_message_id);
        sb.append(" -->");

        return sb.toString();
    }

    private HL7LabReportType buildHL7LabReportType(HL7ParsedMessage hl7ParsedMsg) {
        HL7LabReportType lbt = new HL7LabReportType();

        if (!hl7ParsedMsg.getParsedMessage().getClass().isNestmateOf(OruR1.class)) {
            log.error("Unidentified Hl7 message type, supported type(s): OruR1");
            return lbt;
        }

        OruR1 oruR1 = (OruR1) hl7ParsedMsg.getParsedMessage();

        lbt.setHL7MSH(buildHL7MSHType(oruR1.getMessageHeader()));
        lbt.getHL7SoftwareSegment().addAll(buildSoftwareSegments(oruR1));

        for (PatientResult pr : oruR1.getPatientResult()) {
            lbt.getHL7PATIENTRESULT().add(buildHL7PATIENTRESULTType(pr));
        }

        if (!isEmptyHL7ContinuationPointerType(oruR1)) {
            lbt.setHL7ContinuationPointer(buildHL7ContinuationPointerType(oruR1));
        }

        return lbt;
    }

    private HL7PATIENTRESULTType buildHL7PATIENTRESULTType(PatientResult pr) {
        HL7PATIENTRESULTType hl7PATIENTRESULTType = new HL7PATIENTRESULTType();

        hl7PATIENTRESULTType.setPATIENT(buildHL7PATIENTType(pr.getPatient()));
        for (OrderObservation oo : pr.getOrderObservation()) {
            hl7PATIENTRESULTType.getORDEROBSERVATION().add(buildHL7OrderObservationType(oo));
        }

        validateHL7PATIENTRESULTType(hl7PATIENTRESULTType.getORDEROBSERVATION());

        return hl7PATIENTRESULTType;
    }

    private void validateHL7PATIENTRESULTType(List<HL7OrderObservationType> listOfOOTypes) {
        HL7OBRType assumedParentOBRType = null;
        HL7OBRType assumedChildOBRType = null;

        for (HL7OrderObservationType ooType : listOfOOTypes) {
            HL7OBRType hl7OBRType = ooType.getObservationRequest();
            if ((null == hl7OBRType.getParent()) && (null == hl7OBRType.getParentResult())) {
                if (null == assumedParentOBRType) {
                    assumedParentOBRType = hl7OBRType;
                    break;
                }
            }
        }

        for (HL7OrderObservationType ooType : listOfOOTypes) {
            HL7OBRType hl7OBRType = ooType.getObservationRequest();
            if ((null != hl7OBRType.getParent()) && (null != hl7OBRType.getParentResult())) {
                if (null == assumedChildOBRType) {
                    assumedChildOBRType = hl7OBRType;
                    break;
                }
            }
        }

        for (HL7OrderObservationType ooType : listOfOOTypes) {
            HL7OBRType hl7OBRType = ooType.getObservationRequest();
            if ((null == hl7OBRType.getParent()) && (null == hl7OBRType.getParentResult())) {
                if ((assumedParentOBRType != hl7OBRType) && (assumedChildOBRType != hl7OBRType)) {
                    hl7OBRType.setParent(assumedChildOBRType.getParent());
                    hl7OBRType.setParentResult(assumedChildOBRType.getParentResult());
                }
            }
        }
    }

    private HL7OrderObservationType buildHL7OrderObservationType(OrderObservation oo) {
        HL7OrderObservationType hl7OrderObservationType = new HL7OrderObservationType();

        if( !isEmptyHL7ORCType(oo.getCommonOrder()) ) {
            hl7OrderObservationType.setCommonOrder(buildHL7ORCType(oo.getCommonOrder()));
        }

        hl7OrderObservationType.setObservationRequest(buildHL7OBRType(oo.getObservationRequest()));

        for (NoteAndComment nac : oo.getNoteAndComment()) {
            hl7OrderObservationType.getNotesAndComments().add(buildHL7NTEType(nac));
        }

        //hl7OrderObservationType.setHL7TIMINGQuantiyType(buildHL7TIMINGQuantiyType(oo.getTimingQty()));

        if (!isEmptyHL7CTDType(oo.getContactData())) {
            hl7OrderObservationType.setContactData(buildHL7CTDType(oo.getContactData()));
        }

        hl7OrderObservationType.setPatientResultOrderObservation(buildPatientResultOrderObservation(oo.getObservation()));

        for (FinancialTransaction ft : oo.getFinancialTransaction()) {
            hl7OrderObservationType.getFinancialTransaction().add(buildHL7FT1Type(ft));
        }

        for (ClinicalTrialIdentification cti : oo.getClinicalTrialIdentification()) {
            hl7OrderObservationType.getClinicalTrialIdentification().add(buildHL7CTIType(cti));
        }

        if ((null != oo.getSpecimen() && (oo.getSpecimen().size() > 0))) {
            for (Specimen specimen : oo.getSpecimen()) {
                hl7OrderObservationType.setPatientResultOrderSPMObservation(buildHL7PatientResultSPMType(oo.getSpecimen()));
            }
        }

        return hl7OrderObservationType;
    }

    private HL7PatientResultSPMType buildHL7PatientResultSPMType(List<Specimen> specimenList) {
        HL7PatientResultSPMType hl7PatientResultSPMType = new HL7PatientResultSPMType();

        for (Specimen s : specimenList) {
            hl7PatientResultSPMType.getSPECIMEN().add(buildHL7SPECIMENType(s));
        }

        return hl7PatientResultSPMType;
    }

    private PatientResultOrderObservation buildPatientResultOrderObservation(List<Observation> observations) {
        PatientResultOrderObservation proo = new PatientResultOrderObservation();

        for (Observation o : observations) {
            proo.getOBSERVATION().add(buildHL7OBSERVATIONType(o));
        }

        return proo;
    }

    private HL7CTIType buildHL7CTIType(ClinicalTrialIdentification cti) {
        HL7CTIType hl7CTIType = new HL7CTIType();

        hl7CTIType.setSponsorStudyID(buildHL7EIType(cti.getSponsorStudyId()));
        hl7CTIType.setStudyPhaseIdentifier(buildHL7CEType(cti.getStudyPhaseIdentifier()));
        hl7CTIType.setStudyScheduledTimePoint(buildHL7CEType(cti.getStudyScheduledTimePoint()));

        return hl7CTIType;
    }

    //TODO: Dead Code
    private HL7TIMINGQuantiyType buildHL7TIMINGQuantiyType(List<TimingQty> timingQty) {
        HL7TIMINGQuantiyType hl7TIMINGQuantiyType = new HL7TIMINGQuantiyType();

        for (TimingQty tq : timingQty) {
            hl7TIMINGQuantiyType.getTIMINGQTY().add(buildHL7TIMINGQTYType(tq));
        }

        return hl7TIMINGQuantiyType;
    }

    private HL7TIMINGQTYType buildHL7TIMINGQTYType(TimingQty tq) {
        HL7TIMINGQTYType hl7TIMINGQTYType = new HL7TIMINGQTYType();

        hl7TIMINGQTYType.setTimingQuantity(buildHL7TQ1Type(tq.getTimeQuantity()));

        for (TimingQuantityRelationship tqr : tq.getTimeQuantityRelationship()) {
            hl7TIMINGQTYType.getTimingQuantityRelationship().add(buildHL7TQ2Type(tqr));
        }

        return hl7TIMINGQTYType;
    }

    private HL7SPECIMENType buildHL7SPECIMENType(Specimen s) {
        HL7SPECIMENType hl7SPECIMENType = new HL7SPECIMENType();

        hl7SPECIMENType.setSPECIMEN(buildHL7SPMType(s.getSpecimen()));
        for (ObservationResult or : s.getObservationResult()) {
            hl7SPECIMENType.getObservationResult().add(buildHL7OBXType(or));
        }

        return hl7SPECIMENType;
    }

    private HL7OBSERVATIONType buildHL7OBSERVATIONType(Observation o) {
        HL7OBSERVATIONType hl7OBSERVATIONType = new HL7OBSERVATIONType();

        hl7OBSERVATIONType.setObservationResult(buildHL7OBXType(o.getObservationResult()));

        for (NoteAndComment nac : o.getNoteAndComments()) {
            hl7OBSERVATIONType.getNotesAndComments().add(buildHL7NTEType(nac));
        }

        return hl7OBSERVATIONType;
    }

    private HL7CNEType buildHL7CNEType(Cne cne) {
        HL7CNEType hl7CNEType = new HL7CNEType();

        hl7CNEType.setHL7Identifier(cne.getIdentifier());

        if( StringUtils.isNotEmpty(cne.getText())) {
            hl7CNEType.setHL7Text(cne.getText());
        }

        if( StringUtils.isNotEmpty(cne.getNameOfAlternateCodingSystem())) {
             hl7CNEType.setHL7NameofCodingSystem(cne.getNameOfAlternateCodingSystem());
        }

        if( StringUtils.isNotEmpty(cne.getAlternateIdentifier())) {
            hl7CNEType.setHL7AlternateIdentifier(cne.getAlternateIdentifier());
        }

        if( StringUtils.isNotEmpty(cne.getAlternateText())) {
            hl7CNEType.setHL7AlternateText(cne.getAlternateText());
        }

        if( StringUtils.isNotEmpty(cne.getNameOfAlternateCodingSystem())) {
            hl7CNEType.setHL7NameofAlternateCodingSystem(cne.getNameOfAlternateCodingSystem());
        }

        if( StringUtils.isNotEmpty(cne.getCodingSystemVersionId())) {
            hl7CNEType.setHL7CodingSystemVersionID(cne.getCodingSystemVersionId());
        }

        if( StringUtils.isNotEmpty(cne.getAlternateCodingSystemVersionId())) {
            hl7CNEType.setHL7AlternateCodingSystemVersionID(cne.getAlternateCodingSystemVersionId());
        }

        if( StringUtils.isNotEmpty(cne.getOriginalText())) {
            hl7CNEType.setHL7OriginalText(cne.getOriginalText());
        }

        return hl7CNEType;
    }

    private HL7CWEType buildHL7CWEType(Cwe cwe) {
        HL7CWEType hl7CWEType = new HL7CWEType();

        hl7CWEType.setHL7Identifier(cwe.getIdentifier());

        if( StringUtils.isNotEmpty(cwe.getText())) {
            hl7CWEType.setHL7Text(cwe.getText());
        }

        if( StringUtils.isNotEmpty(cwe.getNameOfCodingSystem())) {
            hl7CWEType.setHL7NameofCodingSystem(cwe.getNameOfCodingSystem());
        }

        if( StringUtils.isNotEmpty(cwe.getAlternateIdentifier())) {
            hl7CWEType.setHL7AlternateIdentifier(cwe.getAlternateIdentifier());
        }

        if( StringUtils.isNotEmpty(cwe.getAlternateText())) {
            hl7CWEType.setHL7AlternateText(cwe.getAlternateText());
        }

        if( StringUtils.isNotEmpty(cwe.getNameOfAlterCodeSystem())) {
            hl7CWEType.setHL7NameofAlternateCodingSystem(cwe.getNameOfAlterCodeSystem());
        }

        if( StringUtils.isNotEmpty(cwe.getCodeSystemVerId())) {
            hl7CWEType.setHL7CodingSystemVersionID(cwe.getCodeSystemVerId());
        }

        if( StringUtils.isNotEmpty(cwe.getAlterCodeSystemVerId())) {
            hl7CWEType.setHL7AlternateCodingSystemVersionID(cwe.getAlterCodeSystemVerId());
        }

        if( StringUtils.isNotEmpty(cwe.getOriginalText())) {
            hl7CWEType.setHL7OriginalText(cwe.getOriginalText());
        }

        return hl7CWEType;
    }

    private HL7PLType buildHL7PLType(Pl pl) {
        HL7PLType hl7PLType = new HL7PLType();

        hl7PLType.setHL7PointofCare(pl.getPointOfCare());
        hl7PLType.setHL7Room(pl.getRoom());
        hl7PLType.setHL7Bed(pl.getBed());

        if (!isEmptyHL7HDType(pl.getFacility())) {
            hl7PLType.setHL7Facility(buildHL7HDType(pl.getFacility()));
        }

        hl7PLType.setHL7LocationStatus(pl.getLocationStatus());
        hl7PLType.setHL7PersonLocationType(pl.getPersonLocationType());
        hl7PLType.setHL7Building(pl.getBuilding());
        hl7PLType.setHL7Floor(pl.getFloor());
        hl7PLType.setHL7LocationDescription(pl.getLocationDescription());
        hl7PLType.setHL7ComprehensiveLocationIdentifier(buildHL7EIType(pl.getComprehensiveLocationIdentifier()));
        hl7PLType.setHL7AssigningAuthorityforLocation(buildHL7HDType(pl.getAssignAuthorityForLocation()));

        return hl7PLType;
    }

    private HL7XONType buildHL7XONType(Xon xon) {
        HL7XONType hl7XONType = new HL7XONType();

        hl7XONType.setHL7OrganizationName(xon.getOrganizationName());

        if( StringUtils.isNotEmpty(xon.getOrganizationNameTypeCode())) {
            hl7XONType.setHL7OrganizationNameTypeCode(xon.getOrganizationNameTypeCode());
        }

        hl7XONType.setHL7IDNumber(buildHl7NMType(xon.getIdNumber()));
        hl7XONType.setHL7CheckDigit(buildHl7NMType(xon.getCheckDigit()));
        hl7XONType.setHL7CheckDigitScheme(xon.getCheckDigitScheme());

        if (!isEmptyHL7HDType(xon.getAssignAuthority())) {
            hl7XONType.setHL7AssigningAuthority(buildHL7HDType(xon.getAssignAuthority()));
        }

        hl7XONType.setHL7IdentifierTypeCode(xon.getIdentifierTypeCode());

        if (!isEmptyHL7HDType(xon.getAssignFacility())) {
            hl7XONType.setHL7AssigningFacility(buildHL7HDType(xon.getAssignFacility()));
        }

        hl7XONType.setHL7NameRepresentationCode(xon.getNameRepresentationCode());
        hl7XONType.setHL7OrganizationIdentifier(xon.getOrganizationIdentifier());

        return hl7XONType;
    }

    private HL7PATIENTType buildHL7PATIENTType(Patient p) {
        HL7PATIENTType hl7PATIENTType = new HL7PATIENTType();

        hl7PATIENTType.setPatientIdentification(buildHL7PIDType(p.getPatientIdentification()));

        if (!isEmptyHL7PID1Type(p.getPatientAdditionalDemographic())) {
            hl7PATIENTType.setPatientAdditionalDemographic(buildHL7PID1Type(p.getPatientAdditionalDemographic()));
        }

        for (NoteAndComment nac : p.getNoteAndComment()) {
            hl7PATIENTType.getNotesandComments().add(buildHL7NTEType(nac));
        }

        for (NextOfKin nok : p.getNextOfKin()) {
            hl7PATIENTType.getNextofKinAssociatedParties().add(buildHL7NK1Type(nok));
        }

        if (!isEmptyHL7VisitType(p.getVisit())) {
            hl7PATIENTType.setVisit(buildHL7VisitType(p.getVisit()));
        }

        return hl7PATIENTType;
    }

    private HL7VisitType buildHL7VisitType(Visit v) {
        HL7VisitType hl7VisitType = new HL7VisitType();

        if (!isEmptyHL7PIV1Type(v.getPatientVisit())) {
            hl7VisitType.setPatientVisit(buildHL7PIV1Type(v.getPatientVisit()));
        }

        if (!isEmptyHL7PIV2Type(v.getPatientVisitAdditional())) {
            hl7VisitType.setPatientVisitAdditionalInformation(buildHL7PIV2Type(v.getPatientVisitAdditional()));
        }

        return hl7VisitType;
    }

    private HL7NTEType buildHL7NTEType(NoteAndComment noteAndComment) {
        HL7NTEType hl7NTEType = new HL7NTEType();

        HL7SIType hl7SIType = new HL7SIType();
        hl7SIType.setHL7SequenceID(noteAndComment.getSetIdNte());

        hl7NTEType.setHL7SetIDNTE(hl7SIType);
        hl7NTEType.setHL7SourceOfComment(noteAndComment.getSourceOfComment());

        for (String s : noteAndComment.getComment()) {
            hl7NTEType.getHL7Comment().add(s);
        }

        hl7NTEType.setHL7CommentType(buildHL7CWEType(noteAndComment.getCommentType()));

        return hl7NTEType;
    }

    private HL7CTDType buildHL7CTDType(ContactData contactData) {
        HL7CTDType hl7CTDType = new HL7CTDType();

        if ((null == contactData.getContactRole()) || (contactData.getContactRole().size() <= 0)) {
            hl7CTDType.getContactRole().add(buildHL7CEType(null));
        } else {
            for (Ce ce : contactData.getContactRole()) {
                hl7CTDType.getContactRole().add(buildHL7CEType(ce));
            }
        }

        for (Xpn xpn : contactData.getContactName()) {
            hl7CTDType.getContactName().add(buildHL7XPNType(xpn));
        }

        for (Xad xad : contactData.getContactAddress()) {
            hl7CTDType.getContactAddress().add(buildHL7XADType(xad));
        }

        hl7CTDType.setContactLocation(buildHL7PLType(contactData.getContactLocation()));

        for (Xtn xtn : contactData.getContactCommunicationInformation()) {
            hl7CTDType.getContactCommunicationInformation().add(buildHL7XTNType(xtn));
        }

        hl7CTDType.setPreferredMethodOfContact(buildHL7CEType(contactData.getPreferredMethodOfContact()));

        for (Pln pln : contactData.getContactIdentifiers()) {
            hl7CTDType.getContactIdentifiers().add(buildHL7PLNType(pln));
        }

        return hl7CTDType;
    }

    private HL7PLNType buildHL7PLNType(Pln pln) {
        HL7PLNType hl7PLNType = new HL7PLNType();

        hl7PLNType.setTypeOfIDNumber(pln.getIdNumber());
        hl7PLNType.setTypeOfIDNumber(pln.getTypeOfIdNumber());
        hl7PLNType.setStateOtherQualifyingInformation(pln.getStateOtherQualifyingInformation());
        hl7PLNType.setExpirationDate(buildHL7TSType(pln.getExpirationDate()));

        return hl7PLNType;
    }

    private HL7SIType buildHL7SIType(String s) {
        HL7SIType hl7SIType = new HL7SIType();

        hl7SIType.setHL7SequenceID(s);

        return hl7SIType;
    }

    private HL7SPSType buildHL7SPSType(Sps sps) {
        HL7SPSType hl7SPSType = new HL7SPSType();

        hl7SPSType.setHL7SpecimenSourceNameOrCode(buildHL7CWEType(sps.getSpecimenSourceNameOrCode()));
        hl7SPSType.setHL7Additives(buildHL7CWEType(sps.getAdditives()));

        if( StringUtils.isNotEmpty(sps.getSpecimenCollectionMethod())) {
            hl7SPSType.setHL7SpecimenCollectionMethod(buildHL7TXType(sps.getSpecimenCollectionMethod()));
        }

        hl7SPSType.setHL7BodySite(buildHL7CWEType(sps.getBodySite()));
        hl7SPSType.setHL7SiteModifier(buildHL7CWEType(sps.getSiteModifier()));
        hl7SPSType.setHL7CollectionMethodModifierCode(buildHL7CWEType(sps.getCollectionMethodModifierCode()));
        hl7SPSType.setHL7SpecimenRole(buildHL7CWEType(sps.getSpecimenRole()));

        return hl7SPSType;
    }

    private HL7MOCType buildHL7MOCType(Moc moc) {
        HL7MOCType hl7MOCType = new HL7MOCType();

        hl7MOCType.setHL7MonetaryAmount(buildHL7MOType(moc.getMonetaryAmount()));
        hl7MOCType.setHL7ChargeCode(buildHL7CEType(moc.getChargeCode()));

        return hl7MOCType;
    }

    private HL7MOType buildHL7MOType(Mo mo) {
        HL7MOType hl7MOType = new HL7MOType();

        hl7MOType.setHL7Quantity(buildHl7NMType(mo.getQuantity()));
        hl7MOType.setHL7Denomination(mo.getDenomination());

        return hl7MOType;
    }

    private HL7PRLType buildHL7PRLType(Prl prl) {
        if (isEmptyHL7PRLType(prl)) return null;

        HL7PRLType hl7PRLType = new HL7PRLType();

        hl7PRLType.setParentObservationIdentifier(buildHL7CEType(prl.getParentObservationIdentifier()));
        hl7PRLType.setParentObservationSubidentifier(prl.getParentObservationSubIdentifier());
        hl7PRLType.setParentObservationValueDescriptor(buildHL7TXType(prl.getParentObservationValueDescriptor()));

        return hl7PRLType;
    }

    private HL7NDLType buildHL7NDLType(Ndl ndl) {
        HL7NDLType hl7NDLType = new HL7NDLType();

        hl7NDLType.setHL7Name(buildHL7CNNType(ndl.getName()));
        hl7NDLType.setHL7StartDatetime(buildHL7TSType(ndl.getStartDateTime()));
        hl7NDLType.setHL7EndDatetime(buildHL7TSType(ndl.getEndDateTime()));
        hl7NDLType.setHL7PointOfCare(ndl.getPointOfCare());
        hl7NDLType.setHL7Room(ndl.getRoom());
        hl7NDLType.setHL7Bed(ndl.getBed());

        if (!isEmptyHL7HDType(ndl.getFacility())) {
            hl7NDLType.setHL7Facility(buildHL7HDType(ndl.getFacility()));
        }

        hl7NDLType.setHL7LocationStatus(ndl.getLocationStatus());
        hl7NDLType.setHL7PatientLocationType(ndl.getPatientLocationType());
        hl7NDLType.setHL7Building(ndl.getBuilding());
        hl7NDLType.setHL7Floor(ndl.getFloor());

        return hl7NDLType;
    }

    private HL7CNNType buildHL7CNNType(Cnn cnn) {
        HL7CNNType hl7CNNType = new HL7CNNType();

        hl7CNNType.setHL7IDNumber(cnn.getIdNumber());
        hl7CNNType.setHL7FamilyName(cnn.getFamilyName());
        hl7CNNType.setHL7GivenName(cnn.getGivenName());
        hl7CNNType.setHL7SecondAndFurtherGivenNamesOrInitialsThereof(cnn.getSecondAndFurtherGivenNameOrInitial());
        hl7CNNType.setHL7Suffix(cnn.getSuffix());
        hl7CNNType.setHL7Prefix(cnn.getPrefix());
        hl7CNNType.setHL7Degree(cnn.getDegree());
        hl7CNNType.setHL7SourceTable(cnn.getSourceTable());
        hl7CNNType.setHL7AssigningAuthorityNamespaceID(cnn.getAssignAuthorityNamespaceId());
        hl7CNNType.setAssigningAuthorityUniversalID(cnn.getAssignAuthorityUniversalId());
        hl7CNNType.setAssigningAuthorityUniversalIDType(cnn.getAssignAuthorityUniversalIdType());

        return hl7CNNType;
    }

    private HL7OBRType buildHL7OBRType(ObservationRequest or) {
        HL7OBRType hl7OBRType = new HL7OBRType();

        hl7OBRType.setSetIDOBR(buildHL7SIType(or.getSetIdObr()));

        if (!isEmptyHL7EIType(or.getPlacerOrderNumber())) {
            hl7OBRType.setPlacerOrderNumber(buildHL7EIType(or.getPlacerOrderNumber()));
        }

        if (!isEmptyHL7EIType(or.getFillerOrderNumber())) {
            hl7OBRType.setFillerOrderNumber(buildHL7EIType(or.getFillerOrderNumber()));
        }

        hl7OBRType.setUniversalServiceIdentifier(buildHL7CWEType(or.getUniversalServiceIdentifier()));
        hl7OBRType.setPriorityOBR(or.getPriorityObr());
        hl7OBRType.setRequestedDateTime(buildHL7TSType(or.getRequestedDateTime()));
        hl7OBRType.setObservationDateTime(buildHL7TSType(or.getObservationDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));
        hl7OBRType.setObservationEndDateTime(buildHL7TSType(or.getObservationEndDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));

        if (!isEmptyHL7CQType(or.getCollectionVolume())) {
            hl7OBRType.setCollectionVolume(buildHL7CQType(or.getCollectionVolume()));
        }

        for (Xcn xcn : or.getCollectorIdentifier()) {
            hl7OBRType.getCollectorIdentifier().add(buildHL7XCNType(xcn));
        }

        hl7OBRType.setSpecimenActionCode(or.getSpecimenActionCode());

        if (!isEmptyHL7CEType(or.getDangerCode())) {
            hl7OBRType.setDangerCode(buildHL7CWEType(or.getDangerCode()));
        }

        hl7OBRType.setRelevantClinicalInformation(or.getRelevantClinicalInformation());
        hl7OBRType.setSpecimenReceivedDateTime(buildHL7TSType(or.getSpecimenReceivedDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));

        if (!isEmptyHL7SPSType(or.getSpecimenSource())) {
            hl7OBRType.setSpecimenSource(buildHL7SPSType(or.getSpecimenSource()));
        }

        for (Xcn xcn : or.getOrderingProvider()) {
            hl7OBRType.getOrderingProvider().add(buildHL7XCNType(xcn));
        }

        for (Xtn xtn : or.getOrderCallbackPhoneNumber()) {
            hl7OBRType.getOrderCallbackPhoneNumber().add(buildHL7XTNType(xtn));
        }

        hl7OBRType.setPlacerField1(or.getPlacerField1());
        hl7OBRType.setPlacerField2(or.getPlacerField2());
        hl7OBRType.setFillerField1(or.getFillerField1());
        hl7OBRType.setFillerField2(or.getFillerField2());
        hl7OBRType.setResultsRptStatusChngDateTime(buildHL7TSType(or.getResultRptStatusChngDateTime()));

        if (!isEmptyHL7MOCType(or.getChargeToPractice())) {
            hl7OBRType.setChargeToPractice(buildHL7MOCType(or.getChargeToPractice()));
        }

        hl7OBRType.setDiagnosticServSectID(or.getDiagnosticServSectId());
        hl7OBRType.setResultStatus(or.getResultStatus());

        HL7PRLType hl7PRLType = buildHL7PRLType(or.getParentResult());
        if (null != hl7PRLType) {
            hl7OBRType.setParentResult(hl7PRLType);
        }

        for (Tq tq : or.getQuantityTiming()) {
            hl7OBRType.setQuantityTiming(buildHL7TQType(tq));
        }

        for (Xcn xcn : or.getResultCopiesTo()) {
            hl7OBRType.getResultCopiesTo().add(buildHL7XCNType(xcn));
        }

        HL7EIPType hl7EIPType = buildHL7EIPType(or.getParent());
        if (null != hl7EIPType) {
            hl7OBRType.setParent(hl7EIPType);
        }

        hl7OBRType.setTransportationMode(or.getTransportationMode());

        for (Ce ce : or.getReasonForStudy()) {
            hl7OBRType.getReasonforStudy().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7NDLType(or.getPrincipalResultInterpreter())) {
            hl7OBRType.setPrincipalResultInterpreter(buildHL7NDLType(or.getPrincipalResultInterpreter()));
        }

        for (Ndl ndl : or.getAssistantResultInterpreter()) {
            hl7OBRType.getAssistantResultInterpreter().add(buildHL7NDLType(ndl));
        }

        for (Ndl ndl : or.getTechnician()) {
            hl7OBRType.getTechnician().add(buildHL7NDLType(ndl));
        }

        for (Ndl ndl : or.getTranscriptionist()) {
            hl7OBRType.getTranscriptionist().add(buildHL7NDLType(ndl));
        }

        hl7OBRType.setScheduledDateTime(buildHL7TSType(or.getScheduledDateTime()));
        hl7OBRType.setNumberofSampleContainers(buildHl7NMType(or.getNumberOfSampleContainers()));

        for (Ce ce : or.getTransportLogisticsOfCollectedSample()) {
            hl7OBRType.getTransportLogisticsofCollectedSample().add(buildHL7CWEType(ce));
        }

        for (Ce ce : or.getCollectorComment()) {
            hl7OBRType.getCollectorsComment().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7CEType(or.getTransportArrangementResponsibility())) {
            hl7OBRType.setTransportArrangementResponsibility(buildHL7CWEType(or.getTransportArrangementResponsibility()));
        }

        hl7OBRType.setTransportArranged(or.getTransportArranged());
        hl7OBRType.setEscortRequired(or.getEscortRequired());

        for (Ce ce : or.getPlannedPatientTransportComment()) {
            hl7OBRType.getPlannedPatientTransportComment().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7CEType(or.getProcedureCode())) {
            hl7OBRType.setProcedureCode(buildHL7CWEType(or.getProcedureCode()));
        }

        for (Ce ce : or.getProcedureCodeModifier()) {
            hl7OBRType.getProcedureCodeModifier().add(buildHL7CWEType(ce));
        }

        for (Ce ce : or.getPlacerSupplementalServiceInformation()) {
            hl7OBRType.getPlacerSupplementalServiceInformation().add(buildHL7CWEType(ce));
        }

        for (Ce ce : or.getFillerSupplementalServiceInformation()) {
            hl7OBRType.getFillerSupplementalServiceInformation().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7CWEType(or.getMedicallyNecessaryDuplicateProcedureReason())) {
            hl7OBRType.setMedicallyNecessaryDuplicateProcedureReason(buildHL7CWEType(or.getMedicallyNecessaryDuplicateProcedureReason()));
        }

        hl7OBRType.setResultHandling(or.getResultHandling());

        if (!isEmptyHL7CWEType(or.getParentUniversalServiceIdentifier())) {
            hl7OBRType.setParentUniversalServiceIdentifier(buildHL7CWEType(or.getParentUniversalServiceIdentifier()));
        }

        return hl7OBRType;
    }

    private HL7TQ2Type buildHL7TQ2Type(TimingQuantityRelationship tqr) {
        HL7TQ2Type hl7TQ2Type = new HL7TQ2Type();

        hl7TQ2Type.setSetIDTQ2(buildHL7SIType(tqr.getSetIdTq2()));
        hl7TQ2Type.getSequenceResultsFlag().add(tqr.getSequenceResultFlag());

        for (Ei ei : tqr.getRelatedPlacerNumber()) {
            hl7TQ2Type.getRelatedPlacerNumber().add(buildHL7EIType(ei));
        }

        for (Ei ei : tqr.getRelatedFillerNumber()) {
            hl7TQ2Type.getRelatedFillerNumber().add(buildHL7EIType(ei));
        }

        for (Ei ei : tqr.getRelatedPlacerGroupNumber()) {
            hl7TQ2Type.getRelatedPlacerGroupNumber().add(buildHL7EIType(ei));
        }

        hl7TQ2Type.getSequenceConditionCode().add(tqr.getSequenceConditionCode());
        hl7TQ2Type.getCyclicEntryExitIndicator().add(tqr.getCyclicEntryExitIndicator());
        hl7TQ2Type.getSequenceConditionTimeInterval().add(buildHL7CQType(tqr.getSequenceConditionTimeInterval()));
        hl7TQ2Type.getCyclicGroupMaximumNumberofRepeats().add(buildHl7NMType(tqr.getCyclicGroupMaximumNumberOfRepeats()));
        hl7TQ2Type.getSpecialServiceRequestRelationship().add(tqr.getSpecialServiceRequestRelationship());

        return hl7TQ2Type;
    }

    private HL7XCNType buildHL7XCNType(Xcn xcn) {
        HL7XCNType hl7XCNType = new HL7XCNType();

        hl7XCNType.setHL7IDNumber(xcn.getIdNumber());
        hl7XCNType.setHL7FamilyName(buildHl7FNType(xcn.getFamilyName()));
        hl7XCNType.setHL7GivenName(xcn.getGivenName());
        hl7XCNType.setHL7SecondAndFurtherGivenNamesOrInitialsThereof(xcn.getSecondAndFurtherGivenNameOrInitial());
        hl7XCNType.setHL7Suffix(xcn.getSuffix());
        hl7XCNType.setHL7Prefix(xcn.getPrefix());
        hl7XCNType.setHL7Degree(xcn.getDegree());
        hl7XCNType.setHL7SourceTable(xcn.getSourceTable());

        if (!isEmptyHL7HDType(xcn.getAssignAuthority())) {
            hl7XCNType.setHL7AssigningAuthority(buildHL7HDType(xcn.getAssignAuthority()));
        }

        hl7XCNType.setHL7NameTypeCode(xcn.getNameTypeCode());
        hl7XCNType.setHL7IdentifierCheckDigit(xcn.getIdentifierCheckDigit());
        hl7XCNType.setHL7CheckDigitScheme(xcn.getCheckDigitScheme());
        hl7XCNType.setHL7IdentifierTypeCode(xcn.getIdentifierTypeCode());

        if (!isEmptyHL7HDType(xcn.getAssignFacility())) {
            hl7XCNType.setHL7AssigningFacility(buildHL7HDType(xcn.getAssignFacility()));
        }

        hl7XCNType.setHL7NameRepresentationCode(xcn.getNameRepresentationCode());

        if (!isEmptyHL7CEType(xcn.getNameContext())) {
            hl7XCNType.setHL7NameContext(buildHL7CEType(xcn.getNameContext()));
        }

        if (!isEmptyHL7DRType(xcn.getNameValidityRange())) {
            hl7XCNType.setHL7NameValidityRange(buildHL7DRType(xcn.getNameValidityRange()));
        }

        hl7XCNType.setHL7NameAssemblyOrder(xcn.getNameAssemblyOrder());
        hl7XCNType.setHL7EffectiveDate(buildHL7TSType(xcn.getEffectiveDate()));
        hl7XCNType.setHL7ExpirationDate(buildHL7TSType(xcn.getExpirationDate()));
        //hl7XCNType.setHL7ProfessionalSuffix(xcn.getPrefix());

        if (!isEmptyHL7CWEType(xcn.getAssignJurisdiction())) {
            hl7XCNType.setHL7AssigningJurisdiction(buildHL7CWEType(xcn.getAssignJurisdiction()));
        }

        if (!isEmptyHL7CWEType(xcn.getAssignAgencyDept())) {
            hl7XCNType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(xcn.getAssignAgencyDept()));
        }

        return hl7XCNType;
    }

    private HL7TQ1Type buildHL7TQ1Type(TimingQuantity tq) {
        HL7TQ1Type hl7TQ1Type = new HL7TQ1Type();

        hl7TQ1Type.setSetIDTQ1(buildHL7SIType(buildEmptyValue(tq.getSetIdTq1())));
        hl7TQ1Type.setQuantity(buildHL7CQType(tq.getQuantity()));

        for (Rpt rpt : tq.getRepeatPattern()) {
            hl7TQ1Type.getRepeatPattern().add(buildHL7RPTType(rpt));
        }

        for (String s : tq.getExplicitTime()) {
            hl7TQ1Type.getExplicitTime().add(buildHL7TMType(s));
        }

        for (Cq cq : tq.getRelativeTimeAndUnits()) {
            hl7TQ1Type.getRelativeTimeAndUnits().add(buildHL7CQType(cq));
        }

        hl7TQ1Type.setServiceDuration(buildHL7CQType(tq.getServiceDuration()));
        hl7TQ1Type.setStartdatetime(buildHL7TSType(tq.getStartDateTime()));
        hl7TQ1Type.setEnddatetime(buildHL7TSType(tq.getEndDateTime()));

        for (Cwe cwe : tq.getPriority()) {
            hl7TQ1Type.getPriority().add(buildHL7CWEType(cwe));
        }

        hl7TQ1Type.setConditiontext(buildHL7TXType(tq.getConditionText()));
        hl7TQ1Type.setTextinstruction(buildHL7TXType(tq.getTextInstruction()));
        hl7TQ1Type.setConjunction(tq.getConjunction());
        hl7TQ1Type.setOccurrenceDuration(buildHL7CQType(tq.getOccurrenceDuration()));
        hl7TQ1Type.setTotalOccurrences(buildHl7NMType(tq.getTotalOccurrences()));

        return hl7TQ1Type;
    }

    private HL7RPTType buildHL7RPTType(Rpt rpt) {
        HL7RPTType hl7RPTType = new HL7RPTType();

        hl7RPTType.setRepeatPatternCode(buildHL7CWEType(rpt.getRepeatPatternCode()));
        hl7RPTType.setCalendarAlignment(rpt.getCalendarAlignment());
        hl7RPTType.setPhaseRangeBeginValue(buildHl7NMType(rpt.getPhaseRangeBeginValue()));
        hl7RPTType.setPhaseRangeEndValue(buildHl7NMType(rpt.getPhaseRangeEndValue()));
        hl7RPTType.setPeriodQuantity(buildHl7NMType(rpt.getPeriodQuantity()));
        hl7RPTType.setPeriodUnits(rpt.getPeriodUnits());
        hl7RPTType.setInstitutionSpecifiedTime(rpt.getInstitutionSpecifiedTime());
        hl7RPTType.setEvent(rpt.getEvent());
        hl7RPTType.setEventOffsetQuantity(buildHl7NMType(rpt.getEventOffsetQuantity()));
        hl7RPTType.setEventOffsetUnits(rpt.getEventOffsetUnits());

        return hl7RPTType;
    }

    private HL7OBXType buildHL7OBXType(ObservationResult or) {
        HL7OBXType hl7OBXType = new HL7OBXType();

        hl7OBXType.setSetIDOBX(buildHL7SIType(or.getSetIdObx()));
        hl7OBXType.setValueType(or.getValueType());
        hl7OBXType.setObservationIdentifier(buildHL7CWEType(or.getObservationIdentifier()));
        hl7OBXType.setObservationSubID(or.getObservationSubId());

        for (String s : or.getObservationValue()) {
            if (OBX_VALUE_TYPE_SN.equals(hl7OBXType.getValueType())) {
                String refinedValue = s.replaceAll(CARET_SEPARATOR, "");
                hl7OBXType.getObservationValue().add(refinedValue);
            } else {
                hl7OBXType.getObservationValue().add(s);
            }
        }

        if (!isEmptyHL7CEType(or.getUnits())) {
            hl7OBXType.setUnits(buildHL7CEType(or.getUnits()));
        }

        hl7OBXType.setReferencesRange(or.getReferencesRange());

        /*
        for(String s : or.getAbnormalFlag()) {
            hl7OBXType.getAbnormalFlags().add(buildHL7CWEType(s));
        }
        */

        hl7OBXType.getProbability().add(buildHl7NMType(or.getProbability()));

        for (String s : or.getNatureOfAbnormalTest()) {
            hl7OBXType.getNatureOfAbnormalTest().add(s);
        }

        hl7OBXType.setObservationResultStatus(buildEmptyValue(or.getObservationResultStatus()));
        hl7OBXType.setEffectiveDateOfReferenceRangeValues(buildHL7TSType(or.getEffectiveDateOfReferenceRange()));
        hl7OBXType.setUserDefinedAccessChecks(or.getUserDefinedAccessChecks());
        hl7OBXType.setDateTimeOftheObservation(buildHL7TSType(or.getDateTimeOfTheObservation()));

        if (!isEmptyHL7CEType(or.getProducerId())) {
            hl7OBXType.setProducersReference(buildHL7CWEType(or.getProducerId()));
        }

        for (Xcn xcn : or.getResponsibleObserver()) {
            hl7OBXType.getResponsibleObserver().add(buildHL7XCNType(xcn));
        }

        for (Ce ce : or.getObservationMethod()) {
            hl7OBXType.getObservationMethod().add(buildHL7CEType(ce));
        }

        for (Ei ei : or.getEquipmentInstanceIdentifier()) {
            hl7OBXType.getEquipmentInstanceIdentifier().add(buildHL7EIType(ei));
        }

        hl7OBXType.setDateTimeOftheAnalysis(buildHL7TSType(or.getDateTimeOfTheAnalysis(), TS_FMT_DATE_ONLY));

        //hl7OBXType.setReservedforHarmonizationWithV261(or.getReservedForHarmonizationWithV261());
        //hl7OBXType.setReservedForHarmonizationwithV262(or.getReservedForHarmonizationWithV262());
        //hl7OBXType.setReservedForHarmonizationWithV263(or.getReservedForHarmonizationWithV263());

        if (!isEmptyHL7XONType(or.getPerformingOrganizationName())) {
            hl7OBXType.setPerformingOrganizationName(buildHL7XONType(or.getPerformingOrganizationName()));
        }

        if (!isEmptyHL7XADType(or.getPerformingOrganizationAddress())) {
            hl7OBXType.setPerformingOrganizationAddress(buildHL7XADType(or.getPerformingOrganizationAddress()));
        }

        if (!isEmptyHL7XCNType(or.getPerformingOrganizationMedicalDirector())) {
            hl7OBXType.setPerformingOrganizationMedicalDirector(buildHL7XCNType(or.getPerformingOrganizationMedicalDirector()));
        }

        return hl7OBXType;
    }

    private HL7SPMType buildHL7SPMType(gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen s) {
        HL7SPMType hl7SPMType = new HL7SPMType();

        hl7SPMType.setSetIDSPM(buildHL7SIType(s.getSetIdSpm()));
        hl7SPMType.setSpecimenID(buildHL7EIPType(s.getSpecimenId()));

        for (Eip eip : s.getSpecimenParentId()) {
            hl7SPMType.getSpecimenParentIDs().add(buildHL7EIPType(eip));
        }

        hl7SPMType.setSpecimenType(buildHL7CWEType(s.getSpecimenType()));

        for (Cwe cwe : s.getSpecimenTypeModifier()) {
            hl7SPMType.getSpecimenTypeModifier().add(buildHL7CWEType(cwe));
        }

        for (Cwe cwe : s.getSpecimenAdditives()) {
            hl7SPMType.getSpecimenAdditives().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenCollectionMethod())) {
            hl7SPMType.setSpecimenCollectionMethod(buildHL7CWEType(s.getSpecimenCollectionMethod()));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenSourceSite())) {
            hl7SPMType.setSpecimenSourceSite(buildHL7CWEType(s.getSpecimenSourceSite()));
        }

        for (Cwe cwe : s.getSpecimenSourceSiteModifier()) {
            hl7SPMType.getSpecimenSourceSiteModifier().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenCollectionSite())) {
            hl7SPMType.setSpecimenCollectionSite(buildHL7CWEType(s.getSpecimenCollectionSite()));
        }

        for (Cwe cwe : s.getSpecimenRole()) {
            hl7SPMType.getSpecimenRole().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7CQType(s.getSpecimenCollectionAmount())) {
            hl7SPMType.setSpecimenCollectionAmount(buildHL7CQType(s.getSpecimenCollectionAmount()));
        }

        hl7SPMType.setGroupedSpecimenCount(buildHl7NMType(s.getGroupedSpecimenCount()));

        for (String str : s.getSpecimenDescription()) {
            hl7SPMType.getSpecimenDescription().add(str);
        }

        for (Cwe cwe : s.getSpecimenHandlingCode()) {
            hl7SPMType.getSpecimenHandlingCode().add(buildHL7CWEType(cwe));
        }

        for (Cwe cwe : s.getSpecimenRiskCode()) {
            hl7SPMType.getSpecimenRiskCode().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7DRType(s.getSpecimenCollectionDateTime())) {
            hl7SPMType.setSpecimenCollectionDateTime(buildHL7DRType(s.getSpecimenCollectionDateTime()));
        }

        hl7SPMType.setSpecimenReceivedDateTime(buildHL7TSType(s.getSpecimenReceivedDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));
        hl7SPMType.setSpecimenExpirationDateTime(buildHL7TSType(s.getSpecimenExpirationDateTime()));
        hl7SPMType.setSpecimenAvailability(s.getSpecimenAvailability());

        for (Cwe cwe : s.getSpecimenRejectReason()) {
            hl7SPMType.getSpecimenRejectReason().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenQuality())) {
            hl7SPMType.setSpecimenQuality(buildHL7CWEType(s.getSpecimenQuality()));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenAppropriateness())) {
            hl7SPMType.setSpecimenAppropriateness(buildHL7CWEType(s.getSpecimenAppropriateness()));
        }

        for (Cwe cwe : s.getSpecimenCondition()) {
            hl7SPMType.getSpecimenCondition().add(buildHL7CWEType(cwe));
        }

        if (!isEmptyHL7CQType(s.getSpecimenCurrentQuantity())) {
            hl7SPMType.setSpecimenCurrentQuantity(buildHL7CQType(s.getSpecimenCurrentQuantity()));
        }

        hl7SPMType.setNumberOfSpecimenContainers(buildHl7NMType(s.getNumberOfSpecimenContainers()));

        if (!isEmptyHL7CWEType(s.getContainerType())) {
            hl7SPMType.setContainerType(buildHL7CWEType(s.getContainerType()));
        }

        if (!isEmptyHL7CWEType(s.getContainerCondition())) {
            hl7SPMType.setContainerCondition(buildHL7CWEType(s.getContainerCondition()));
        }

        if (!isEmptyHL7CWEType(s.getSpecimenChildRole())) {
            hl7SPMType.setSpecimenChildRole(buildHL7CWEType(s.getSpecimenChildRole()));
        }

        return hl7SPMType;
    }

    private HL7CPType buildHL7CPType(Cp cp) {
        HL7CPType hl7CPType = new HL7CPType();

        hl7CPType.setHL7Price(buildHL7MOType(cp.getPrice()));
        hl7CPType.setHL7PriceType(cp.getPriceType());
        hl7CPType.setHL7FromValue(buildHl7NMType(cp.getFromValue()));
        hl7CPType.setHL7ToValue(buildHl7NMType(cp.getToValue()));
        hl7CPType.setHL7RangeUnits(buildHL7CEType(cp.getRangeUnits()));
        hl7CPType.setHL7RangeType(cp.getRangeType());

        return hl7CPType;
    }

    private HL7FT1Type buildHL7FT1Type(FinancialTransaction ft) {
        HL7FT1Type hl7FT1Type = new HL7FT1Type();

        hl7FT1Type.setSetIDFT1(buildHL7SIType(ft.getSetIdFT1()));
        hl7FT1Type.setTransactionID(ft.getTransactionId());
        hl7FT1Type.setTransactionBatchID(ft.getTransactionBatchId());
        hl7FT1Type.setTransactionDate(buildHL7DRType(ft.getTransactionDate()));
        hl7FT1Type.setTransactionPostingDate(buildHL7TSType(ft.getTransactionPostingDate()));
        hl7FT1Type.setTransactionType(buildEmptyValue(ft.getTransactionType()));
        hl7FT1Type.setTransactionCode(buildHL7CEType(ft.getTransactionCode()));
        hl7FT1Type.setTransactionDescription(ft.getTransactionDescription());
        hl7FT1Type.setTransactionDescriptionAlt(ft.getTransactionDescriptionAlter());
        hl7FT1Type.setTransactionQuantity(buildHl7NMType(ft.getTransactionQuantity()));
        hl7FT1Type.setTransactionAmountExtended(buildHL7CPType(ft.getTransactionAmountExt()));
        hl7FT1Type.setTransactionAmountUnit(buildHL7CPType(ft.getTransactionAmountUnit()));
        hl7FT1Type.setDepartmentCode(buildHL7CEType(ft.getDepartmentCode()));
        hl7FT1Type.setInsurancePlanID(buildHL7CEType(ft.getInsurancePlanId()));
        hl7FT1Type.setInsuranceAmount(buildHL7CPType(ft.getInsuranceAmount()));
        hl7FT1Type.setAssignedPatientLocation(buildHL7PLType(ft.getAssignedPatientLocation()));
        hl7FT1Type.setFeeSchedule(ft.getFeeSchedule());
        hl7FT1Type.setPatientType(ft.getPatientType());

        for (Ce ce : ft.getDiagnosisCode()) {
            hl7FT1Type.getDiagnosisCodeFT1().add(buildHL7CEType(ce));
        }

        for (Xcn xcn : ft.getPerformedByCode()) {
            hl7FT1Type.getPerformedByCode().add(buildHL7XCNType(xcn));
        }

        for (Xcn xcn : ft.getOrderedByCode()) {
            hl7FT1Type.getOrderedByCode().add(buildHL7XCNType(xcn));
        }

        if (!isEmptyHL7CEType(ft.getProcedureCode())) {
            hl7FT1Type.setProcedureCode(buildHL7CEType(ft.getProcedureCode()));
        }

        for (Ce ce : ft.getProcedureCodeModifier()) {
            hl7FT1Type.getProcedureCodeModifier().add(buildHL7CEType(ce));
        }

        hl7FT1Type.setAdvancedBeneficiaryNoticeCode(buildHL7CEType(ft.getAdvancedBeneficiaryNoticeCode()));

        if (!isEmptyHL7CWEType(ft.getMedicallyNecessaryDuplicateProcedureReason())) {
            hl7FT1Type.setMedicallyNecessaryDuplicateProcedureReason(buildHL7CWEType(ft.getMedicallyNecessaryDuplicateProcedureReason()));
        }

        hl7FT1Type.setNDCCode(buildHL7CNEType(ft.getNdcCode()));
        hl7FT1Type.setPaymentReferenceID(buildHL7CXType(ft.getPaymentReferenceId()));

        for (String s : ft.getTransactionReferenceKey()) {
            hl7FT1Type.getTransactionReferenceKey().add(buildHL7SIType(s));
        }

        return hl7FT1Type;
    }

    private HL7PIV2Type buildHL7PIV2Type(PatientVisitAdditional pva) {
        HL7PIV2Type hl7PIV2Type = new HL7PIV2Type();

        hl7PIV2Type.setPriorPendingLocation(buildHL7PLType(pva.getPriorPendingLocation()));
        hl7PIV2Type.setAccommodationCode(buildHL7CWEType(pva.getAccommodationCode()));
        hl7PIV2Type.setAdmitReason(buildHL7CWEType(pva.getAdmitReason()));
        hl7PIV2Type.setTransferReason(buildHL7CWEType(pva.getTransferReason()));

        for (String s : pva.getPatientValuables()) {
            hl7PIV2Type.getPatientValuables().add(s);
        }

        hl7PIV2Type.setPatientValuablesLocation(pva.getPatientValuablesLocation());

        for (String s : pva.getVisitUserCode()) {
            hl7PIV2Type.getVisitUserCode().add(s);
        }

        hl7PIV2Type.setExpectedAdmitDateTime(buildHL7TSType(pva.getExpectedAdmitDateTime()));
        hl7PIV2Type.setExpectedDischargeDateTime(buildHL7TSType(pva.getExpectedDischargeDateTime()));
        hl7PIV2Type.setEstimatedLengthOfInpatientStay(buildHl7NMType(pva.getEstimateLengthOfInpatientDay()));
        hl7PIV2Type.setActualLengthOfInpatientStay(buildHl7NMType(pva.getActualLengthOfInpatientDay()));
        hl7PIV2Type.getVisitDescription().add(pva.getVisitDescription());

        for (Xcn xcn : pva.getReferralSourceCode()) {
            hl7PIV2Type.getReferralSourceCode().add(buildHL7XCNType(xcn));
        }

        hl7PIV2Type.setPreviousServiceDate(buildHL7DTType(pva.getPreviousServiceDate()));
        hl7PIV2Type.setEmploymentIllnessRelatedIndicator(pva.getEmploymentIllnessRelatedIndicator());
        hl7PIV2Type.setPurgeStatusCode(pva.getPurgeStatusCode());
        hl7PIV2Type.setPurgeStatusDate(buildHL7DTType(pva.getPurgeStatusDate()));
        hl7PIV2Type.setSpecialProgramCode(pva.getSpecialProgramCode());
        hl7PIV2Type.setRetentionIndicator(pva.getRetentionIndicator());
        hl7PIV2Type.setExpectedNumberOfInsurancePlans(buildHl7NMType(pva.getExpectedNumberOfInsurancePlans()));
        hl7PIV2Type.setVisitPriorityCode(pva.getVisitPublicityCode());
        hl7PIV2Type.getVisitProtectionIndicator().add(pva.getVisitProtectionIndicator());

        for (Xon xon : pva.getClinicOrganizationName()) {
            hl7PIV2Type.setClinicOrganizationName(buildHL7XONType(xon));
        }

        hl7PIV2Type.setPatientStatusCode(pva.getPatientStatusCode());
        hl7PIV2Type.setVisitPriorityCode(pva.getVisitPriorityCode());
        hl7PIV2Type.setPreviousTreatmentDate(buildHL7DTType(pva.getPreviousServiceDate()));
        hl7PIV2Type.setExpectedDischargeDisposition(pva.getExpectedDischargeDisposition());
        hl7PIV2Type.setSignatureOnFileDate(buildHL7DTType(pva.getSignatureOnFileDate()));
        hl7PIV2Type.setFirstSimilarIllnessDate(buildHL7DTType(pva.getFirstSimilarIllnessDate()));
        hl7PIV2Type.setPatientStatusCode(pva.getPatientStatusCode());
        hl7PIV2Type.setRecurringServiceCode(pva.getRecurringServiceCode());
        hl7PIV2Type.setBillingMediaCode(pva.getBillingMediaCode());
        hl7PIV2Type.setExpectedSurgeryDateAndTime(buildHL7TSType(pva.getExpectedSurgeryDateTime()));
        hl7PIV2Type.setMilitaryPartnershipCode(pva.getMilitaryPartnershipCode());
        hl7PIV2Type.setMilitaryNonAvailabilityCode(pva.getMilitaryNonAvailCode());
        hl7PIV2Type.setNewbornBabyIndicator(pva.getNewbornBabyIndicator());
        hl7PIV2Type.setBabyDetainedIndicator(pva.getBabyDetainedIndicator());
        hl7PIV2Type.getModeOfArrivalCode().add(buildHL7CWEType(pva.getModeOfArrivalCode()));

        for (Ce ce : pva.getRecreationalDrugUseCode()) {
            hl7PIV2Type.setRecreationalDrugUseCode(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setAdmissionLevelOfCareCode(buildHL7CWEType(pva.getAdmissionLevelOfCareCode()));

        for (Ce ce : pva.getPrecautionCode()) {
            hl7PIV2Type.setPrecautionCode(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setPatientConditionCode(buildHL7CWEType(pva.getPatientConditionCode()));
        hl7PIV2Type.setLivingWillCode(pva.getLivingWillCode());
        hl7PIV2Type.setOrganDonorCode(pva.getOrganDonorCode());

        for (Ce ce : pva.getAdvanceDirectiveCode()) {
            hl7PIV2Type.getAdvanceDirectiveCode().add(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setPatientStatusEffectiveDate(buildHL7DTType(pva.getPatientStatusEffectiveDate()));
        hl7PIV2Type.setExpectedLOAReturnDateTime(buildHL7TSType(pva.getExpectedLoaReturnDateTime()));
        hl7PIV2Type.setExpectedPreadmissionTestingDateTime(buildHL7TSType(pva.getExpectedPreAdmissionTestingDateTime()));

        for (String s : pva.getNotifyClergyCode()) {
            hl7PIV2Type.getNotifyClergyCode().add(s);
        }

        return hl7PIV2Type;
    }


    private HL7TMType buildHL7TMType(String s) {
        HL7TMType hl7TMType = new HL7TMType();

        return hl7TMType;
    }

    private HL7PIV1Type buildHL7PIV1Type(PatientVisit pv) {
        HL7PIV1Type hl7PIV1Type = new HL7PIV1Type();

        hl7PIV1Type.setSetIDPV1(buildHL7SIType(pv.getSetIdPv1()));
        hl7PIV1Type.setPatientClass(buildEmptyValue(pv.getPatientClass()));
        hl7PIV1Type.setAssignedPatientLocation(buildHL7PLType(pv.getAssignPatientLocation()));
        hl7PIV1Type.setAdmissionType(pv.getAdmissionType());

        if ((null == pv.getPreadmitNumber().getIdNumber())) {
            pv.getPreadmitNumber().setIdNumber(EMPTY_STRING);
        }
        hl7PIV1Type.setPreadmitNumber(buildHL7CXType(pv.getPreadmitNumber()));

        hl7PIV1Type.setPriorPatientLocation(buildHL7PLType(pv.getPriorPatientLocation()));

        for (Xcn xcn : pv.getAttendingDoctor()) {
            hl7PIV1Type.getAttendingDoctor().add(buildHL7XCNType(xcn));
        }

        for (Xcn xcn : pv.getReferringDoctor()) {
            hl7PIV1Type.getReferringDoctor().add(buildHL7XCNType(xcn));
        }

        for (Xcn xcn : pv.getConsultingDoctor()) {
            hl7PIV1Type.setConsultingDoctor(buildHL7XCNType(xcn));
        }

        hl7PIV1Type.setHospitalService(pv.getHospitalService());
        hl7PIV1Type.setTemporaryLocation(buildHL7PLType(pv.getTemporaryLocation()));
        hl7PIV1Type.setPreadmitTestIndicator(pv.getPreadmitTestIndicator());
        hl7PIV1Type.setReAdmissionIndicator(pv.getReAdmissionIndicator());
        hl7PIV1Type.setAdmissionType(pv.getAdmitSource());

        for (String s : pv.getAmbulatoryStatus()) {
            hl7PIV1Type.getAmbulatoryStatus().add(s);
        }

        hl7PIV1Type.setVisitIndicator(pv.getVisitIndicator());

        for (Xcn xcn : pv.getAdmittingDoctor()) {
            hl7PIV1Type.getAttendingDoctor().add(buildHL7XCNType(xcn));
        }

        hl7PIV1Type.setPatientType(pv.getPatientType());

        HL7CXType hl7CXType = buildHL7CXType(pv.getVisitNumber());
        if (hl7CXType != null) {
            hl7PIV1Type.setVisitNumber(hl7CXType);
        }

        for (Fc fc : pv.getFinancialClass()) {
            hl7PIV1Type.getFinancialClass().add(buildHL7FCType(fc));
        }

        hl7PIV1Type.setChargePriceIndicator(pv.getChargePriceIndicator());
        hl7PIV1Type.setCourtesyCode(pv.getCourtesyCode());
        hl7PIV1Type.setCreditRating(pv.getCreditRating());

        for (String s : pv.getContractRole()) {
            hl7PIV1Type.getContractCode().add(s);
        }

        for (String s : pv.getContractEffectiveDate()) {
            hl7PIV1Type.getContractEffectiveDate().add(buildHL7DTType(s));
        }

        for (String s : pv.getContractAmount()) {
            hl7PIV1Type.getContractAmount().add(buildHl7NMType(s));
        }

        for (String s : pv.getContractPeriod()) {
            hl7PIV1Type.getContractPeriod().add(buildHl7NMType(s));
        }

        hl7PIV1Type.setInterestCode(pv.getInterestCode());
        hl7PIV1Type.setTransferToBadDebtCode(pv.getTransferToBadDebtCode());
        hl7PIV1Type.setTransferToBadDebtDate(buildHL7DTType(pv.getTransferToBadDebtDate()));
        hl7PIV1Type.setBadDebtAgencyCode(pv.getBadDebtAgencyCode());
        hl7PIV1Type.setBadDebtTransferAmount(buildHl7NMType(pv.getBadDebtTransferAmount()));
        hl7PIV1Type.setBadDebtRecoveryAmount(buildHl7NMType(pv.getBadDebtTransferAmount()));
        hl7PIV1Type.setDeleteAccountIndicator(pv.getDeleteAccountIndicator());
        hl7PIV1Type.setDeleteAccountDate(buildHL7DTType(pv.getDeleteAccountDate()));
        hl7PIV1Type.setDischargeDisposition(pv.getDischargeDisposition());
        hl7PIV1Type.setDischargedToLocation(buildHL7DLDType(pv.getDischargedToLocation()));
        hl7PIV1Type.setDietType(buildHL7CWEType(pv.getDietType()));
        hl7PIV1Type.setServicingFacility(pv.getServicingFacility());
        hl7PIV1Type.setBedStatus(pv.getBedStatus());
        hl7PIV1Type.setAccountStatus(pv.getAccountStatus());
        hl7PIV1Type.setPendingLocation(buildHL7PLType(pv.getPendingLocation()));
        hl7PIV1Type.setPriorTemporaryLocation(buildHL7PLType(pv.getPriorTemporaryLocation()));
        hl7PIV1Type.setAdmitDateTime(buildHL7TSType(pv.getAdmitDateTime()));

        for (Ts ts : pv.getDischargeDateTime()) {
            hl7PIV1Type.getDischargeDateTime().add(buildHL7TSType(ts));
        }

        hl7PIV1Type.setCurrentPatientBalance(buildHl7NMType(pv.getCurrentPatientBalance()));
        hl7PIV1Type.setTotalCharges(buildHl7NMType(pv.getTotalCharge()));
        hl7PIV1Type.setTotalAdjustments(buildHl7NMType(pv.getTotalAdjustment()));
        hl7PIV1Type.setTotalPayments(buildHl7NMType(pv.getTotalPayment()));
        hl7PIV1Type.setAlternateVisitID(buildHL7CXType(pv.getAlternateVisitId()));
        hl7PIV1Type.setVisitIndicator(pv.getVisitIndicator());

        for (Xcn xcn : pv.getOtherHealthcareProvider()) {
            hl7PIV1Type.setOtherHealthcareProvider(buildHL7XCNType(xcn));
        }

        return hl7PIV1Type;
    }

    // --------------------------------------------------------------------------------------------

    /*
    protected BigInteger hours;
    protected BigInteger minutes;
    protected BigInteger seconds;
    protected BigInteger millis;
    protected String gmtOffset;
    */

    private HL7DLDType buildHL7DLDType(Dld dld) {
        HL7DLDType hl7DLDType = new HL7DLDType();

        hl7DLDType.setDischargeLocation(buildEmptyValue(dld.getDischargeLocation()));
        hl7DLDType.setEffectiveDate(buildHL7TSType(dld.getEffectiveDate()));

        return hl7DLDType;
    }

    private HL7FCType buildHL7FCType(Fc fc) {
        HL7FCType hl7FCType = new HL7FCType();

        hl7FCType.setEffectiveDate(buildHL7TSType(fc.getEffectiveDate()));
        hl7FCType.setFinancialClassCode(fc.getFinancialClassCode());

        return hl7FCType;
    }

    private HL7NK1Type buildHL7NK1Type(NextOfKin nok) {
        HL7NK1Type hl7NK1Type = new HL7NK1Type();

        hl7NK1Type.setSetIDNK1(nok.getSetIdNK1());

        for (Xpn xpn : nok.getNkName()) {
            hl7NK1Type.getName().add(buildHL7XPNType(xpn));
        }

        hl7NK1Type.setRelationship(buildHL7CWEType(nok.getRelationship()));

        for (Xad xad : nok.getAddress()) {
            hl7NK1Type.getAddress().add(buildHL7XADType(xad));
        }

        for (Xtn xtn : nok.getPhoneNumber()) {
            hl7NK1Type.getPhoneNumber().add(buildHL7XTNType(xtn));
        }

        for (Xtn xtn : nok.getBusinessPhoneNumber()) {
            hl7NK1Type.getBusinessPhoneNumber().add(buildHL7XTNType(xtn));
        }

        if (!isEmptyHL7CEType(nok.getContactRole())) {
            hl7NK1Type.setContactRole(buildHL7CWEType(nok.getContactRole()));
        }

        hl7NK1Type.setStartDate(buildHL7DTType(nok.getStartDate()));
        hl7NK1Type.setEndDate(buildHL7DTType(nok.getEndDate()));
        hl7NK1Type.getNextOfKinAssociatedPartiesJobTitle().add(nok.getNextOfKinAssociatedPartiesJobTitle());

        if (!isEmptyHL7JCCType(nok.getNextOfKinAssociatedPartiesJobCode())) {
            hl7NK1Type.setNextOfKinAssociatedPartiesJobCodeClass(buildHL7JCCType(nok.getNextOfKinAssociatedPartiesJobCode()));
        }

        HL7CXType hl7CXType = buildHL7CXType(nok.getNextOfKinAssociatedPartiesEmployee());
        if (null != hl7CXType) {
            hl7NK1Type.setNextOfKinAssociatedPartiesEmployeeNumber(hl7CXType);
        }

        for (Xon xon : nok.getOrganizationNameNk1()) {
            hl7NK1Type.getOrganizationNameNK1().add(buildHL7XONType(xon));
        }

        if (!isEmptyHL7CEType(nok.getMartialStatus())) {
            hl7NK1Type.getMaritalStatus().add(buildHL7CWEType(nok.getMartialStatus()));
        }

        hl7NK1Type.getAdministrativeSex().add(nok.getAdministrativeSex());
        hl7NK1Type.setDateTimeOfBirth(buildHL7TSType(nok.getDateTimeOfBirth()));

        for (String s : nok.getLivingDependency()) {
            hl7NK1Type.getLivingDependency().add(s);
        }

        for (String s : nok.getAmbulatoryStatus()) {
            hl7NK1Type.getAmbulatoryStatus().add(s);
        }

        for (Ce ce : nok.getCitizenship()) {
            hl7NK1Type.getCitizenship().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7CEType(nok.getPrimaryLanguage())) {
            hl7NK1Type.setPrimaryLanguage(buildHL7CWEType(nok.getPrimaryLanguage()));
        }

        hl7NK1Type.setLivingArrangement(nok.getLivingArrangement());

        if( !isEmptyHL7CEType(nok.getPublicityCode()) ) {
            hl7NK1Type.setPublicityCode(buildHL7CWEType(nok.getPublicityCode()));
        }

        hl7NK1Type.setProtectionIndicator(nok.getProtectionIndicator());

        if( StringUtils.isNotEmpty(nok.getStudentIndicator())) {
            hl7NK1Type.setStudentIndicator(buildEmptyValue(nok.getStudentIndicator()));
        }

        if (!isEmptyHL7CEType(nok.getReligion())) {
            hl7NK1Type.setReligion(buildHL7CWEType(nok.getReligion()));
        }

        for (Xpn xpn : nok.getMotherMaidenName()) {
            hl7NK1Type.getMothersMaidenName().add(buildHL7XPNType(xpn));
        }

        if (!isEmptyHL7CEType(nok.getNationality())) {
            hl7NK1Type.setNationality(buildHL7CWEType(nok.getNationality()));
        }

        for (Ce ce : nok.getEthnicGroup()) {
            hl7NK1Type.getEthnicGroup().add(buildHL7CWEType(ce));
        }

        for (Ce ce : nok.getContactReason()) {
            hl7NK1Type.getContactReason().add(buildHL7CWEType(ce));
        }

        for (Xpn xpn : nok.getContactPersonName()) {
            hl7NK1Type.getContactPersonsName().add(buildHL7XPNType(xpn));
        }

        for (Xpn xpn : nok.getContactPersonName()) {
            hl7NK1Type.getContactPersonsName().add(buildHL7XPNType(xpn));
        }

        for (Xad xad : nok.getContactPersonAddress()) {
            hl7NK1Type.getContactPersonsAddress().add(buildHL7XADType(xad));
        }

        for (Cx cx : nok.getNextOfKinAssociatedPartyIdentifier()) {
            hl7CXType = buildHL7CXType(cx);
            if (null != hl7CXType) {
                hl7NK1Type.getNextOfKinAssociatedPartysIdentifiers().add(hl7CXType);
            }
        }

        hl7NK1Type.setJobStatus(nok.getJobStatus());

        for (Ce ce : nok.getRace()) {
            hl7NK1Type.getRace().add(buildHL7CWEType(ce));
        }

        hl7NK1Type.setHandicap(nok.getHandicap());
        hl7NK1Type.setContactPersonSocialSecurityNumber(nok.getContactPersonSocialSecurityNumber());
        hl7NK1Type.setNextOfKinBirthPlace(nok.getNextOfKinBirthPlace());
        hl7NK1Type.setVIPIndicator(nok.getVipIndicator());

        return hl7NK1Type;
    }

    private HL7JCCType buildHL7JCCType(Jcc jcc) {
        HL7JCCType hl7JCCType = new HL7JCCType();

        hl7JCCType.setHL7JobCode(jcc.getJobCode());
        hl7JCCType.setHL7JobClass(jcc.getJobClass());
        hl7JCCType.setHL7JobDescriptionText(buildHL7TXType(jcc.getJobDescriptionText()));

        return hl7JCCType;
    }


    /*
    private HL7CXType buildCXType(Cx cx) {
        HL7CXType cxType = new HL7CXType();

        cxType.setHL7IDNumber(buildEmptyValue(cx.getIdNumber()));
        cxType.setHL7CheckDigit(cx.getCheckDigit());
        cxType.setHL7CheckDigitScheme(cx.getCheckDigitScheme());
        cxType.setHL7AssigningAuthority(buildHL7HDType(cx.getAssignAuthority()));
        cxType.setHL7IdentifierTypeCode(cx.getIdentifierTypeCode());
        cxType.setHL7AssigningFacility(buildHL7HDType(cx.getAssignFacility()));
        cxType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(cx.getAssignAgentOrDept()));
        cxType.setHL7EffectiveDate(buildHL7DTType(cx.getEffectiveDate()));
        cxType.setHL7ExpirationDate(buildHL7DTType(cx.getExpirationDate()));
        cxType.setHL7AssigningJurisdiction(buildHL7CWEType(cx.getAssignJurisdiction()));
        cxType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(cx.getAssignAgentOrDept()));

        return cxType;
    }
    */

    private HL7CXType buildHL7CXType(Cx cx) {
        if (null == cx.getIdNumber()) return null;

        HL7CXType hl7CXType = new HL7CXType();

        hl7CXType.setHL7IDNumber(cx.getIdNumber());
        hl7CXType.setHL7CheckDigit(cx.getCheckDigit());
        hl7CXType.setHL7CheckDigitScheme(cx.getCheckDigitScheme());

        if (!isEmptyHL7HDType(cx.getAssignAuthority())) {
            hl7CXType.setHL7AssigningAuthority(buildHL7HDType(cx.getAssignAuthority()));
        }

        hl7CXType.setHL7IdentifierTypeCode(cx.getIdentifierTypeCode());

        if (!isEmptyHL7HDType(cx.getAssignFacility())) {
            hl7CXType.setHL7AssigningFacility(buildHL7HDType(cx.getAssignFacility()));
        }

        if (null != cx.getExpirationDate()) {
            hl7CXType.setHL7EffectiveDate(buildHL7DTType(cx.getExpirationDate()));
        }

        if (null != cx.getExpirationDate()) {
            hl7CXType.setHL7ExpirationDate(buildHL7DTType(cx.getExpirationDate()));
        }

        if (!isEmptyHL7CWEType(cx.getAssignJurisdiction())) {
            hl7CXType.setHL7AssigningJurisdiction(buildHL7CWEType(cx.getAssignJurisdiction()));
        }

        if (!isEmptyHL7CWEType(cx.getAssignAgentOrDept())) {
            hl7CXType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(cx.getAssignAgentOrDept()));
        }

        return hl7CXType;
    }

    private HL7PID1Type buildHL7PID1Type(PatientAdditionalDemographic pad) {
        HL7PID1Type hl7PID1Type = new HL7PID1Type();

        for (String s : pad.getLivingDependency()) {
            hl7PID1Type.getLivingDependency().add(s);
        }

        hl7PID1Type.setLivingArrangement(pad.getLivingArrangement());

        for (Xon xon : pad.getPatientPrimaryFacility()) {
            hl7PID1Type.getPatientPrimaryFacility().add(buildHL7XONType(xon));
        }

        for (Xcn xcn : pad.getPatientPrimaryCareProviderNameAndIdNo()) {
            hl7PID1Type.setPatientPrimaryCareProviderNameAndIDNo(buildHL7XCNType(xcn));
        }

        if( StringUtils.isNotEmpty(pad.getStudentIndicator())) {
            hl7PID1Type.setStudentIndicator(buildEmptyValue(pad.getStudentIndicator()));
        }

        hl7PID1Type.setHandicap(pad.getHandiCap());
        hl7PID1Type.setLivingWillCode(pad.getLivingWillCode());
        hl7PID1Type.setOrganDonorCode(pad.getOrganDonorCode());
        hl7PID1Type.setSeparateBill(pad.getSeparateBill());

        for (Cx cx : pad.getDuplicatePatient()) {
            HL7CXType hl7CXType = buildHL7CXType(cx);
            if (null != hl7CXType) {
                hl7PID1Type.getDuplicatePatient().add(hl7CXType);
            }
        }

        if( !isEmptyHL7CEType(pad.getPublicityCode()) ) {
            hl7PID1Type.setPublicityCode(buildHL7CEType(pad.getPublicityCode()));
        }

        hl7PID1Type.setProtectionIndicator(pad.getProtectionIndicator());
        hl7PID1Type.setProtectionIndicatorEffectiveDate(buildHL7DTType(pad.getProtectionIndicatorEffectiveDate()));

        for (Xon xon : pad.getPlaceOfWorship()) {
            hl7PID1Type.getPlaceofWorship().add(buildHL7XONType(xon));
        }

        for (Ce ce : pad.getAdvanceDirectiveCode()) {
            hl7PID1Type.getAdvanceDirectiveCode().add(buildHL7CEType(ce));
        }

        hl7PID1Type.setImmunizationRegistryStatus(pad.getImmunizationRegistryStatus());
        hl7PID1Type.setImmunizationRegistryStatusEffectiveDate(buildHL7DTType(pad.getImmunizationRegistryStatusEffectiveDate()));

        if( !isEmptyHL7CEType(pad.getPublicityCode()) ) {
            hl7PID1Type.setPublicityCodeEffectiveDate(buildHL7DTType(pad.getPublicityCodeEffectiveDate()));
        }

        hl7PID1Type.setMilitaryBranch(pad.getMilitaryBranch());
        hl7PID1Type.setMilitaryRankGrade(pad.getMilitaryRank());
        hl7PID1Type.setMilitaryStatus(pad.getMilitaryStatus());

        return hl7PID1Type;
    }

    private String buildEmptyValue(String src) {
        return ((null == src) ? EMPTY_STRING : src);
    }

    private HL7PIDType buildHL7PIDType(PatientIdentification pi) {
        HL7PIDType hl7PIDType = new HL7PIDType();

        HL7SIType hl7SIType = new HL7SIType();
        hl7SIType.setHL7SequenceID(pi.getSetPid());

        hl7PIDType.getSetIDPID().add(hl7SIType);
        hl7PIDType.setPatientID(buildHL7CXType(pi.getPatientId()));

        for (Cx cx : pi.getPatientIdentifierList()) {
            hl7PIDType.getPatientIdentifierList().add(buildHL7CXType(cx));
        }

        for (Cx api : pi.getAlternativePatientId()) {
            hl7PIDType.getAlternatePatientIDPID().add(buildHL7CXType(api));
        }

        for (Xpn xpn : pi.getPatientName()) {
            hl7PIDType.getPatientName().add(buildHL7XPNType(xpn));
        }

        for (Xpn xpn : pi.getMotherMaidenName()) {
            hl7PIDType.getMothersMaidenName().add(buildHL7XPNType(xpn));
        }

        hl7PIDType.setDateTimeOfBirth(buildHL7TSType(pi.getDateTimeOfBirth(), TS_FMT_DATE_HOUR_MINUTE_ONLY));
        hl7PIDType.setAdministrativeSex(pi.getAdministrativeSex());

        for (Xpn xpn : pi.getPatientAlias()) {
            hl7PIDType.setPatientAlias(buildHL7XPNType(xpn));
        }

        for (Ce ce : pi.getRace()) {
            hl7PIDType.getRace().add(buildHL7CWEType(ce));
        }

        for (Xad xad : pi.getPatientAddress()) {
            hl7PIDType.getPatientAddress().add(buildHL7XADType(xad));
        }

        hl7PIDType.setCountyCode(pi.getCountyCode());

        for (Xtn xtn : pi.getPhoneNumberHome()) {
            hl7PIDType.getPhoneNumberHome().add(buildHL7XTNType(xtn));
        }

        for (Xtn xtn : pi.getPhoneNumberBusiness()) {
            hl7PIDType.getPhoneNumberBusiness().add(buildHL7XTNType(xtn));
        }

        if (!isEmptyHL7CEType(pi.getPrimaryLanguage())) {
            hl7PIDType.getPrimaryLanguage().add(buildHL7CEType(pi.getPrimaryLanguage()));
        }

        if (!isEmptyHL7CEType(pi.getMartialStatus())) {
            hl7PIDType.setMaritalStatus(buildHL7CEType(pi.getMartialStatus()));
        }

        if (!isEmptyHL7CEType(pi.getReligion())) {
            hl7PIDType.setReligion(buildHL7CEType(pi.getReligion()));
        }

        //hl7PIDType.setPatientAccountNumber(buildHL7CXType(pi.getPatientAccountNumber()));
        if( StringUtils.isNotEmpty(pi.getSsnNumberPatient())) {
            hl7PIDType.setSSNNumberPatient(pi.getSsnNumberPatient());
        }

        if (!isEmptyHL7DLNType(pi.getDriverLicenseNumberPatient())) {
            hl7PIDType.setDriversLicenseNumberPatient(buildHL7DLNType(pi.getDriverLicenseNumberPatient()));
        }

        for (Cx cx : pi.getMotherIdentifier()) {
            hl7PIDType.getMothersIdentifier().add(buildHL7CXType(cx));
        }

        for (Ce ce : pi.getEthnicGroup()) {
            hl7PIDType.getEthnicGroup().add(buildHL7CWEType(ce));
        }

        hl7PIDType.setBirthPlace(pi.getBirthPlace());
        hl7PIDType.setMultipleBirthIndicator(pi.getMultipleBirthIndicator());
        hl7PIDType.setBirthOrder(buildHl7NMType(pi.getBirthOrder()));

        for (Ce ce : pi.getCitizenship()) {
            hl7PIDType.getCitizenship().add(buildHL7CWEType(ce));
        }

        if (!isEmptyHL7CEType(pi.getVeteranStatus())) {
            hl7PIDType.setVeteransMilitaryStatus(buildHL7CWEType(pi.getVeteranStatus()));
        }

        if (!isEmptyHL7CEType(pi.getNationality())) {
            hl7PIDType.setNationality(buildHL7CWEType(pi.getNationality()));
        }

        hl7PIDType.setPatientDeathDateAndTime(buildHL7TSType(pi.getPatientDeathDateAndTime()));
        hl7PIDType.setPatientDeathIndicator(pi.getPatientDeathIndicator());
        hl7PIDType.setIdentityUnknownIndicator(pi.getIdentityUnknownIndicator());

        for (String s : pi.getIdentityReliabilityCode()) {
            hl7PIDType.getIdentityReliabilityCode().add(s);
        }

        hl7PIDType.setLastUpdateDateTime(buildHL7TSType(pi.getLastUpdateDateTime()));

        if (!isEmptyHL7HDType(pi.getLastUpdateFacility())) {
            hl7PIDType.setLastUpdateFacility(buildHL7HDType(pi.getLastUpdateFacility()));
        }

        if (!isEmptyHL7CEType(pi.getSpeciesCode())) {
            hl7PIDType.setSpeciesCode(buildHL7CEType(pi.getSpeciesCode()));
        }

        if (!isEmptyHL7CEType(pi.getBreedCode())) {
            hl7PIDType.setBreedCode(buildHL7CEType(pi.getBreedCode()));
        }

        hl7PIDType.setStrain(pi.getStrain());

        if (!isEmptyHL7CEType(pi.getProductionClassCode())) {
            hl7PIDType.getProductionClassCode().add(buildHL7CEType(pi.getProductionClassCode()));
        }

        for (Cwe cwe : pi.getTribalCitizenship()) {
            hl7PIDType.getTribalCitizenship().add(buildHL7CWEType(cwe));
        }

        return hl7PIDType;
    }

    private HL7ORCType buildHL7ORCType(CommonOrder commonOrder) {
        //if(null == commonOrder.getOrderControl()) return null;

        HL7ORCType hl7ORCType = new HL7ORCType();

        hl7ORCType.setOrderControl(buildEmptyValue(commonOrder.getOrderControl()));

        if (!isEmptyHL7EIType(commonOrder.getPlacerOrderNumber())) {
            hl7ORCType.setPlacerOrderNumber(buildHL7EIType(commonOrder.getPlacerOrderNumber()));
        }

        if (!isEmptyHL7EIType(commonOrder.getFillerOrderNumber())) {
            hl7ORCType.setFillerOrderNumber(buildHL7EIType(commonOrder.getFillerOrderNumber()));
        }

        if (!isEmptyHL7EIType(commonOrder.getPlacerGroupNumber())) {
            HL7EIType eiType = buildHL7EIType(commonOrder.getPlacerGroupNumber());
            if (null != eiType) hl7ORCType.setPlacerGroupNumber(eiType);
        }

        hl7ORCType.setOrderStatus(commonOrder.getOrderStatus());
        hl7ORCType.setResponseFlag(commonOrder.getResponseFlag());

        for (Tq tq : commonOrder.getQuantityTiming()) {
            hl7ORCType.getQuantityTiming().add(buildHL7TQType(tq));
        }

        HL7EIPType hl7EIPType = buildHL7EIPType(commonOrder.getParentOrder());
        if (null != hl7EIPType) {
            hl7ORCType.setParent(hl7EIPType);
        }

        hl7ORCType.setDateTimeOfTransaction(buildHL7TSType(commonOrder.getDateTimeOfTransaction()));

        if (commonOrder.getEnteredBy().size() > 0) {
            for (Xcn xcn : commonOrder.getEnteredBy()) {
                hl7ORCType.getEnteredBy().add(buildHL7XCNType(xcn));
            }
        }

        if (commonOrder.getVerifiedBy().size() > 0) {
            for (Xcn xcn : commonOrder.getVerifiedBy()) {
                hl7ORCType.getVerifiedBy().add(buildHL7XCNType(xcn));
            }
        }

        if (commonOrder.getOrderingProvider().size() > 0) {
            for (Xcn xcn : commonOrder.getOrderingProvider()) {
                hl7ORCType.getOrderingProvider().add(buildHL7XCNType(xcn));
            }
        }

        if (!isEmptyHL7PLType(commonOrder.getEntererLocation())) {
            hl7ORCType.setEnterersLocation(buildHL7PLType(commonOrder.getEntererLocation()));
        }

        for (Xtn xtn : commonOrder.getCallBackPhoneNumber()) {
            hl7ORCType.getCallBackPhoneNumber().add(buildHL7XTNType(xtn));
        }

        hl7ORCType.setOrderEffectiveDateTime(buildHL7TSType(commonOrder.getOrderEffectiveDateTime()));

        if (!isEmptyHL7CEType(commonOrder.getOrderControlCodeReason())) {
            hl7ORCType.setOrderControlCodeReason(buildHL7CWEType(commonOrder.getOrderControlCodeReason()));
        }

        if (!isEmptyHL7CEType(commonOrder.getEnteringOrganization())) {
            hl7ORCType.setEnteringOrganization(buildHL7CWEType(commonOrder.getEnteringOrganization()));
        }

        if (!isEmptyHL7CEType(commonOrder.getEnteringOrganization())) {
            hl7ORCType.setEnteringDevice(buildHL7CWEType(commonOrder.getEnteringDevice()));
        }

        if (commonOrder.getActionBy().size() > 0) {
            for (Xcn xcn : commonOrder.getActionBy()) {
                hl7ORCType.getActionBy().add(buildHL7XCNType(xcn));
            }
        }

        if (!isEmptyHL7CEType(commonOrder.getAdvancedBeneficiaryNoticeCode())) {
            hl7ORCType.setAdvancedBeneficiaryNoticeCode(buildHL7CWEType(commonOrder.getAdvancedBeneficiaryNoticeCode()));
        }

        if (commonOrder.getOrderingFacilityName().size() > 0) {
            for (Xon xon : commonOrder.getOrderingFacilityName()) {
                hl7ORCType.getOrderingFacilityName().add(buildHL7XONType(xon));
            }
        }

        if (commonOrder.getOrderingFacilityAddress().size() > 0) {
            for (Xad xad : commonOrder.getOrderingFacilityAddress()) {
                hl7ORCType.getOrderingFacilityAddress().add(buildHL7XADType(xad));
            }
        }

        if (commonOrder.getOrderingFacilityPhoneNumber().size() > 0) {
            for (Xtn xtn : commonOrder.getOrderingFacilityPhoneNumber()) {
                hl7ORCType.getOrderingFacilityPhoneNumber().add(buildHL7XTNType(xtn));
            }
        }

        if (commonOrder.getOrderingProviderAddress().size() > 0) {
            for (Xad xad : commonOrder.getOrderingProviderAddress()) {
                hl7ORCType.getOrderingProviderAddress().add(buildHL7XADType(xad));
            }
        }

        if (!isEmptyHL7CWEType(commonOrder.getOrderStatusModifier())) {
            hl7ORCType.setOrderStatusModifier(buildHL7CWEType(commonOrder.getOrderStatusModifier()));
        }

        if (!isEmptyHL7CWEType(commonOrder.getAdvancedBeneficiaryNoticeOverrideReason())) {
            hl7ORCType.setAdvancedBeneficiaryNoticeOverrideReason(buildHL7CWEType(commonOrder.getAdvancedBeneficiaryNoticeOverrideReason()));
        }

        hl7ORCType.setFillersExpectedAvailabilityDateTime(buildHL7TSType(commonOrder.getFillerExpectedAvailabilityDateTime()));

        if (!isEmptyHL7CWEType(commonOrder.getConfidentialityCode())) {
            hl7ORCType.setConfidentialityCode(buildHL7CWEType(commonOrder.getConfidentialityCode()));
        }

        if (!isEmptyHL7CWEType(commonOrder.getOrderType())) {
            hl7ORCType.setOrderType(buildHL7CWEType(commonOrder.getOrderType()));
        }

        if (!isEmptyHL7CNEType(commonOrder.getEntererAuthorizationMode())) {
            hl7ORCType.setEntererAuthorizationMode(buildHL7CNEType(commonOrder.getEntererAuthorizationMode()));
        }

        if (!isEmptyHL7CWEType(commonOrder.getParentUniversalServiceIdentifier())) {
            hl7ORCType.setParentUniversalServiceIdentifier(buildHL7CWEType(commonOrder.getParentUniversalServiceIdentifier()));
        }

        return hl7ORCType;
    }

    private HL7ContinuationPointerType buildHL7ContinuationPointerType(OruR1 oruR1) {
        HL7ContinuationPointerType hl7ContinuationPointerType = new HL7ContinuationPointerType();

        if (null == oruR1.getContinuationPointer()) {
            return hl7ContinuationPointerType;
        }

        hl7ContinuationPointerType.setContinuationPointer(oruR1.getContinuationPointer().getContinuationPointer());
        hl7ContinuationPointerType.setContinuationStyle(oruR1.getContinuationPointer().getContinuationStyle());

        return hl7ContinuationPointerType;
    }

    private HL7EIPType buildHL7EIPType(Eip eip) {
        if (isEmptyHL7EIPType(eip)) return null;

        HL7EIPType hl7EIPType = new HL7EIPType();

        if (!isEmptyHL7EIType(eip.getPlacerAssignedIdentifier())) {
            hl7EIPType.setHL7PlacerAssignedIdentifier(buildHL7EIType(eip.getPlacerAssignedIdentifier()));
        }

        hl7EIPType.setHL7FillerAssignedIdentifier(buildHL7EIType(eip.getFillerAssignedIdentifier()));

        return hl7EIPType;
    }

    private HL7DLNType buildHL7DLNType(Dln dln) {
        HL7DLNType hl7DLNType = new HL7DLNType();

        hl7DLNType.setHL7LicenseNumber(buildEmptyValue(dln.getLicenseNumber()));
        hl7DLNType.setHL7IssuingStateProvinceCountry(dln.getIssuedStateCountry());
        hl7DLNType.setHL7ExpirationDate(buildHL7DTType(dln.getExpirationDate()));

        return hl7DLNType;
    }

    private HL7TQType buildHL7TQType(Tq tq) {
        HL7TQType hl7TQType = new HL7TQType();

        hl7TQType.setHL7Quantity(buildHL7CQType(tq.getQuantity()));
        hl7TQType.setHL7Interval(buildHL7RIType(tq.getInterval()));
        hl7TQType.setHL7Duration(tq.getDuration());
        hl7TQType.setHL7StartDateTime(buildHL7TSType(tq.getStartDateTime()));
        hl7TQType.setHL7EndDateTime(buildHL7TSType(tq.getEndDateTime()));
        hl7TQType.setHL7Priority(tq.getPriority());
        hl7TQType.setHL7Condition(tq.getCondition());
        hl7TQType.setHL7Text(buildHL7TXType(tq.getText()));
        hl7TQType.setHL7Conjunction(tq.getConjunction());
        hl7TQType.setHL7OrderSequencing(buildHL7OSDType(tq.getOrderSequencing()));
        hl7TQType.setHL7OccurrenceDuration(buildHL7CEType(tq.getOccurrenceDuration()));
        hl7TQType.setHL7TotalOccurrences(buildHl7NMType(tq.getTotalOccurrences()));

        return hl7TQType;
    }

    private HL7OSDType buildHL7OSDType(Osd osd) {
        HL7OSDType hl7OSDType = new HL7OSDType();

        hl7OSDType.setHL7SequenceResultsFlag(buildEmptyValue(osd.getSequenceResultFlag()));
        hl7OSDType.setHL7PlacerOrderNumberEntityIdentifier(buildEmptyValue(osd.getPlacerOrderNumberEntityIdentifier()));
        hl7OSDType.setHL7PlacerOrderNumberNamespaceID(osd.getPlacerOrderNumberNamespaceId());
        hl7OSDType.setHL7FillerOrderNumberEntityIdentifier(buildEmptyValue(osd.getFillerOrderNumberEntityIdentifier()));
        hl7OSDType.setHL7FillerOrderNumberNamespaceID(osd.getPlacerOrderNumberNamespaceId());
        hl7OSDType.setHL7SequenceConditionValue(osd.getSequenceConditionValue());
        hl7OSDType.setHL7MaximumNumberOfRepeats(buildHl7NMType(osd.getMaximumNumberOfRepeats()));
        hl7OSDType.setHL7PlacerOrderNumberUniversalID(buildEmptyValue(osd.getPlacerOrderNumberUniversalId()));
        hl7OSDType.setHL7PlacerOrderNumberUniversalIDType(buildEmptyValue(osd.getPlacerOrderNumberUniversalIdType()));
        hl7OSDType.setHL7FillerOrderNumberUniversalID(buildEmptyValue(osd.getFillerOrderNumberUniversalId()));
        hl7OSDType.setHL7FillerOrderNumberUniversalIDType(osd.getFillerOrderNumberUniversalIdType());

        return hl7OSDType;
    }

    private HL7TXType buildHL7TXType(String txt) {
        HL7TXType hl7TXType = new HL7TXType();

        hl7TXType.setHL7String(txt);

        return hl7TXType;
    }

    private HL7RIType buildHL7RIType(Ri ri) {
        HL7RIType hl7RIType = new HL7RIType();

        hl7RIType.setRepeatPattern(ri.getRepeatPattern());
        hl7RIType.setExplicitTimeInterval(ri.getExplicitTimeInterval());

        return hl7RIType;
    }

    private HL7CQType buildHL7CQType(Cq cq) {
        HL7CQType hl7CQType = new HL7CQType();

        hl7CQType.setHL7Quantity(buildHl7NMType(cq.getQuantity()));
        hl7CQType.setHL7Units(buildHL7CWEType(cq.getUnits()));

        return hl7CQType;
    }

    private HL7XTNType buildHL7XTNType(Xtn xtn) {
        HL7XTNType hl7XTNType = new HL7XTNType();

        hl7XTNType.setHL7TelephoneNumber(xtn.getTelephoneNumber());
        hl7XTNType.setHL7TelecommunicationUseCode(xtn.getTeleComCode());
        hl7XTNType.setHL7TelecommunicationEquipmentType(xtn.getTeleComEquipmentType());
        hl7XTNType.setHL7EmailAddress(xtn.getEmailAddress());
        hl7XTNType.setHL7CountryCode(buildHl7NMType(xtn.getCountryCode()));
        hl7XTNType.setHL7AreaCityCode(buildHl7NMType(xtn.getCityCode()));
        hl7XTNType.setHL7LocalNumber(buildHl7NMType(xtn.getLocalNumber()));
        hl7XTNType.setHL7Extension(buildHl7NMType(xtn.getExtension()));
        hl7XTNType.setHL7AnyText(xtn.getAnyText());
        hl7XTNType.setHL7ExtensionPrefix(xtn.getExtPrefix());
        hl7XTNType.setHL7SpeedDialCode(xtn.getSpeedDialCode());
        hl7XTNType.setHL7UnformattedTelephonenumber(xtn.getUnformattedTeleNumber());

        return hl7XTNType;
    }

    private HL7XADType buildHL7XADType(Xad xad) {
        HL7XADType hl7XADType = new HL7XADType();

        hl7XADType.setHL7StreetAddress(buildHL7SADType(xad.getStreetAddress()));
        hl7XADType.setHL7OtherDesignation(xad.getOtherDesignation());
        hl7XADType.setHL7City(xad.getCity());
        hl7XADType.setHL7StateOrProvince(xad.getState());
        hl7XADType.setHL7ZipOrPostalCode(xad.getZip());
        hl7XADType.setHL7Country(xad.getCountry());
        hl7XADType.setHL7AddressType(xad.getAddressType());
        //hl7XADType.setHL7OtherGeographicDesignation(xad.getOtherGeographic());
        hl7XADType.setHL7CountyParishCode(xad.getCountyCode());
        hl7XADType.setHL7CensusTract(xad.getCensusTract());
        hl7XADType.setHL7AddressRepresentationCode(xad.getAddressRepresentationCode());

        if (!isEmptyHL7DRType(xad.getAddressValidityRange())) {
            hl7XADType.setHL7AddressValidityRange(buildHL7DRType(xad.getAddressValidityRange()));
        }

        hl7XADType.setHL7EffectiveDate(buildHL7TSType(xad.getEffectiveDate()));
        hl7XADType.setHL7ExpirationDate(buildHL7TSType(xad.getExpirationDate()));

        return hl7XADType;
    }

    private HL7SADType buildHL7SADType(Sad sad) {
        HL7SADType hl7SADType = new HL7SADType();

        hl7SADType.setHL7StreetOrMailingAddress(sad.getStreetMailingAddress());
        hl7SADType.setHL7StreetName(sad.getStreetName());
        hl7SADType.setHL7DwellingNumber(sad.getDwellingNumber());

        return hl7SADType;
    }

    private HL7XPNType buildHL7XPNType(Xpn xpn) {
        HL7XPNType hl7XPNType = new HL7XPNType();

        hl7XPNType.setHL7FamilyName(buildHl7FNType(xpn.getFamilyName()));
        hl7XPNType.setHL7GivenName(xpn.getGivenName());
        hl7XPNType.setHL7SecondAndFurtherGivenNamesOrInitialsThereof(xpn.getSecondAndFurtherGivenNameOrInitial());
        hl7XPNType.setHL7Suffix(xpn.getSuffix());
        hl7XPNType.setHL7Prefix(xpn.getPrefix());
        hl7XPNType.setHL7Degree(xpn.getDegree());
        hl7XPNType.setHL7NameTypeCode(xpn.getNameTypeCode());
        hl7XPNType.setHL7NameRepresentationCode(xpn.getNameRepresentationCode());

        if (!isEmptyHL7CEType(xpn.getNameContext())) {
            hl7XPNType.setHL7NameContext(buildHL7CEType(xpn.getNameContext()));
        }

        if (!isEmptyHL7DRType(xpn.getNameValidityRange())) {
            hl7XPNType.setHL7NameValidityRange(buildHL7DRType(xpn.getNameValidityRange()));
        }

        hl7XPNType.setHL7NameAssemblyOrder(xpn.getNameAssemblyOrder());
        hl7XPNType.setHL7EffectiveDate(buildHL7TSType(xpn.getEffectiveDate()));
        hl7XPNType.setHL7ExpirationDate(buildHL7TSType(xpn.getExpirationDate()));
        //hl7XPNType.setHL7ProfessionalSuffix(xpn.getProfessionalSuffix());

        return hl7XPNType;
    }

    private HL7DRType buildHL7DRType(Dr dr) {
        HL7DRType hl7DRType = new HL7DRType();
        hl7DRType.setHL7RangeStartDateTime(buildHL7TSType(dr.getRangeStartDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));
        hl7DRType.setHL7RangeEndDateTime(buildHL7TSType(dr.getRangeEndDateTime(), TS_FMT_DATE_HOUR_MINUTE_ONLY));
        return hl7DRType;
    }

    private HL7FNType buildHl7FNType(Fn fn) {
        HL7FNType hl7FNType = new HL7FNType();

        hl7FNType.setHL7Surname(buildEmptyValue(fn.getSurname()));

        if( StringUtils.isNotEmpty(fn.getOwnSurnamePrefix())) {
            hl7FNType.setHL7OwnSurnamePrefix(buildEmptyValue(fn.getOwnSurnamePrefix()));
        }

        if( StringUtils.isNotEmpty(fn.getOwnSurname())) {
            hl7FNType.setHL7OwnSurname(buildEmptyValue(fn.getOwnSurname()));
        }

        if( StringUtils.isNotEmpty(fn.getSurnamePrefixFromPartner())) {
            hl7FNType.setHL7SurnamePrefixFromPartnerSpouse(buildEmptyValue(fn.getSurnamePrefixFromPartner()));
        }

        if( StringUtils.isNotEmpty(fn.getSurnamePrefixFromPartner())) {
            hl7FNType.setHL7SurnameFromPartnerSpouse(buildEmptyValue(fn.getSurnameFromPartner()));
        }

        return hl7FNType;
    }

    private List<HL7SoftwareSegmentType> buildSoftwareSegments(OruR1 oruR1) {
        ArrayList<HL7SoftwareSegmentType> destList = new ArrayList<>();

        if (null == oruR1.getSoftwareSegment()) {
            return destList;
        }

        for (SoftwareSegment ss : oruR1.getSoftwareSegment()) {
            HL7SoftwareSegmentType segType = new HL7SoftwareSegmentType();
            segType.setSoftwareVendorOrganization(buildHL7XONType(ss.getSoftwareVendorOrganization()));
            segType.setSoftwareCertifiedVersionOrReleaseNumber(ss.getSoftwareCertifiedVersionOrReleaseNumber());
            segType.setSoftwareProductName(ss.getSoftwareProductName());
            segType.setSoftwareBinaryID(ss.getSoftwareBinaryId());
            segType.setSoftwareProductInformation(buildHL7TXType(ss.getSoftwareProductInformation()));
            segType.setSoftwareInstallDate(buildHL7TSType(ss.getSoftwareInstallDate(), TS_FMT_DATE_ONLY));
            destList.add(segType);
        }

        return destList;
    }

    private HL7MSHType buildHL7MSHType(MessageHeader mh) {
        HL7MSHType mshType = new HL7MSHType();

        mshType.setFieldSeparator(mh.getFieldSeparator());
        mshType.setEncodingCharacters(mh.getEncodingCharacters());

        mshType.setSendingApplication(buildHL7HDType(mh.getSendingApplication()));
        mshType.setSendingFacility(buildHL7HDType(mh.getSendingFacility()));
        mshType.setReceivingApplication(buildHL7HDType(mh.getReceivingApplication()));
        mshType.setReceivingFacility(buildHL7HDType(mh.getReceivingFacility()));
        mshType.setDateTimeOfMessage(buildHL7TSType(mh.getDateTimeOfMessage(), TS_FMT_DATE_HOUR_ONLY));
        mshType.setSecurity("");
        mshType.setMessageType(buildHl7MsgType());
        mshType.setMessageControlID(mh.getMessageControlId());
        mshType.setProcessingID(buildHl7PtType(mh.getProcessingId()));
        mshType.setVersionID(buildHL7VidType(mh.getVersionId()));
        mshType.setSequenceNumber(null);
        if (null != mh.getSequenceNumber()) {
            mshType.setSequenceNumber(buildHl7NMType(mh.getSequenceNumber()));
        }

        if (null != mh.getCharacterSet()) {
            mshType.getCharacterSet().addAll(mh.getCharacterSet());
        }

        mshType.setContinuationPointer(mh.getContinuationPointer());
        mshType.setAcceptAcknowledgmentType(mh.getAcceptAckType());
        mshType.setApplicationAcknowledgmentType(mh.getApplicationAckType());
        mshType.setCountryCode(mh.getCountryCode());
        mshType.setAlternateCharacterSetHandlingScheme(mh.getAlternateCharacterSetHandlingScheme());

        if (null != mh.getMessageProfileIdentifier()) {
            mshType.getMessageProfileIdentifier().addAll(buildMessageProfileIdentifiers(mh.getMessageProfileIdentifier()));
        }

        return mshType;
    }

    private List<HL7EIType> buildMessageProfileIdentifiers(List<Ei> srcItems) {
        ArrayList<HL7EIType> destItems = new ArrayList<>();

        for (Ei ei : srcItems) {
            destItems.add(buildHL7EIType(ei));
        }

        return destItems;
    }

    private HL7EIType buildHL7EIType(Ei ei) {
        if (null == ei) return null;

        HL7EIType hl7EIType = new HL7EIType();

        hl7EIType.setHL7EntityIdentifier(ei.getEntityIdentifier());
        hl7EIType.setHL7NamespaceID(ei.getNameSpaceId());
        hl7EIType.setHL7UniversalID(ei.getUniversalId());
        hl7EIType.setHL7UniversalIDType(ei.getUniversalIdType());

        return hl7EIType;
    }

    private HL7NMType buildHl7NMType(String value) {
        HL7NMType hl7NMType = new HL7NMType();

        try {
            hl7NMType.setHL7Numeric(BigInteger.valueOf(Integer.valueOf(value)));
        } catch (Exception e) {
            // ignore, sequence number not present in the samples
        }

        return hl7NMType;
    }

    private HL7VIDType buildHL7VidType(Vid vid) {
        HL7VIDType hl7VIDType = new HL7VIDType();

        hl7VIDType.setHL7VersionID(vid.getVersionId());

        if (!isEmptyHL7CEType(vid.getInternationalizationCode())) {
            hl7VIDType.setHL7InternationalizationCode(buildHL7CEType(vid.getInternationalizationCode()));
        }

        if (!isEmptyHL7CEType(vid.getInternationalVersionId())) {
            hl7VIDType.setHL7InternationalVersionID(buildHL7CEType(vid.getInternationalVersionId()));
        }

        return hl7VIDType;
    }

    private HL7CEType buildHL7CEType(Ce ce) {
        HL7CEType hl7CEType = new HL7CEType();

        if (null == ce) {
            hl7CEType.setHL7Identifier(EMPTY_STRING);
            return hl7CEType;
        }

        if( StringUtils.isNotEmpty(ce.getAlternateIdentifier())) {
            hl7CEType.setHL7AlternateIdentifier(buildEmptyValue(ce.getAlternateIdentifier()));
        }

        if( StringUtils.isNotEmpty(ce.getAlternateText())) {
            hl7CEType.setHL7AlternateText(buildEmptyValue(ce.getAlternateText()));
        }

        if( StringUtils.isNotEmpty(ce.getIdentifier())) {
            hl7CEType.setHL7Identifier(buildEmptyValue(ce.getIdentifier()));
        }

        if( StringUtils.isNotEmpty(ce.getNameOfCodingSystem())) {
            hl7CEType.setHL7NameofCodingSystem(buildEmptyValue(ce.getNameOfCodingSystem()));
        }

        if( StringUtils.isNotEmpty(ce.getText())) {
            hl7CEType.setHL7Text(buildEmptyValue(ce.getText()));
        }

        if( StringUtils.isNotEmpty(ce.getNameOfAlternateCodingSystem())) {
            hl7CEType.setHL7NameofAlternateCodingSystem(buildEmptyValue(ce.getNameOfAlternateCodingSystem()));
        }

        return hl7CEType;
    }

    private HL7PTType buildHl7PtType(Pt pt) {
        HL7PTType hl7PTType = new HL7PTType();
        hl7PTType.setHL7ProcessingID(pt.getProcessingId());

        hl7PTType.setHL7ProcessingMode("");
        if (null != pt.getProcessingMode()) {
            hl7PTType.setHL7ProcessingMode(pt.getProcessingMode());
        }

        return hl7PTType;
    }

    private HL7MSGType buildHl7MsgType() {
        HL7MSGType hl7MSGType = new HL7MSGType();

        hl7MSGType.setMessageCode("ORU");
        hl7MSGType.setTriggerEvent("R01");
        hl7MSGType.setMessageStructure("ORU_R01");

        return hl7MSGType;
    }

    private HL7TSType buildHL7TSType(Ts ts) {
        return buildHL7TSType(ts, TS_FMT_ALL);
    }

    private HL7TSType buildHL7TSType(Ts ts, int outFmt) {
        if ((null == ts) || (null == ts.time)) return null;
        return buildHL7TSType(ts.time, outFmt);
    }

    private HL7TSType buildHL7TSType(String ts) {
        return buildHL7TSType(ts, TS_FMT_ALL);
    }

    private HL7TSType buildHL7TSType(String ts, int outFmt) {
        HL7TSType hl7TSType = new HL7TSType();

        if (null == ts) return hl7TSType;

        DateTimeFormatter tsFormatter = formatter;
        if (ts.indexOf("-") > 0) {
            tsFormatter = formatterWithZone;
        } else {
            if (ts.length() <= 8) {
                ts = ts + "000000";
            } else if (ts.length() <= 12) {
                ts = ts + "00";
            } else if (ts.length() > 14) {
                ts = ts.substring(0, 14);
            }
        }

        LocalDateTime localDateTime = LocalDateTime.parse(ts, tsFormatter);

        hl7TSType.setGmtOffset("");
        if (outFmt == TS_FMT_DATE_ONLY) {
            hl7TSType.setYear(BigInteger.valueOf(localDateTime.getYear()));
            hl7TSType.setMonth(BigInteger.valueOf(localDateTime.getMonthValue()));
            hl7TSType.setDay(BigInteger.valueOf(localDateTime.getDayOfMonth()));
        } else if (outFmt == TS_FMT_DATE_HOUR_ONLY) {
            hl7TSType.setYear(BigInteger.valueOf(localDateTime.getYear()));
            hl7TSType.setMonth(BigInteger.valueOf(localDateTime.getMonthValue()));
            hl7TSType.setDay(BigInteger.valueOf(localDateTime.getDayOfMonth()));
            hl7TSType.setHours(BigInteger.valueOf(localDateTime.getHour()));
        } else if (outFmt == TS_FMT_DATE_HOUR_MINUTE_ONLY) {
            hl7TSType.setYear(BigInteger.valueOf(localDateTime.getYear()));
            hl7TSType.setMonth(BigInteger.valueOf(localDateTime.getMonthValue()));
            hl7TSType.setDay(BigInteger.valueOf(localDateTime.getDayOfMonth()));
            hl7TSType.setHours(BigInteger.valueOf(localDateTime.getHour()));
            hl7TSType.setMinutes(BigInteger.valueOf(localDateTime.getMinute()));
        } else if (outFmt == TS_FMT_ALL) {
            hl7TSType.setYear(BigInteger.valueOf(localDateTime.getYear()));
            hl7TSType.setMonth(BigInteger.valueOf(localDateTime.getMonthValue()));
            hl7TSType.setDay(BigInteger.valueOf(localDateTime.getDayOfMonth()));
            hl7TSType.setHours(BigInteger.valueOf(localDateTime.getHour()));
            hl7TSType.setMinutes(BigInteger.valueOf(localDateTime.getMinute()));
            hl7TSType.setSeconds(BigInteger.valueOf(localDateTime.getSecond()));
        }
        return hl7TSType;
    }

    private HL7HDType buildHL7HDType(Hd hdType) {
        HL7HDType hl7HDType = new HL7HDType();

        if (null != hdType) {
            hl7HDType.setHL7UniversalID(hdType.getUniversalId());
            hl7HDType.setHL7UniversalIDType(hdType.getUniversalIdType());
            hl7HDType.setHL7NamespaceID(hdType.getNameSpaceId());
        }

        return hl7HDType;
    }

    private HL7CWEType buildHL7CWEType(Ce ce) {
        HL7CWEType hl7CWEType = new HL7CWEType();

        hl7CWEType.setHL7Identifier(ce.getIdentifier());

        if( StringUtils.isNotEmpty(ce.getText())) {
            hl7CWEType.setHL7Text(ce.getText());
        }

        if( StringUtils.isNotEmpty(ce.getNameOfCodingSystem())) {
            hl7CWEType.setHL7NameofCodingSystem(ce.getNameOfCodingSystem());
        }

        if( StringUtils.isNotEmpty(ce.getAlternateIdentifier())) {
            hl7CWEType.setHL7AlternateIdentifier(ce.getAlternateIdentifier());
        }

        if( StringUtils.isNotEmpty(ce.getAlternateText())) {
            hl7CWEType.setHL7AlternateText(ce.getAlternateText());
        }

        return hl7CWEType;
    }

    private HL7DTType buildHL7DTType(String hld7Dt) {
        if (hld7Dt == null) return null;

        if (hld7Dt.length() <= 8) {
            hld7Dt = hld7Dt + "000000";
        } else if (hld7Dt.length() <= 12) {
            hld7Dt = hld7Dt + "00";
        } else if (hld7Dt.length() > 14) {
            hld7Dt = hld7Dt.substring(0, (14 - 1));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDtTime = LocalDateTime.parse(hld7Dt, formatter);

        HL7DTType hl7DTType = new HL7DTType();

        hl7DTType.setYear(BigInteger.valueOf(localDtTime.getYear()));
        hl7DTType.setMonth(BigInteger.valueOf(localDtTime.getMonthValue()));
        hl7DTType.setDay(BigInteger.valueOf(localDtTime.getDayOfMonth()));

        return hl7DTType;
    }

    private boolean isEmptyHL7CEType(Ce ce) {
        if (null == ce) return true;
        if (null == ce.getIdentifier()) return true;
        if( StringUtils.isEmpty(ce.getIdentifier()) ) return true;

        return false;
    }

    private boolean isEmptyHL7PRLType(Prl prl) {
        return isEmptyHL7CEType(prl.getParentObservationIdentifier());
    }

    private boolean isEmptyHL7EIType(Ei ei) {
        if( StringUtils.isEmpty(ei.getEntityIdentifier()) ) return true;
        return false;
    }

    private boolean isEmptyHL7EIPType(Eip eip) {
        return isEmptyHL7EIType(eip.getPlacerAssignedIdentifier()) && isEmptyHL7EIType(eip.getFillerAssignedIdentifier());
    }

    private boolean isEmptyHL7DLNType(Dln dln) {
        if (null == dln) return true;
        if( StringUtils.isEmpty(dln.getLicenseNumber()) ) return true;
        return false;
    }

    private boolean isEmptyHL7DRType(Dr dr) {
        if ((null != dr.getRangeStartDateTime()) && (null == dr.getRangeStartDateTime().getTime())) return true;
        if ((null != dr.getRangeEndDateTime()) && (null == dr.getRangeEndDateTime().getTime())) return true;
        return false;
    }

    private boolean isEmptyHL7HDType(Hd hdType) {
        if (null == hdType) return true;
        if( StringUtils.isEmpty(hdType.getUniversalId()) ) return true;
        return false;
    }

    private boolean isEmptyHL7CWEType(Cwe ce) {
        if (null == ce) return true;
        if( StringUtils.isEmpty(ce.getIdentifier()) ) return true;
        return false;
    }

    private boolean isEmptyHL7CNEType(Cne cne) {
        if (null == cne) return true;
        if( StringUtils.isEmpty(cne.getIdentifier()) ) return true;
        return false;
    }

    private boolean isEmptyHL7PID1Type(PatientAdditionalDemographic pad) {
        if (null == pad) return true;
        if (pad.getLivingDependency().size() <= 0) return true;
        return false;
    }

    private boolean isEmptyHL7PIV1Type(PatientVisit pv) {
        if (null == pv) return true;
        if( StringUtils.isEmpty(pv.getSetIdPv1()) ) return true;
        return false;
    }

    private boolean isEmptyHL7PLType(Pl pl) {
        if (null == pl) return true;
        if( StringUtils.isEmpty(pl.getPointOfCare()) ) return true;
        if( isEmptyHL7HDType(pl.getFacility()) ) return true;
        return false;
    }

    private boolean isEmptyHL7PIV2Type(PatientVisitAdditional pva) {
        if(null == pva) return true;
        if( isEmptyHL7PLType(pva.getPriorPendingLocation())) return true;
        return false;
    }

    private boolean isEmptyHL7VisitType(Visit v) {
        if (null == v) return true;
        if (isEmptyHL7PIV1Type(v.getPatientVisit())) return true;
        return false;
    }

    private boolean isEmptyHL7CQType(Cq cq) {
        if (null == cq) return true;
        if( StringUtils.isEmpty(cq.getQuantity()) ) return true;
        return false;
    }

    private boolean isEmptyHL7SPSType(Sps sps) {
        if (null == sps) return true;
        if (isEmptyHL7CWEType(sps.getSpecimenSourceNameOrCode())) return true;
        return false;
    }

    private boolean isEmptyHL7MOCType(Moc moc) {
        if (null == moc) return true;
        if ((null == moc.getMonetaryAmount()) || (null == moc.getMonetaryAmount().getQuantity())) return true;
        return false;
    }

    private boolean isEmptyHL7NDLType(Ndl ndl) {
        if (null == ndl) return true;
        if ((null == ndl.getName()) || (null == ndl.getName().getIdNumber())) return true;
        return false;
    }

    private boolean isEmptyHL7CTDType(ContactData contactData) {
        if (null == contactData) return true;
        if ((null == contactData.getContactRole()) || (contactData.getContactRole().size() <= 0)) return true;
        return false;
    }

    private boolean isEmptyHL7XONType(Xon xon) {
        if (null == xon) return true;
        if( StringUtils.isEmpty(xon.getOrganizationName()) ) return true;
        return false;
    }

    private boolean isEmptyHL7XADType(Xad xad) {
        if (null == xad) return true;
        if ((null == xad.getStreetAddress()) || isEmptyHL7SADType(xad.getStreetAddress())) return true;
        return false;
    }

    private boolean isEmptyHL7SADType(Sad sad) {
        if (null == sad) return true;
        if( StringUtils.isEmpty(sad.getStreetMailingAddress()) ) return true;
        return false;
    }

    private boolean isEmptyHL7XCNType(Xcn xcn) {
        if (null == xcn) return true;
        if( StringUtils.isEmpty(xcn.getIdNumber()) ) return true;
        return false;
    }

    private boolean isEmptyHL7ContinuationPointerType(OruR1 oruR1) {
        if (null == oruR1) return true;
        if ((null == oruR1.getContinuationPointer()) || (null == oruR1.getContinuationPointer().getContinuationPointer()))
            return true;
        return false;
    }

    private boolean isEmptyHL7JCCType(Jcc jcc) {
        if(null == jcc) return true;
        if( StringUtils.isEmpty(jcc.getJobDescriptionText()) ) return true;
        return false;
    }

    private boolean isEmptyHL7ORCType(CommonOrder co) {
        if(null == co) return true;
        if( StringUtils.isEmpty(co.getOrderControl()) ) return true;
        return false;
    }
}
