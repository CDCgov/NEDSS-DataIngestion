package gov.cdc.dataingestion.hl7.helper.helper;

import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;

import java.util.ArrayList;
import java.util.List;

public class Mapping231To251Helper {

    //region Map Message Header - 231 to 251
    public static MessageHeader MapMsh(MSH inMsh231, MessageHeader outMsh251) {
        outMsh251.getMessageType().setMessageCode("ORU");
        outMsh251.getMessageType().setTriggerEvent("R01");
        outMsh251.getMessageType().setMessageStructure("ORU_R01");
        outMsh251.getVersionId().setVersionId("2.5.1");

        Ei messageProfileIdentifier = new Ei();
        messageProfileIdentifier.setEntityIdentifier("PHLabReport-NoAck");
        messageProfileIdentifier.setNameSpaceId("ELR_Receiver");
        messageProfileIdentifier.setUniversalId("2.16.840.1.113883.9.11");
        messageProfileIdentifier.setUniversalIdType("ISO");

        if(outMsh251.getMessageProfileIdentifier() != null) {

            if (outMsh251.getMessageProfileIdentifier().size() > 0) {
                outMsh251.getMessageProfileIdentifier().set(0, messageProfileIdentifier);
            } else {
                outMsh251.getMessageProfileIdentifier().add(messageProfileIdentifier);
            }
        } else {
            List<Ei> eis = new ArrayList<>();
            eis.add(messageProfileIdentifier);
            outMsh251.setMessageProfileIdentifier(eis);
        }
        return outMsh251;
    }
    //endregion

    //region Map Software Segments - 231 to 251
    public static List<SoftwareSegment> MapSoftwareSegment(List<SoftwareSegment> softwareSegment) {
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

        if (softwareSegment != null) {
            if (softwareSegment.isEmpty()) {
                softwareSegment.add(sft);
            } else {
                softwareSegment.set(0, sft);
            }
        } else {
            softwareSegment = new ArrayList<SoftwareSegment>();
            softwareSegment.add(sft);
        }
        return softwareSegment;
    }
    //endregion

    //region Map Patient Identification - 231 to 251
    public static PatientIdentification MapPid(PID inPid231, PatientIdentification outPid251) {
        outPid251.setSetPid("1");
        if (outPid251.getPatientIdentifierList() == null) {
            outPid251.setPatientIdentifierList(new ArrayList<Cx>());
        }

        if (outPid251.getPatientName() == null) {
            outPid251.setPatientName(new ArrayList<Xpn>());
        }

        // PatientIdentifierList
        for(int a = 0; a < outPid251.getPatientIdentifierList().size(); a++) {
            if (outPid251.getPatientIdentifierList().get(a) != null) {
                var patientIdentifier = outPid251.getPatientIdentifierList().get(a);
                if (patientIdentifier.getIdentifierTypeCode() == null || patientIdentifier.getIdentifierTypeCode().isEmpty()) {
                    patientIdentifier = MapCxWithNullToCx(patientIdentifier, patientIdentifier, "U");
                } else {
                    patientIdentifier = MapCxWithNullToCx(patientIdentifier, patientIdentifier, patientIdentifier.getIdentifierTypeCode());
                }
                outPid251.getPatientIdentifierList().set(a, patientIdentifier);
            }
        }

        // Patient ID
        if (outPid251.getPatientId() != null) {
            outPid251.getPatientIdentifierList().add(MapCxWithNullToCx(outPid251.getPatientId(), new Cx(), "PT"));
        }

        // Patient AlternatePatientId
        for(int b = 0; b < outPid251.getAlternativePatientId().size(); b++) {
            if (outPid251.getAlternativePatientId().get(b) != null) {
                outPid251.getPatientIdentifierList().add(MapCxWithNullToCx(outPid251.getAlternativePatientId().get(b), new Cx(), "APT"));
            }
        }

        // Patient Name - hapi
        // MotherMaidenName - hapi
        // DateTimeOfBirth - hapi
        // Patient Alias
        for(int c = 0; c < outPid251.getPatientAlias().size(); c++) {
            if (outPid251.getPatientAlias().get(c) != null) {
                outPid251.getPatientName().add(MapXpnToXpn(outPid251.getPatientAlias().get(c), new Xpn(), "A"));
            }
        }
        // Race - hapi
        // Patient Address - hapi
        // HomePhoneNumber - hapi
        // BusinessPhoneNumber - hapi
        // PrimaryLanguage - hapi
        // MartialStatus - hapi
        // Religion - hapi
        // PatientAccountNumber - hapi
        // SSN Number
        outPid251.getPatientIdentifierList().add(MapSocialSecurityToPatientIdentifier(outPid251.getSsnNumberPatient(), new Cx()));
        // Mother Identifier - hapi
        // Ethnic Group - hapi
        // Citizenship - hapi
        // Veteran - hapi
        // Nationality - hapi
        // MapSocialSecurityToPatientIdentifier - hapi
        // DriversLicenseNumber
        outPid251.getPatientIdentifierList().add(MapDlnToCx(outPid251.getDriverLicenseNumberPatient(), new Cx()));
        return outPid251;
    }
    //endregion

