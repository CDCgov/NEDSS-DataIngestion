package gov.cdc.dataingestion.nbs.converters;

import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisit;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantity;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantityRelationship;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.*;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;

import  gov.cdc.dataingestion.nbs.jaxb.*;

import  gov.cdc.dataingestion.hl7.helper.HL7Helper;
import  gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import  gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;

import  jakarta.xml.bind.JAXBContext;
import  jakarta.xml.bind.Marshaller;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  java.io.ByteArrayOutputStream;
import  java.math.BigInteger;
import  java.time.format.DateTimeFormatter;
import  java.time.LocalDateTime;
import  java.util.List;
import  java.util.ArrayList;

public class Hl7ToRhapsodysXmlConverter {
    private static Logger log = LoggerFactory.getLogger(Hl7ToRhapsodysXmlConverter.class);
    private static Hl7ToRhapsodysXmlConverter instance = new Hl7ToRhapsodysXmlConverter();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static Hl7ToRhapsodysXmlConverter getInstance() {
        return instance;
    }

    private Hl7ToRhapsodysXmlConverter() {
    }

    public String convert(String hl7Msg) throws Exception {
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
        return rhapsodyXml;
    }

    private HL7LabReportType buildHL7LabReportType(HL7ParsedMessage hl7ParsedMsg) {
        HL7LabReportType lbt = new HL7LabReportType();

        if( !hl7ParsedMsg.getParsedMessage().getClass().isNestmateOf(OruR1.class) ) {
            log.error("Unidentified Hl7 message type, supported type(s): OruR1");
            return lbt;
        }

        OruR1 oruR1 = (OruR1) hl7ParsedMsg.getParsedMessage();

        lbt.setHL7MSH(buildHL7MSHType(oruR1.getMessageHeader()));
        lbt.getHL7SoftwareSegment().addAll(buildSoftwareSegments(oruR1));


        lbt.setHL7ContinuationPointer(buildHl7ContinuationPointerType(oruR1));

        return lbt;
    }


    private HL7CNEType buildHL7CNEType(Cne cne) {
        HL7CNEType hl7CNEType = new HL7CNEType();

        hl7CNEType.setHL7Identifier(cne.getIdentifier());
        hl7CNEType.setHL7Text(cne.getText());
        hl7CNEType.setHL7NameofCodingSystem(cne.getNameOfAlternateCodingSystem());
        hl7CNEType.setHL7AlternateIdentifier(cne.getAlternateIdentifier());
        hl7CNEType.setHL7AlternateText(cne.getAlternateText());
        hl7CNEType.setHL7NameofAlternateCodingSystem(cne.getNameOfAlternateCodingSystem());
        hl7CNEType.setHL7CodingSystemVersionID(cne.getCodingSystemVersionId());
        hl7CNEType.setHL7AlternateCodingSystemVersionID(cne.getAlternateCodingSystemVersionId());
        hl7CNEType.setHL7OriginalText(cne.getOriginalText());

        return hl7CNEType;
    }

    private HL7CWEType buildHL7CWEType(Cwe cwe) {
        HL7CWEType hl7CWEType = new HL7CWEType();

        hl7CWEType.setHL7Identifier(cwe.getIdentifier());
        hl7CWEType.setHL7Text(cwe.getText());
        hl7CWEType.setHL7NameofCodingSystem(cwe.getNameOfCodingSystem());
        hl7CWEType.setHL7AlternateIdentifier(cwe.getAlternateIdentifier());
        hl7CWEType.setHL7AlternateText(cwe.getAlternateText());
        hl7CWEType.setHL7NameofAlternateCodingSystem(cwe.getNameOfAlterCodeSystem());
        hl7CWEType.setHL7CodingSystemVersionID(cwe.getCodeSystemVerId());
        hl7CWEType.setHL7AlternateCodingSystemVersionID(cwe.getNameOfAlterCodeSystem());
        hl7CWEType.setHL7OriginalText(cwe.getOriginalText());

        return hl7CWEType;
    }

    private HL7PLType buildHL7PLType(Pl pl) {
        HL7PLType hl7PLType = new HL7PLType();

        hl7PLType.setHL7PointofCare(pl.getPointOfCare());
        hl7PLType.setHL7Room(pl.getRoom());
        hl7PLType.setHL7Bed(pl.getBed());
        hl7PLType.setHL7Facility(buildHL7HDType(pl.getFacility()));
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

        hl7XONType.setHL7OrganizationIdentifier(xon.getOrganizationIdentifier());
        hl7XONType.setHL7OrganizationNameTypeCode(xon.getOrganizationNameTypeCode());
        hl7XONType.setHL7IDNumber(buildHl7NMType(xon.getIdNumber()));
        hl7XONType.setHL7CheckDigit(buildHl7NMType(xon.getCheckDigit()));
        hl7XONType.setHL7CheckDigitScheme(xon.getCheckDigitScheme());
        hl7XONType.setHL7AssigningAuthority(buildHL7HDType(xon.getAssignAuthority()));
        hl7XONType.setHL7IdentifierTypeCode(xon.getIdentifierTypeCode());
        hl7XONType.setHL7AssigningFacility(buildHL7HDType(xon.getAssignFacility()));
        hl7XONType.setHL7NameRepresentationCode(xon.getNameRepresentationCode());
        hl7XONType.setHL7OrganizationIdentifier(xon.getOrganizationIdentifier());

        return hl7XONType;
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
        hl7SPSType.setHL7SpecimenCollectionMethod(buildHL7TXType(sps.getSpecimenCollectionMethod()));
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
        hl7NDLType.setHL7Facility(buildHL7HDType(ndl.getFacility()));
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


    private HL7TQ2Type buildHL7TQ2Type(TimingQuantityRelationship tqr) {
        HL7TQ2Type hl7TQ2Type = new HL7TQ2Type();

        hl7TQ2Type.setSetIDTQ2(buildHL7SIType(tqr.getSetIdTq2()));
        hl7TQ2Type.getSequenceResultsFlag().add(tqr.getSequenceResultFlag());

        for(Ei ei : tqr.getRelatedPlacerNumber()) {
            hl7TQ2Type.getRelatedPlacerNumber().add(buildHL7EIType(ei));
        }

        for(Ei ei : tqr.getRelatedFillerNumber()) {
            hl7TQ2Type.getRelatedFillerNumber().add(buildHL7EIType(ei));
        }

        for(Ei ei : tqr.getRelatedPlacerGroupNumber()) {
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
        hl7XCNType.setHL7AssigningAuthority(buildHL7HDType(xcn.getAssignAuthority()));
        hl7XCNType.setHL7NameTypeCode(xcn.getNameTypeCode());
        hl7XCNType.setHL7IdentifierCheckDigit(xcn.getIdentifierCheckDigit());
        hl7XCNType.setHL7CheckDigitScheme(xcn.getCheckDigitScheme());
        hl7XCNType.setHL7IdentifierTypeCode(xcn.getIdentifierTypeCode());
        hl7XCNType.setHL7AssigningFacility(buildHL7HDType(xcn.getAssignFacility()));
        hl7XCNType.setHL7NameRepresentationCode(xcn.getNameRepresentationCode());
        hl7XCNType.setHL7NameContext(buildHL7CEType(xcn.getNameContext()));
        hl7XCNType.setHL7NameValidityRange(buildHL7DRType(xcn.getNameValidityRange()));
        hl7XCNType.setHL7NameAssemblyOrder(xcn.getNameAssemblyOrder());
        hl7XCNType.setHL7EffectiveDate(buildHL7TSType(xcn.getEffectiveDate()));
        hl7XCNType.setHL7ExpirationDate(buildHL7TSType(xcn.getExpirationDate()));
        hl7XCNType.setHL7ProfessionalSuffix(xcn.getPrefix());
        hl7XCNType.setHL7AssigningJurisdiction(buildHL7CWEType(xcn.getAssignJurisdiction()));
        hl7XCNType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(xcn.getAssignAgencyDept()));

        return hl7XCNType;
    }

    private HL7TQ1Type buildHL7TQ1Type(TimingQuantity tq) {
        HL7TQ1Type hl7TQ1Type = new HL7TQ1Type();

        hl7TQ1Type.setSetIDTQ1(buildHL7SIType(tq.getSetIdTq1()));
        hl7TQ1Type.setQuantity(buildHL7CQType(tq.getQuantity()));

        for(Rpt rpt : tq.getRepeatPattern()) {
            hl7TQ1Type.getRepeatPattern().add(buildHL7RPTType(rpt));
        }

        for(String s : tq.getExplicitTime()) {
            hl7TQ1Type.getExplicitTime().add(buildHL7TMType(s));
        }

        for(Cq cq : tq.getRelativeTimeAndUnits()) {
            hl7TQ1Type.getRelativeTimeAndUnits().add(buildHL7CQType(cq));
        }

        hl7TQ1Type.setServiceDuration(buildHL7CQType(tq.getServiceDuration()));
        hl7TQ1Type.setStartdatetime(buildHL7TSType(tq.getStartDateTime()));
        hl7TQ1Type.setEnddatetime(buildHL7TSType(tq.getEndDateTime()));

        for(Cwe cwe : tq.getPriority()) {
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

        for(String s : or.getObservationValue()) {
            hl7OBXType.getObservationValue().add(s);
        }

        hl7OBXType.setUnits(buildHL7CEType(or.getUnits()));
        hl7OBXType.setReferencesRange(or.getReferencesRange());

        /*
        for(String s : or.getAbnormalFlag()) {
            hl7OBXType.getAbnormalFlags().add(buildHL7CWEType(s));
        }
        */

        hl7OBXType.getProbability().add(buildHl7NMType(or.getProbability()));

        for(String s : or.getNatureOfAbnormalTest()) {
            hl7OBXType.getNatureOfAbnormalTest().add(s);
        }

        hl7OBXType.setObservationResultStatus(or.getObservationResultStatus());
        hl7OBXType.setEffectiveDateOfReferenceRangeValues(buildHL7TSType(or.getEffectiveDateOfReferenceRange()));
        hl7OBXType.setUserDefinedAccessChecks(or.getUserDefinedAccessChecks());
        hl7OBXType.setDateTimeOftheObservation(buildHL7TSType(or.getDateTimeOfTheObservation()));
        hl7OBXType.setProducersReference(buildHL7CWEType(or.getProducerId()));

        for(Xcn xcn : or.getResponsibleObserver()) {
            hl7OBXType.getResponsibleObserver().add(buildHL7XCNType(xcn));
        }

        for(Ce ce : or.getObservationMethod()) {
            hl7OBXType.getObservationMethod().add(buildHL7CEType(ce));
        }

        for(Ei ei : or.getEquipmentInstanceIdentifier()) {
            hl7OBXType.getEquipmentInstanceIdentifier().add(buildHL7EIType(ei));
        }

        hl7OBXType.setDateTimeOftheAnalysis(buildHL7TSType(or.getDateTimeOfTheAnalysis()));
        hl7OBXType.setReservedforHarmonizationWithV261(or.getReservedForHarmonizationWithV261());
        hl7OBXType.setReservedForHarmonizationwithV262(or.getReservedForHarmonizationWithV262());
        hl7OBXType.setReservedForHarmonizationWithV263(or.getReservedForHarmonizationWithV263());
        hl7OBXType.setPerformingOrganizationName(buildHL7XONType(or.getPerformingOrganizationName()));
        hl7OBXType.setPerformingOrganizationAddress(buildHL7XADType(or.getPerformingOrganizationAddress()));
        hl7OBXType.setPerformingOrganizationMedicalDirector(buildHL7XCNType(or.getPerformingOrganizationMedicalDirector()));

        return hl7OBXType;
    }

    private HL7SPMType buildHL7SPMType(gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen s) {
        HL7SPMType hl7SPMType = new HL7SPMType();
        
        hl7SPMType.setSetIDSPM(buildHL7SIType(s.getSetIdSpm()));
        hl7SPMType.setSpecimenID(buildHL7EIPType(s.getSpecimenId()));
        
        for(Eip eip : s.getSpecimenParentId()) {
            hl7SPMType.getSpecimenParentIDs().add(buildHL7EIPType(eip));
        }
        
        hl7SPMType.setSpecimenType(buildHL7CWEType(s.getSpecimenType()));
        
        for(Cwe cwe : s.getSpecimenTypeModifier()) {
            hl7SPMType.getSpecimenTypeModifier().add(buildHL7CWEType(cwe));
        }
        
        for(Cwe cwe : s.getSpecimenAdditives()) {
            hl7SPMType.getSpecimenAdditives().add(buildHL7CWEType(cwe));
        }
        
        hl7SPMType.setSpecimenCollectionMethod(buildHL7CWEType(s.getSpecimenCollectionMethod()));
        hl7SPMType.setSpecimenSourceSite(buildHL7CWEType(s.getSpecimenSourceSite()));
        
        for(Cwe cwe : s.getSpecimenSourceSiteModifier()) {
            hl7SPMType.getSpecimenSourceSiteModifier().add(buildHL7CWEType(cwe));
        }
        
        hl7SPMType.setSpecimenCollectionSite(buildHL7CWEType(s.getSpecimenCollectionSite()));
        
        for(Cwe cwe : s.getSpecimenRole()) {
            hl7SPMType.getSpecimenRole().add(buildHL7CWEType(cwe));
        }
        
        hl7SPMType.setSpecimenCollectionAmount(buildHL7CQType(s.getSpecimenCollectionAmount()));
        hl7SPMType.setGroupedSpecimenCount(buildHl7NMType(s.getGroupedSpecimenCount()));
        
        for(String str : s.getSpecimenDescription()) {
            hl7SPMType.getSpecimenDescription().add(str);
        }
        
        for(Cwe cwe : s.getSpecimenHandlingCode()) {
            hl7SPMType.getSpecimenHandlingCode().add(buildHL7CWEType(cwe));
        }

        for(Cwe cwe : s.getSpecimenRiskCode()) {
            hl7SPMType.getSpecimenRiskCode().add(buildHL7CWEType(cwe));
        }
        
        hl7SPMType.setSpecimenCollectionDateTime(buildHL7DRType(s.getSpecimenCollectionDateTime()));
        hl7SPMType.setSpecimenReceivedDateTime(buildHL7TSType(s.getSpecimenReceivedDateTime()));
        hl7SPMType.setSpecimenExpirationDateTime(buildHL7TSType(s.getSpecimenExpirationDateTime()));
        hl7SPMType.setSpecimenAvailability(s.getSpecimenAvailability());

        for(Cwe cwe : s.getSpecimenRejectReason()) {
            hl7SPMType.getSpecimenRejectReason().add(buildHL7CWEType(cwe));
        }

        hl7SPMType.setSpecimenQuality(buildHL7CWEType(s.getSpecimenQuality()));
        hl7SPMType.setSpecimenAppropriateness(buildHL7CWEType(s.getSpecimenAppropriateness()));

        for(Cwe cwe : s.getSpecimenCondition())
        {
            hl7SPMType.getSpecimenCondition().add(buildHL7CWEType(cwe));
        }

        hl7SPMType.setSpecimenCurrentQuantity(buildHL7CQType(s.getSpecimenCurrentQuantity()));
        hl7SPMType.setNumberOfSpecimenContainers(buildHl7NMType(s.getNumberOfSpecimenContainers()));
        hl7SPMType.setContainerType(buildHL7CWEType(s.getContainerType()));
        hl7SPMType.setContainerCondition(buildHL7CWEType(s.getContainerCondition()));
        hl7SPMType.setSpecimenChildRole(buildHL7CWEType(s.getSpecimenChildRole()));

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
        hl7FT1Type.setTransactionType(ft.getTransactionType());
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

        for(Ce ce : ft.getDiagnosisCode()) {
            hl7FT1Type.getDiagnosisCodeFT1().add(buildHL7CEType(ce));
        }

        for(Xcn xcn : ft.getPerformedByCode()) {
            hl7FT1Type.getPerformedByCode().add(buildHL7XCNType(xcn));
        }

        for(Xcn xcn : ft.getOrderedByCode()) {
            hl7FT1Type.getOrderedByCode().add(buildHL7XCNType(xcn));
        }

        hl7FT1Type.setProcedureCode(buildHL7CEType(ft.getProcedureCode()));

        for(Ce ce : ft.getProcedureCodeModifier()) {
            hl7FT1Type.getProcedureCodeModifier().add(buildHL7CEType(ce));
        }

        hl7FT1Type.setAdvancedBeneficiaryNoticeCode(buildHL7CEType(ft.getAdvancedBeneficiaryNoticeCode()));

        hl7FT1Type.setMedicallyNecessaryDuplicateProcedureReason(buildHL7CWEType(ft.getMedicallyNecessaryDuplicateProcedureReason()));
        hl7FT1Type.setNDCCode(buildHL7CNEType(ft.getNdcCode()));
        hl7FT1Type.setPaymentReferenceID(buildCXType(ft.getPaymentReferenceId()));

        for(String s : ft.getTransactionReferenceKey()) {
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

        for(String s : pva.getPatientValuables()) {
            hl7PIV2Type.getPatientValuables().add(s);
        }

        hl7PIV2Type.setPatientValuablesLocation(pva.getPatientValuablesLocation());

        for(String s : pva.getVisitUserCode()) {
            hl7PIV2Type.getVisitUserCode().add(s);
        }

        hl7PIV2Type.setExpectedAdmitDateTime(buildHL7TSType(pva.getExpectedAdmitDateTime()));
        hl7PIV2Type.setExpectedDischargeDateTime(buildHL7TSType(pva.getExpectedDischargeDateTime()));
        hl7PIV2Type.setEstimatedLengthOfInpatientStay(buildHl7NMType(pva.getEstimateLengthOfInpatientDay()));
        hl7PIV2Type.setActualLengthOfInpatientStay(buildHl7NMType(pva.getActualLengthOfInpatientDay()));
        hl7PIV2Type.getVisitDescription().add(pva.getVisitDescription());

        for(Xcn xcn : pva.getReferralSourceCode()) {
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

        for(Xon xon : pva.getClinicOrganizationName()) {
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

        for(Ce ce : pva.getRecreationalDrugUseCode()) {
            hl7PIV2Type.setRecreationalDrugUseCode(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setAdmissionLevelOfCareCode(buildHL7CWEType(pva.getAdmissionLevelOfCareCode()));

        for(Ce ce : pva.getPrecautionCode()) {
            hl7PIV2Type.setPrecautionCode(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setPatientConditionCode(buildHL7CWEType(pva.getPatientConditionCode()));
        hl7PIV2Type.setLivingWillCode(pva.getLivingWillCode());
        hl7PIV2Type.setOrganDonorCode(pva.getOrganDonorCode());

        for(Ce ce : pva.getAdvanceDirectiveCode()) {
            hl7PIV2Type.getAdvanceDirectiveCode().add(buildHL7CWEType(ce));
        }

        hl7PIV2Type.setPatientStatusEffectiveDate(buildHL7DTType(pva.getPatientStatusEffectiveDate()));
        hl7PIV2Type.setExpectedLOAReturnDateTime(buildHL7TSType(pva.getExpectedLoaReturnDateTime()));
        hl7PIV2Type.setExpectedPreadmissionTestingDateTime(buildHL7TSType(pva.getExpectedPreAdmissionTestingDateTime()));

        for(String s : pva.getNotifyClergyCode()) {
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
        hl7PIV1Type.setPatientClass(pv.getPatientClass());
        hl7PIV1Type.setAssignedPatientLocation(buildHL7PLType(pv.getAssignPatientLocation()));
        hl7PIV1Type.setAdmissionType(pv.getAdmissionType());
        hl7PIV1Type.setPreadmitNumber(buildCXType(pv.getPreadmitNumber()));
        hl7PIV1Type.setPriorPatientLocation(buildHL7PLType(pv.getPriorPatientLocation()));

        for(Xcn xcn : pv.getAttendingDoctor()) {
            hl7PIV1Type.getAttendingDoctor().add(buildHL7XCNType(xcn));
        }

        for(Xcn xcn : pv.getReferringDoctor()) {
            hl7PIV1Type.getReferringDoctor().add(buildHL7XCNType(xcn));
        }

        for(Xcn xcn : pv.getConsultingDoctor()) {
            hl7PIV1Type.setConsultingDoctor(buildHL7XCNType(xcn));
        }

        hl7PIV1Type.setHospitalService(pv.getHospitalService());
        hl7PIV1Type.setTemporaryLocation(buildHL7PLType(pv.getTemporaryLocation()));
        hl7PIV1Type.setPreadmitTestIndicator(pv.getPreadmitTestIndicator());
        hl7PIV1Type.setReAdmissionIndicator(pv.getReAdmissionIndicator());
        hl7PIV1Type.setAdmissionType(pv.getAdmitSource());

        for(String s : pv.getAmbulatoryStatus()) {
            hl7PIV1Type.getAmbulatoryStatus().add(s);
        }

        hl7PIV1Type.setVisitIndicator(pv.getVisitIndicator());

        for(Xcn xcn : pv.getAdmittingDoctor()) {
            hl7PIV1Type.getAttendingDoctor().add(buildHL7XCNType(xcn));
        }

        hl7PIV1Type.setPatientType(pv.getPatientType());
        hl7PIV1Type.setVisitNumber(buildHL7CXType(pv.getVisitNumber()));

        for(Fc fc : pv.getFinancialClass()) {
            hl7PIV1Type.getFinancialClass().add(buildHL7FCType(fc));
        }

        hl7PIV1Type.setChargePriceIndicator(pv.getChargePriceIndicator());
        hl7PIV1Type.setCourtesyCode(pv.getCourtesyCode());
        hl7PIV1Type.setCreditRating(pv.getCreditRating());

        for(String s : pv.getContractRole()) {
            hl7PIV1Type.getContractCode().add(s);
        }

        for(String s : pv.getContractEffectiveDate()) {
            hl7PIV1Type.getContractEffectiveDate().add(buildHL7DTType(s));
        }

        for(String s : pv.getContractAmount()) {
            hl7PIV1Type.getContractAmount().add(buildHl7NMType(s));
        }

        for(String s : pv.getContractPeriod()) {
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

        for(Ts ts : pv.getDischargeDateTime()) {
            hl7PIV1Type.getDischargeDateTime().add(buildHL7TSType(ts));
        }

        hl7PIV1Type.setCurrentPatientBalance(buildHl7NMType(pv.getCurrentPatientBalance()));
        hl7PIV1Type.setTotalCharges(buildHl7NMType(pv.getTotalCharge()));
        hl7PIV1Type.setTotalAdjustments(buildHl7NMType(pv.getTotalAdjustment()));
        hl7PIV1Type.setTotalPayments(buildHl7NMType(pv.getTotalPayment()));
        hl7PIV1Type.setAlternateVisitID(buildCXType(pv.getAlternateVisitId()));
        hl7PIV1Type.setVisitIndicator(pv.getVisitIndicator());

        for(Xcn xcn : pv.getOtherHealthcareProvider()) {
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

    // -------------------------- ********* --------------

    private HL7DLDType buildHL7DLDType(Dld dld) {
        HL7DLDType hl7DLDType = new HL7DLDType();

        hl7DLDType.setDischargeLocation(dld.getDischargeLocation());
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

        hl7NK1Type.setContactRole(buildHL7CWEType(nok.getContactRole()));
        hl7NK1Type.setStartDate(buildHL7DTType(nok.getStartDate()));
        hl7NK1Type.setEndDate(buildHL7DTType(nok.getEndDate()));
        hl7NK1Type.getNextOfKinAssociatedPartiesJobTitle().add(nok.getNextOfKinAssociatedPartiesJobTitle());
        hl7NK1Type.setNextOfKinAssociatedPartiesJobCodeClass(buildHL7JCCType(nok.getNextOfKinAssociatedPartiesJobCode()));
        hl7NK1Type.setNextOfKinAssociatedPartiesEmployeeNumber(buildHL7CXType(nok.getNextOfKinAssociatedPartiesEmployee()));

        for (Xon xon : nok.getOrganizationNameNk1()) {
            hl7NK1Type.getOrganizationNameNK1().add(buildHL7XONType(xon));
        }

        hl7NK1Type.getMaritalStatus().add(buildHL7CWEType(nok.getMartialStatus()));
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

        hl7NK1Type.setPrimaryLanguage(buildHL7CWEType(nok.getPrimaryLanguage()));
        hl7NK1Type.setLivingArrangement(nok.getLivingArrangement());
        hl7NK1Type.setPublicityCode(buildHL7CWEType(nok.getPublicityCode()));
        hl7NK1Type.setProtectionIndicator(nok.getProtectionIndicator());
        hl7NK1Type.setStudentIndicator(nok.getStudentIndicator());
        hl7NK1Type.setReligion(buildHL7CWEType(nok.getReligion()));

        for(Xpn xpn : nok.getMotherMaidenName()) {
            hl7NK1Type.getMothersMaidenName().add(buildHL7XPNType(xpn));
        }

        hl7NK1Type.setNationality(buildHL7CWEType(nok.getNationality()));

        for(Ce ce : nok.getEthnicGroup()) {
            hl7NK1Type.getEthnicGroup().add(buildHL7CWEType(ce));
        }

        for(Ce ce : nok.getContactReason()) {
            hl7NK1Type.getContactReason().add(buildHL7CWEType(ce));
        }

        for(Xpn xpn : nok.getContactPersonName()) {
            hl7NK1Type.getContactPersonsName().add(buildHL7XPNType(xpn));
        }

        for(Xpn xpn : nok.getContactPersonName()) {
            hl7NK1Type.getContactPersonsName().add(buildHL7XPNType(xpn));
        }

        for(Xad xad : nok.getContactPersonAddress()) {
            hl7NK1Type.getContactPersonsAddress().add(buildHL7XADType(xad));
        }

        for(Cx cx : nok.getNextOfKinAssociatedPartyIdentifier()) {
            hl7NK1Type.getNextOfKinAssociatedPartysIdentifiers().add(buildHL7CXType(cx));
        }


        hl7NK1Type.setJobStatus(nok.getJobStatus());

        for(Ce ce : nok.getRace()) {
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

    private HL7CXType buildHL7CXType(Cx cx) {
        HL7CXType hl7CXType = new HL7CXType();

        hl7CXType.setHL7IDNumber(cx.getIdNumber());
        hl7CXType.setHL7CheckDigit(cx.getCheckDigit());
        hl7CXType.setHL7CheckDigitScheme(cx.getCheckDigitScheme());
        hl7CXType.setHL7AssigningAuthority(buildHL7HDType(cx.getAssignAuthority()));
        hl7CXType.setHL7IdentifierTypeCode(cx.getIdentifierTypeCode());
        hl7CXType.setHL7AssigningFacility(buildHL7HDType(cx.getAssignFacility()));
        hl7CXType.setHL7EffectiveDate(buildHL7DTType(cx.getExpirationDate()));
        hl7CXType.setHL7ExpirationDate(buildHL7DTType(cx.getExpirationDate()));
        hl7CXType.setHL7AssigningJurisdiction(buildHL7CWEType(cx.getAssignJurisdiction()));
        hl7CXType.setHL7AssigningAgencyOrDepartment(buildHL7CWEType(cx.getAssignAgentOrDept()));

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

        hl7PID1Type.setStudentIndicator(pad.getStudentIndicator());
        hl7PID1Type.setHandicap(pad.getHandiCap());
        hl7PID1Type.setLivingWillCode(pad.getLivingWillCode());
        hl7PID1Type.setOrganDonorCode(pad.getOrganDonorCode());
        hl7PID1Type.setSeparateBill(pad.getSeparateBill());

        for(Cx cx : pad.getDuplicatePatient()) {
            hl7PID1Type.getDuplicatePatient().add(buildHL7CXType(cx));
        }

        hl7PID1Type.setPublicityCode(buildHL7CEType(pad.getPublicityCode()));
        hl7PID1Type.setProtectionIndicator(pad.getProtectionIndicator());
        hl7PID1Type.setProtectionIndicatorEffectiveDate(buildHL7DTType(pad.getProtectionIndicatorEffectiveDate()));

        for(Xon xon : pad.getPlaceOfWorship()) {
            hl7PID1Type.getPlaceofWorship().add(buildHL7XONType(xon));
        }

        for(Ce ce : pad.getAdvanceDirectiveCode())
        {
            hl7PID1Type.getAdvanceDirectiveCode().add(buildHL7CEType(ce));
        }

        hl7PID1Type.setImmunizationRegistryStatus(pad.getImmunizationRegistryStatus());
        hl7PID1Type.setImmunizationRegistryStatusEffectiveDate(buildHL7DTType(pad.getImmunizationRegistryStatusEffectiveDate()));
        hl7PID1Type.setPublicityCodeEffectiveDate(buildHL7DTType(pad.getPublicityCodeEffectiveDate()));
        hl7PID1Type.setMilitaryBranch(pad.getMilitaryBranch());
        hl7PID1Type.setMilitaryRankGrade(pad.getMilitaryRank());
        hl7PID1Type.setMilitaryStatus(pad.getMilitaryStatus());

        return hl7PID1Type;
    }

    private HL7PIDType buildHL7PIDType(PatientIdentification pi) {
        HL7PIDType hl7PIDType = new HL7PIDType();

        HL7SIType hl7SIType = new HL7SIType();
        hl7SIType.setHL7SequenceID(pi.getSetPid());

        hl7PIDType.getSetIDPID().add(hl7SIType);
        hl7PIDType.setPatientID(buildCXType(pi.getPatientId()));

        for(Cx cx : pi.getPatientIdentifierList()) {
            hl7PIDType.getPatientIdentifierList().add(buildCXType(cx));
        }

        for(Cx api : pi.getAlternativePatientId()) {
            hl7PIDType.getAlternatePatientIDPID().add(buildCXType(api));
        }

        for(Xpn xpn : pi.getPatientName()) {
            hl7PIDType.getPatientName().add(buildHL7XPNType(xpn));
        }

        for(Xpn xpn : pi.getMotherMaidenName()) {
            hl7PIDType.getMothersMaidenName().add(buildHL7XPNType(xpn));
        }

        hl7PIDType.setDateTimeOfBirth(buildHL7TSType(pi.getDateTimeOfBirth()));
        hl7PIDType.setAdministrativeSex(pi.getAdministrativeSex());

        for(Xpn xpn : pi.getPatientAlias()) {
            hl7PIDType.setPatientAlias(buildHL7XPNType(xpn));
        }

        for(Ce ce : pi.getRace()) {
            hl7PIDType.getRace().add(buildHL7CWEType(ce));
        }

        for(Xad xad : pi.getPatientAddress()) {
            hl7PIDType.getPatientAddress().add(buildHL7XADType(xad));
        }

        hl7PIDType.setCountyCode(pi.getCountyCode());

        for(Xtn xtn : pi.getPhoneNumberHome()) {
            hl7PIDType.getPhoneNumberHome().add(buildHL7XTNType(xtn));
        }

        for(Xtn xtn : pi.getPhoneNumberBusiness()) {
            hl7PIDType.getPhoneNumberBusiness().add(buildHL7XTNType(xtn));
        }

        hl7PIDType.getPrimaryLanguage().add(buildHL7CEType(pi.getPrimaryLanguage()));
        hl7PIDType.setMaritalStatus(buildHL7CEType(pi.getMartialStatus()));
        hl7PIDType.setReligion(buildHL7CEType(pi.getReligion()));
        hl7PIDType.setPatientAccountNumber(buildCXType(pi.getPatientAccountNumber()));
        hl7PIDType.setSSNNumberPatient(pi.getSsnNumberPatient());
        hl7PIDType.setDriversLicenseNumberPatient(buildHL7DLNType(pi.getDriverLicenseNumberPatient()));

        for(Cx cx : pi.getMotherIdentifier()) {
            hl7PIDType.getMothersIdentifier().add(buildCXType(cx));
        }

        for(Ce ce : pi.getEthnicGroup()) {
            hl7PIDType.getEthnicGroup().add(buildHL7CWEType(ce));
        }

        hl7PIDType.setBirthPlace(pi.getBirthPlace());
        hl7PIDType.setMultipleBirthIndicator(pi.getMultipleBirthIndicator());
        hl7PIDType.setBirthOrder(buildHl7NMType(pi.getBirthOrder()));

        for(Ce ce : pi.getCitizenship()) {
            hl7PIDType.getCitizenship().add(buildHL7CWEType(ce));
        }

        hl7PIDType.setVeteransMilitaryStatus(buildHL7CWEType(pi.getVeteranStatus()));
        hl7PIDType.setNationality(buildHL7CWEType(pi.getNationality()));
        hl7PIDType.setPatientDeathDateAndTime(buildHL7TSType(pi.getPatientDeathDateAndTime()));
        hl7PIDType.setPatientDeathIndicator(pi.getPatientDeathIndicator());
        hl7PIDType.setIdentityUnknownIndicator(pi.getIdentityUnknownIndicator());

        for(String s : pi.getIdentityReliabilityCode()) {
            hl7PIDType.getIdentityReliabilityCode().add(s);
        }

        hl7PIDType.setLastUpdateDateTime(buildHL7TSType(pi.getLastUpdateDateTime()));
        hl7PIDType.setLastUpdateFacility(buildHL7HDType(pi.getLastUpdateFacility()));
        hl7PIDType.setSpeciesCode(buildHL7CEType(pi.getSpeciesCode()));
        hl7PIDType.setBreedCode(buildHL7CEType(pi.getBreedCode()));
        hl7PIDType.setStrain(pi.getStrain());
        hl7PIDType.getProductionClassCode().add(buildHL7CEType(pi.getProductionClassCode()));

        for(Cwe cwe : pi.getTribalCitizenship()) {
            hl7PIDType.getTribalCitizenship().add(buildHL7CWEType(cwe));
        }

        return hl7PIDType;
    }

    private HL7ORCType buildHL7ORCType(CommonOrder commonOrder) {
        HL7ORCType hl7ORCType = new HL7ORCType();

        hl7ORCType.setOrderControl(commonOrder.getOrderControl());
        hl7ORCType.setPlacerOrderNumber(buildHL7EIType(commonOrder.getPlacerOrderNumber()));
        hl7ORCType.setFillerOrderNumber(buildHL7EIType(commonOrder.getFillerOrderNumber()));
        hl7ORCType.setPlacerGroupNumber(buildHL7EIType(commonOrder.getPlacerGroupNumber()));
        hl7ORCType.setOrderStatus(commonOrder.getOrderStatus());
        hl7ORCType.setResponseFlag(commonOrder.getResponseFlag());

        for(Tq tq : commonOrder.getQuantityTiming()) {
            hl7ORCType.getQuantityTiming().add(buildHL7TQType(tq));
        }

        hl7ORCType.setParent(buildHL7EIPType(commonOrder.getParentOrder()));
        hl7ORCType.setDateTimeOfTransaction(buildHL7TSType(commonOrder.getDateTimeOfTransaction()));

        for(Xcn xcn : commonOrder.getEnteredBy()) {
            hl7ORCType.getEnteredBy().add(buildHL7XCNType(xcn));
        }

        for(Xcn xcn : commonOrder.getVerifiedBy()) {
            hl7ORCType.getVerifiedBy().add(buildHL7XCNType(xcn));
        }

        for(Xcn xcn : commonOrder.getOrderingProvider()) {
            hl7ORCType.getOrderingProvider().add(buildHL7XCNType(xcn));
        }

        hl7ORCType.setEnterersLocation(buildHL7PLType(commonOrder.getEntererLocation()));

        for(Xtn xtn : commonOrder.getCallBackPhoneNumber()) {
            hl7ORCType.getCallBackPhoneNumber().add(buildHL7XTNType(xtn));
        }

        hl7ORCType.setOrderEffectiveDateTime(buildHL7TSType(commonOrder.getOrderEffectiveDateTime()));
        hl7ORCType.setOrderControlCodeReason(buildHL7CWEType(commonOrder.getOrderControlCodeReason()));
        hl7ORCType.setEnteringOrganization(buildHL7CWEType(commonOrder.getEnteringOrganization()));
        hl7ORCType.setEnteringDevice(buildHL7CWEType(commonOrder.getEnteringDevice()));

        for(Xcn xcn : commonOrder.getActionBy()) {
            hl7ORCType.getActionBy().add(buildHL7XCNType(xcn));
        }

        hl7ORCType.setAdvancedBeneficiaryNoticeCode(buildHL7CWEType(commonOrder.getAdvancedBeneficiaryNoticeCode()));

        for(Xon xon : commonOrder.getOrderingFacilityName()) {
            hl7ORCType.getOrderingFacilityName().add(buildHL7XONType(xon));
        }

        for(Xad xad : commonOrder.getOrderingFacilityAddress()) {
            hl7ORCType.getOrderingFacilityAddress().add(buildHL7XADType(xad));
        }

        for(Xtn xtn : commonOrder.getOrderingFacilityPhoneNumber()) {
            hl7ORCType.getOrderingFacilityPhoneNumber().add(buildHL7XTNType(xtn));
        }

        for(Xad xad : commonOrder.getOrderingProviderAddress()) {
            hl7ORCType.getOrderingProviderAddress().add(buildHL7XADType(xad));
        }

        hl7ORCType.setOrderStatusModifier(buildHL7CWEType(commonOrder.getOrderStatusModifier()));
        hl7ORCType.setAdvancedBeneficiaryNoticeOverrideReason(buildHL7CWEType(commonOrder.getAdvancedBeneficiaryNoticeOverrideReason()));
        hl7ORCType.setFillersExpectedAvailabilityDateTime(buildHL7TSType(commonOrder.getFillerExpectedAvailabilityDateTime()));
        hl7ORCType.setConfidentialityCode(buildHL7CWEType(commonOrder.getConfidentialityCode()));
        hl7ORCType.setOrderType(buildHL7CWEType(commonOrder.getOrderType()));
        hl7ORCType.setEntererAuthorizationMode(buildHL7CNEType(commonOrder.getEntererAuthorizationMode()));
        hl7ORCType.setParentUniversalServiceIdentifier(buildHL7CWEType(commonOrder.getParentUniversalServiceIdentifier()));

        return hl7ORCType;
    }

    private HL7ContinuationPointerType buildHl7ContinuationPointerType(OruR1 oruR1) {
        HL7ContinuationPointerType hl7ContinuationPointerType = new HL7ContinuationPointerType();

        if(null == oruR1.getContinuationPointer()) {
            return hl7ContinuationPointerType;
        }

        hl7ContinuationPointerType.setContinuationPointer(oruR1.getContinuationPointer().getContinuationPointer());
        hl7ContinuationPointerType.setContinuationStyle(oruR1.getContinuationPointer().getContinuationStyle());

        return hl7ContinuationPointerType;
    }

    private HL7EIPType buildHL7EIPType(Eip eip) {
        HL7EIPType hl7EIPType = new HL7EIPType();

        hl7EIPType.setHL7PlacerAssignedIdentifier(buildHL7EIType(eip.getPlacerAssignedIdentifier()));
        hl7EIPType.setHL7FillerAssignedIdentifier(buildHL7EIType(eip.getFillerAssignedIdentifier()));

        return hl7EIPType;
    }

    private HL7DLNType buildHL7DLNType(Dln dln) {
        HL7DLNType hl7DLNType = new HL7DLNType();

        hl7DLNType.setHL7LicenseNumber(dln.getLicenseNumber());
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

        hl7OSDType.setHL7SequenceResultsFlag(osd.getSequenceResultFlag());
        hl7OSDType.setHL7PlacerOrderNumberEntityIdentifier(osd.getPlacerOrderNumberEntityIdentifier());
        hl7OSDType.setHL7FillerOrderNumberNamespaceID(osd.getPlacerOrderNumberNamespaceId());
        hl7OSDType.setHL7FillerOrderNumberEntityIdentifier(osd.getFillerOrderNumberEntityIdentifier());
        hl7OSDType.setHL7FillerOrderNumberNamespaceID(osd.getFillerOrderNumberNamespaceId());
        hl7OSDType.setHL7SequenceConditionValue(osd.getSequenceConditionValue());
        hl7OSDType.setHL7MaximumNumberOfRepeats(buildHl7NMType(osd.getMaximumNumberOfRepeats()));
        hl7OSDType.setHL7PlacerOrderNumberNamespaceID(osd.getPlacerOrderNumberUniversalId());
        hl7OSDType.setHL7PlacerOrderNumberUniversalIDType(osd.getPlacerOrderNumberUniversalIdType());
        hl7OSDType.setHL7FillerOrderNumberUniversalID(osd.getFillerOrderNumberUniversalId());
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
        hl7XADType.setHL7OtherGeographicDesignation(xad.getOtherGeographic());
        hl7XADType.setHL7CountyParishCode(xad.getCountyCode());
        hl7XADType.setHL7CensusTract(xad.getCensusTract());
        hl7XADType.setHL7AddressRepresentationCode(xad.getAddressRepresentationCode());
        hl7XADType.setHL7AddressValidityRange(buildHL7DRType(xad.getAddressValidityRange()));
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
        hl7XPNType.setHL7NameContext(buildHL7CEType(xpn.getNameContext()));
        hl7XPNType.setHL7NameValidityRange(buildHL7DRType(xpn.getNameValidityRange()));
        hl7XPNType.setHL7NameAssemblyOrder(xpn.getNameAssemblyOrder());
        hl7XPNType.setHL7EffectiveDate(buildHL7TSType(xpn.getEffectiveDate()));
        hl7XPNType.setHL7ExpirationDate(buildHL7TSType(xpn.getExpirationDate()));
        hl7XPNType.setHL7ProfessionalSuffix(xpn.getProfessionalSuffix());

        return hl7XPNType;
    }

    private HL7DRType buildHL7DRType(Dr dr) {
        HL7DRType hl7DRType = new HL7DRType();
        hl7DRType.setHL7RangeStartDateTime(buildHL7TSType(dr.getRangeStartDateTime()));
        hl7DRType.setHL7RangeEndDateTime(buildHL7TSType(dr.getRangeEndDateTime()));
        return hl7DRType;
    }

    private HL7FNType buildHl7FNType(Fn fn) {
        HL7FNType hl7FNType = new HL7FNType();

        hl7FNType.setHL7Surname(fn.getSurname());
        hl7FNType.setHL7OwnSurnamePrefix(fn.getOwnSurnamePrefix());
        hl7FNType.setHL7OwnSurname(fn.getOwnSurname());
        hl7FNType.setHL7SurnamePrefixFromPartnerSpouse(fn.getSurnamePrefixFromPartner());
        hl7FNType.setHL7SurnameFromPartnerSpouse(fn.getSurnameFromPartner());

        return hl7FNType;
    }

    private HL7CXType buildCXType(Cx cx) {
        HL7CXType cxType = new HL7CXType();

        cxType.setHL7IDNumber(cx.getIdNumber());
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

    private List<HL7SoftwareSegmentType> buildSoftwareSegments(OruR1 oruR1) {
        ArrayList<HL7SoftwareSegmentType> destList = new ArrayList<>();

        if(null == oruR1.getSoftwareSegment()) {
            return destList;
        }

        for(SoftwareSegment ss : oruR1.getSoftwareSegment()) {
            HL7SoftwareSegmentType segType = new HL7SoftwareSegmentType();
            segType.setSoftwareBinaryID(ss.getSoftwareBinaryId());
            //@todo segType.setSoftwareInstallDate();
            //@todo segType.setSoftwareProductInformation();
            segType.setSoftwareProductName(ss.getSoftwareProductName());
            //@todo segType.setSoftwareVendorOrganization();
            segType.setSoftwareCertifiedVersionOrReleaseNumber(ss.getSoftwareCertifiedVersionOrReleaseNumber());

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
        mshType.setReceivingApplication(buildHL7HDType(mh.getReceivingFacility()));
        mshType.setReceivingFacility(buildHL7HDType(mh.getReceivingFacility()));
        mshType.setDateTimeOfMessage(buildHL7TSType(mh.getDateTimeOfMessage()));
        mshType.setSecurity("");
        mshType.setMessageType(buildHl7MsgType());
        mshType.setMessageControlID(mh.getMessageControlId());
        mshType.setProcessingID(buildHl7PtType(mh.getProcessingId()));
        mshType.setVersionID(buildHl7VidType(mh.getVersionId()));
        mshType.setSequenceNumber(null);
        if(null != mh.getSequenceNumber()) {
            mshType.setSequenceNumber(buildHl7NMType(mh.getSequenceNumber()));
        }

        if(null != mh.getCharacterSet()) {
            mshType.getCharacterSet().addAll(mh.getCharacterSet());
        }

        mshType.setContinuationPointer(mh.getContinuationPointer());
        mshType.setAcceptAcknowledgmentType(mh.getAcceptAckType());
        mshType.setCountryCode(mh.getCountryCode());
        mshType.setAlternateCharacterSetHandlingScheme(mh.getAlternateCharacterSetHandlingScheme());

        if(null != mh.getMessageProfileIdentifier()) {
            mshType.getMessageProfileIdentifier().addAll(buildMessageProfileIdentifiers(mh.getMessageProfileIdentifier()));
        }

        return mshType;
    }

    private List<HL7EIType> buildMessageProfileIdentifiers(List<Ei> srcItems) {
        ArrayList<HL7EIType> destItems = new ArrayList<>();

        for(Ei ei : srcItems) {
            destItems.add(buildHL7EIType(ei));
        }

        return destItems;
    }

    private HL7EIType buildHL7EIType(Ei ei) {
        HL7EIType hl7EIType = new HL7EIType();

        hl7EIType.setHL7EntityIdentifier(ei.getEntityIdentifier());
        hl7EIType.setHL7NamespaceID(ei.getNameSpaceId());
        hl7EIType.setHL7UniversalID(ei.getUniversalId());
        hl7EIType.setHL7UniversalIDType(ei.getUniversalIdType());

        return hl7EIType;
    }

    private HL7NMType buildHl7NMType(String seqNumber) {
        HL7NMType hl7NMType = new HL7NMType();

        try {
            hl7NMType.setHL7Numeric(Float.parseFloat(seqNumber));
        }
        catch(Exception e) {
            // ignore, sequence number not present in the samples
        }

        return hl7NMType;
    }

    public HL7VIDType buildHl7VidType(Vid vid) {
        HL7VIDType hl7VIDType = new HL7VIDType();

        hl7VIDType.setHL7VersionID(vid.getVersionId());

        if(null != vid.getInternationalizationCode()) {
            hl7VIDType.setHL7InternationalizationCode(buildHL7CEType(vid.getInternationalizationCode()));
        }

        if(null != vid.getInternationalVersionId()) {
            hl7VIDType.setHL7InternationalVersionID(buildHL7CEType(vid.getInternationalVersionId()));
        }

        return hl7VIDType;
    }

    private HL7CEType buildHL7CEType(Ce ce) {
        HL7CEType hl7CEType = new HL7CEType();

        hl7CEType.setHL7AlternateIdentifier(ce.getAlternateIdentifier());
        hl7CEType.setHL7AlternateText(ce.getAlternateText());
        hl7CEType.setHL7Identifier(ce.getIdentifier());
        hl7CEType.setHL7NameofCodingSystem(ce.getNameOfCodingSystem());
        hl7CEType.setHL7Text(ce.getText());
        hl7CEType.setHL7NameofAlternateCodingSystem(ce.getNameOfAlternateCodingSystem());

        return hl7CEType;
    }

    private HL7PTType buildHl7PtType(Pt pt)
    {
        HL7PTType hl7PTType = new HL7PTType();
        hl7PTType.setHL7ProcessingID(pt.getProcessingId());

        hl7PTType.setHL7ProcessingMode("");
        if(null != pt.getProcessingMode()) {
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
       return buildHL7TSType(ts.time);
    }

    private HL7TSType buildHL7TSType(String ts) {
        HL7TSType hl7TSType = new HL7TSType();

        if(null != ts) {
            if(ts.length() <= 8) {
                ts = ts + "0000";
            }

            LocalDateTime localDateTime = LocalDateTime.parse(ts, formatter);
            hl7TSType.setYear(BigInteger.valueOf(localDateTime.getYear()));
            hl7TSType.setMonth(BigInteger.valueOf(localDateTime.getMonthValue()));
            hl7TSType.setDay(BigInteger.valueOf(localDateTime.getDayOfMonth()));
            hl7TSType.setHours(BigInteger.valueOf(localDateTime.getHour()));
            hl7TSType.setMinutes(BigInteger.valueOf(localDateTime.getMinute()));
            hl7TSType.setSeconds(BigInteger.valueOf(localDateTime.getSecond()));
            hl7TSType.setGmtOffset("");
        }

        return hl7TSType;
    }

    private HL7HDType buildHL7HDType(Hd hdType) {
        HL7HDType hl7HDType = new HL7HDType();

        if(null != hdType) {
            hl7HDType.setHL7UniversalID(hdType.getUniversalId());
            hl7HDType.setHL7UniversalIDType(hdType.getUniversalIdType());
            hl7HDType.setHL7NamespaceID(hdType.getNameSpaceId());
        }

        return hl7HDType;
    }

    private HL7CWEType buildHL7CWEType(Ce ce) {
        HL7CWEType hl7CWEType = new HL7CWEType();

        hl7CWEType.setHL7Identifier(ce.getIdentifier());
        hl7CWEType.setHL7Text(ce.getText());
        hl7CWEType.setHL7NameofCodingSystem(ce.getNameOfCodingSystem());
        hl7CWEType.setHL7AlternateIdentifier(ce.getAlternateIdentifier());
        hl7CWEType.setHL7AlternateText(ce.getAlternateText());

        return hl7CWEType;
    }

    private HL7DTType buildHL7DTType(String date) {
        HL7DTType hl7DTType = new HL7DTType();

        hl7DTType.setDay(BigInteger.valueOf(1));
        hl7DTType.setMonth(BigInteger.valueOf(5));
        hl7DTType.setYear(BigInteger.valueOf(2023));

        return hl7DTType;
    }
}