    public static Cx MapSocialSecurityToPatientIdentifier(String ssnNumber, Cx out) {
        out.setIdNumber(ssnNumber);
        Hd hd = new Hd();
        hd.setNameSpaceId("SSA");
        hd.setUniversalId("2.16.840.1.113883.4.1");
        hd.setUniversalIdType("ISO");
        out.setAssignAuthority(hd);
        out.setIdentifierTypeCode("SS");
        return out;
    }

    public static Cx MapCxWithNullToCx (Cx cx, Cx cxOut, String defaultValue) {
        cxOut.setIdentifierTypeCode(defaultValue);


        if (cx.getAssignAuthority() != null && !cx.getAssignAuthority().getNameSpaceId().isEmpty()) {
            cxOut.getAssignFacility().setNameSpaceId(cx.getAssignFacility().getNameSpaceId());
            if(cx.getAssignAuthority().getUniversalId() == null || cx.getAssignAuthority().getUniversalId().isEmpty()) {
                cxOut.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            } else {
                cxOut.getAssignAuthority().setUniversalId(cx.getAssignAuthority().getUniversalId());
            }

            if (cx.getAssignAuthority().getUniversalIdType() == null || cx.getAssignAuthority().getUniversalIdType().isEmpty()) {
                cxOut.getAssignAuthority().setUniversalIdType("NI");
            } else {
                cxOut.getAssignAuthority().setUniversalIdType(cx.getAssignAuthority().getUniversalIdType());
            }

            if(cx.getAssignFacility() != null && cx.getAssignFacility().getNameSpaceId().isEmpty()) {
                cxOut.getAssignFacility().setNameSpaceId(cx.getAssignFacility().getNameSpaceId());
                if(cx.getAssignFacility().getUniversalId() == null || cx.getAssignFacility().getUniversalId().isEmpty()) {
                    cxOut.getAssignFacility().setUniversalId("2.16.840.1.113883.5.1008");
                } else {
                    cxOut.getAssignFacility().setUniversalId(cx.getAssignFacility().getUniversalId());
                }

                if(cx.getAssignFacility().getUniversalIdType() == null || cx.getAssignFacility().getUniversalIdType().isEmpty()) {
                    cxOut.getAssignFacility().setUniversalIdType("NI");
                } else {
                    cxOut.getAssignFacility().setUniversalIdType(cx.getAssignFacility().getUniversalIdType());
                }
            }
        } else  if (cx.getAssignFacility() != null && !cx.getAssignFacility().getNameSpaceId().isEmpty()) {
            cxOut.getAssignAuthority().setNameSpaceId(cx.getAssignFacility().getNameSpaceId());
            if(cx.getAssignFacility().getUniversalId() == null || cx.getAssignFacility().getUniversalId().isEmpty()) {
                cxOut.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            } else {
                cxOut.getAssignAuthority().setUniversalId(cx.getAssignFacility().getUniversalId());
            }

            if(cx.getAssignFacility().getUniversalIdType() == null || cx.getAssignFacility().getUniversalIdType().isEmpty()) {
                cxOut.getAssignFacility().setUniversalIdType("NI");
            } else {
                cxOut.getAssignAuthority().setUniversalIdType(cx.getAssignFacility().getUniversalIdType());
            }
        } else  {
            cxOut.getAssignAuthority().setNameSpaceId("");
            cxOut.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            cxOut.getAssignAuthority().setUniversalIdType("NI");
        }

        return cxOut;
    }

    public static Xpn MapXpnToXpn(Xpn xpnIn, Xpn xpnOut, String defaultValue) {
        xpnOut.setFamilyName(xpnIn.getFamilyName());
        xpnOut.setGivenName(xpnIn.getGivenName());
        xpnOut.setSecondAndFurtherGivenNameOrInitial(xpnIn.getSecondAndFurtherGivenNameOrInitial());
        xpnOut.setSuffix(xpnIn.getSuffix());
        xpnOut.setPrefix(xpnIn.getPrefix());
        xpnOut.setDegree(xpnIn.getDegree());
        xpnOut.setNameTypeCode(defaultValue);
        xpnOut.setNameRepresentationCode(xpnIn.getNameRepresentationCode());
        return xpnOut;
    }


    public static Cx MapDlnToCx(Dln dln, Cx cx) {
        cx.setIdNumber(dln.getLicenseNumber());
        if(dln.getIssuedStateCountry() != null && !dln.getIssuedStateCountry().isEmpty()) {
            cx.getAssignAuthority().setNameSpaceId(dln.getIssuedStateCountry());
            cx.getAssignAuthority().setUniversalId("OID");
            cx.getAssignAuthority().setUniversalIdType("ISO");
        } else {
            cx.getAssignAuthority().setNameSpaceId("");
            cx.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            cx.getAssignAuthority().setUniversalIdType("NI");
        }

        if (dln.getExpirationDate() != null) {
            cx.setExpirationDate(dln.getExpirationDate());
        }

        cx.setIdentifierTypeCode("DL");
        return cx;
    }


    public static ObservationResult MapObservationResultToObservationResult(ObservationResult obxIn, ObservationResult obxOut) {
        if (obxIn.getProducerId() == null) {
            obxOut.getPerformingOrganizationName().setOrganizationName("Not present in v2.3.1 message");
        }

        if(obxIn.getPerformingOrganizationAddress().getStreetAddress() == null ||
            obxIn.getPerformingOrganizationAddress().getCity().isEmpty() ||
            obxIn.getPerformingOrganizationAddress().getState().isEmpty() ||
            obxIn.getPerformingOrganizationAddress().getCountry().isEmpty() ||
            obxIn.getPerformingOrganizationAddress().getZip().isEmpty()) {
            obxOut.getPerformingOrganizationAddress().getStreetAddress().setStreetMailingAddress("Not present in v2.3.1 message");
        }

        return obxOut;
    }

    public static ObservationRequest ObservationRequestToObservationRequest(ObservationRequest in, ObservationRequest out, Ts timestamp) {
        if(in.getResultRptStatusChngDateTime() == null ||
                (in.getResultRptStatusChngDateTime() != null && in.getResultRptStatusChngDateTime().getTime().isEmpty())
        ) {
            out.setResultRptStatusChngDateTime(timestamp);
        }
        return out;
    }

    public static Specimen ObservationRequestToSpecimen(ObservationRequest in, Specimen out) {
        out.setSetIdSpm(in.getSetIdObr());
        out.getSpecimenId().setPlacerAssignedIdentifier(in.getPlacerOrderNumber());
        out.getSpecimenId().setFillerAssignedIdentifier(in.getFillerOrderNumber());

        if(in.getSpecimenSource().getSpecimenSourceNameOrCode() == null) {
            out.getSpecimenType().setIdentifier("UNK");
            out.getSpecimenType().setText("Unknown");
            out.getSpecimenType().setNameOfCodingSystem("NullFlavor");
        } else {
            out.setSpecimenType(in.getSpecimenSource().getSpecimenSourceNameOrCode());
        }
        out.getSpecimenAdditives().add(in.getSpecimenSource().getAdditives());
        out.setSpecimenCollectionMethod(in.getSpecimenSource().getCollectionMethodModifierCode());
        out.setSpecimenSourceSite(in.getSpecimenSource().getBodySite());
        out.getSpecimenSourceSiteModifier().add(in.getSpecimenSource().getSiteModifier());
        out.getSpecimenCollectionAmount().setQuantity(in.getCollectionVolume().getQuantity());
        out.getSpecimenCollectionAmount().setUnits(in.getCollectionVolume().getUnits());
        out.getSpecimenDescription().add(in.getSpecimenSource().getSpecimenCollectionMethod());
        out.getSpecimenCollectionDateTime().setRangeStartDateTime(in.getObservationDateTime());
        out.getSpecimenCollectionDateTime().setRangeEndDateTime(in.getObservationEndDateTime());
        out.setSpecimenReceivedDateTime(in.getSpecimenReceivedDateTime());
        out.setNumberOfSpecimenContainers(in.getNumberOfSampleContainers());
        return out;
    }


}
