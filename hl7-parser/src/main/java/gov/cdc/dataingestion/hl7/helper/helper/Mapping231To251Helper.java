package gov.cdc.dataingestion.hl7.helper.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.datatype.*;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PID;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_segment.MessageHeader;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_segment.SoftwareSegment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Mapping231To251Helper {
    private static final String UNIVERSAL_ID = "2.16.840.1.113883.5.1008";

    public Mapping231To251Helper() { // NOSONAR
        // Default constructor
    }
    //region Map Message Header - 231 to 251
    public static MessageHeader mapMsh(MSH inMsh231, MessageHeader outMsh251) throws DiHL7Exception {
        outMsh251.getMessageType().setMessageCode("ORU");
        outMsh251.getMessageType().setTriggerEvent("R01");
        outMsh251.getMessageType().setMessageStructure("ORU_R01");
        outMsh251.getVersionId().setVersionId("2.5.1");

        Ei messageProfileIdentifier = new Ei();
        messageProfileIdentifier.setEntityIdentifier("PHLabReport-NoAck");
        messageProfileIdentifier.setNameSpaceId("ELR_Receiver");
        messageProfileIdentifier.setUniversalId("2.16.840.1.113883.9.11");
        messageProfileIdentifier.setUniversalIdType("ISO");

        try {
            if (inMsh231 != null) {
                Ts ts = new Ts();
                if(inMsh231.getDateTimeOfMessage() != null && !inMsh231.getDateTimeOfMessage().isEmpty()) {
                    ts.setTime(inMsh231.getDateTimeOfMessage().getTimeOfAnEvent().getValue());
                    ts.setDegreeOfPrecision(inMsh231.getDateTimeOfMessage().getDegreeOfPrecision().getValue());
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    String formattedDateTime = now.format(formatter);
                    ts.setTime(formattedDateTime);
                }
                outMsh251.setDateTimeOfMessage(ts);
            }
        } catch (HL7Exception e) {
            throw new DiHL7Exception("MSH Error at DateTime Field");
        }


        if(outMsh251.getMessageProfileIdentifier() != null) {

            if (!outMsh251.getMessageProfileIdentifier().isEmpty()) {
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
    public static List<SoftwareSegment> mapSoftwareSegment(List<SoftwareSegment> softwareSegment) {
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
            softwareSegment = new ArrayList<>();
            softwareSegment.add(sft);
        }
        return softwareSegment;
    }
    //endregion


    //region Map Patient Identification - 231 to 251
    public static PatientIdentification mapPid(PID inPid231, PatientIdentification outPid251) {
        outPid251.setSetPid("1");
        if (outPid251.getPatientIdentifierList() == null) {
            outPid251.setPatientIdentifierList(new ArrayList<>());
        }

        if (outPid251.getPatientName() == null) {
            outPid251.setPatientName(new ArrayList<>());
        }

        // PatientIdentifierList
        outPid251 = getPatientIdentifierList(inPid231, outPid251);

        // Patient ID
        if (outPid251.getPatientId() != null) {
            outPid251.getPatientIdentifierList().add(mapCxWithNullToCx(inPid231.getPatientID(), outPid251.getPatientId(), "PT"));
        }

        // Patient AlternatePatientId
        for(int b = 0; b < outPid251.getAlternativePatientId().size(); b++) {
            if (outPid251.getAlternativePatientId().get(b) != null) {
                outPid251.getPatientIdentifierList().add(mapCxWithNullToCx(inPid231.getAlternatePatientIDPID(b), outPid251.getAlternativePatientId().get(b), "APT"));
            }
        }

        // Patient Name - hapi
        // MotherMaidenName - hapi
        // DateTimeOfBirth - hapi
        // Patient Alias
        for(int c = 0; c < outPid251.getPatientAlias().size(); c++) {
            if (outPid251.getPatientAlias().get(c) != null) {
                outPid251.getPatientName().add(mapXpnToXpn(outPid251.getPatientAlias().get(c), new Xpn(), "A"));
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
        outPid251.getPatientIdentifierList().add(mapSocialSecurityToPatientIdentifier(outPid251.getSsnNumberPatient(), new Cx()));
        // Mother Identifier - hapi
        // Ethnic Group - hapi
        // Citizenship - hapi
        // Veteran - hapi
        // Nationality - hapi
        // mapSocialSecurityToPatientIdentifier - hapi
        // DriversLicenseNumber
        outPid251.getPatientIdentifierList().add(mapDlnToCx(outPid251.getDriverLicenseNumberPatient(), new Cx()));
        return outPid251;
    }

    private static PatientIdentification getPatientIdentifierList(PID inPid231, PatientIdentification outPid251) {
        for(int a = 0; a < inPid231.getPatientIdentifierList().length; a++) {
            if (inPid231.getPatientIdentifierList(a) != null) {
                var patientIdentifier = inPid231.getPatientIdentifierList(a);
                Cx newPatientIdentifier;
                if (patientIdentifier.getIdentifierTypeCode() == null ||
                        (patientIdentifier.getIdentifierTypeCode() != null && patientIdentifier.getIdentifierTypeCode().getValue() == null)
                        ||
                        (patientIdentifier.getIdentifierTypeCode() != null &&
                                patientIdentifier.getIdentifierTypeCode().getValue() != null &&
                                patientIdentifier.getIdentifierTypeCode().getValue().isEmpty())
                ) {
                    newPatientIdentifier = mapCxWithNullToCx(inPid231.getPatientIdentifierList(a), outPid251.getPatientIdentifierList().get(a), "U");
                } else {
                    newPatientIdentifier = mapCxWithNullToCx(inPid231.getPatientIdentifierList(a),  outPid251.getPatientIdentifierList().get(a), patientIdentifier.getIdentifierTypeCode().getValue());
                }
                outPid251.getPatientIdentifierList().set(a, newPatientIdentifier);
            }
        }
        return outPid251;
    }

    //endregion

    //region Map ORC to ORC - 231 to 251

    private static CommonOrder mapCommonOrderListHelper1st(ORC inOrc231, CommonOrder outOrc251) {
        List<Xcn> enterBys = new ArrayList<>();
        for(int a = 0; a < inOrc231.getEnteredBy().length; a++) {
            if(inOrc231.getEnteredBy()[a] != null) {
                enterBys.add(mapXcn231(inOrc231.getEnteredBy(a), new Xcn()));
            }
        }
        outOrc251.setEnteredBy(enterBys);

        List<Xcn> verifiedBys = new ArrayList<>();
        for (int b = 0; b < inOrc231.getVerifiedBy().length; b++) {
            if(inOrc231.getVerifiedBy()[b] != null) {
                verifiedBys.add(mapXcn231(inOrc231.getVerifiedBy()[b], new Xcn()));
            }
        }
        outOrc251.setVerifiedBy(verifiedBys);

        List<Xcn> orderProviders = new ArrayList<>();
        for(int c = 0; c < inOrc231.getOrderingProvider().length; c++) {
            if(inOrc231.getOrderingProvider()[c] != null) {
                orderProviders.add(mapXcn231(inOrc231.getOrderingProvider()[c], new Xcn()));
            }
        }
        outOrc251.setOrderingProvider(orderProviders);

        if(inOrc231.getEntererSLocation() != null) {
            outOrc251.setEntererLocation(mapPl231(inOrc231.getEntererSLocation(), outOrc251.getEntererLocation()));
        }

        outOrc251 = mapCommonOrderListHelper1stNested(inOrc231, outOrc251);
        return outOrc251;
    }

    private static CommonOrder mapCommonOrderListHelper1stNested(ORC inOrc231, CommonOrder outOrc251) {
        List<Xtn> phoneList = new ArrayList<>();
        for(int d = 0; d < inOrc231.getCallBackPhoneNumber().length; d++) {
            if(inOrc231.getCallBackPhoneNumber()[d] != null) {
                phoneList.add(mapXtn231(inOrc231.getCallBackPhoneNumber(d), new Xtn()));
            }
        }
        outOrc251.setCallBackPhoneNumber(phoneList);

        if(inOrc231.getOrderEffectiveDateTime() != null) {
            outOrc251.setOrderEffectiveDateTime(mapTs231(inOrc231.getOrderEffectiveDateTime(), outOrc251.getOrderEffectiveDateTime()));
        }

        if(inOrc231.getOrderControlCodeReason() != null) {
            outOrc251.setOrderControlCodeReason(mapCe231(inOrc231.getOrderControlCodeReason(), outOrc251.getOrderControlCodeReason()));
        }

        if(inOrc231.getEnteringOrganization() != null) {
            outOrc251.setEnteringOrganization(mapCe231(inOrc231.getEnteringOrganization(), outOrc251.getEnteringOrganization()));
        }

        if(inOrc231.getEnteringDevice() != null) {
            outOrc251.setEnteringDevice(mapCe231(inOrc231.getEnteringDevice(), outOrc251.getEnteringDevice()));
        }

        List<Xcn> actionbyList = new ArrayList<>();
        for(int e = 0; e < inOrc231.getActionBy().length; e++) {
            if(inOrc231.getActionBy(e) != null) {
                actionbyList.add(mapXcn231(inOrc231.getActionBy(e), new Xcn()));
            }
        }
        outOrc251.setActionBy(actionbyList);
        return outOrc251;
    }

    private static CommonOrder mapCommonOrderListHelper2st(ORC inOrc231, CommonOrder outOrc251) {
        List<Xon> facilityList = new ArrayList<>();
        for(int f= 0; f < inOrc231.getOrderingFacilityName().length; f++) {
            if(inOrc231.getOrderingFacilityName(f) != null) {
                facilityList.add(mapXon231(inOrc231.getOrderingFacilityName(f), new Xon()));
            }
        }
        outOrc251.setOrderingFacilityName(facilityList);

        List<Xad> orderingFacilityList = new ArrayList<>();
        for(int g = 0; g < inOrc231.getOrderingFacilityAddress().length; g++) {
            if(inOrc231.getOrderingFacilityAddress(g) != null) {
                orderingFacilityList.add(mapXad231(inOrc231.getOrderingFacilityAddress(g), new Xad()));
            }
        }
        outOrc251.setOrderingFacilityAddress(orderingFacilityList);

        List<Xtn> orderingFacilityPhoneList = new ArrayList<>();
        for(int h = 0; h < inOrc231.getOrderingFacilityPhoneNumber().length; h++) {
            if(inOrc231.getOrderingFacilityPhoneNumber(h) != null) {
                orderingFacilityPhoneList.add(mapXtn231(inOrc231.getOrderingFacilityPhoneNumber(h), new Xtn()));
            }
        }
        outOrc251.setOrderingFacilityPhoneNumber(orderingFacilityPhoneList);

        List<Xad> orderingProviderAddressList = new ArrayList<>();
        for(int j = 0; j < inOrc231.getOrderingProviderAddress().length; j++) {
            if(inOrc231.getOrderingProviderAddress(j) != null) {
                orderingProviderAddressList.add(mapXad231(inOrc231.getOrderingProviderAddress(j), new Xad()));
            }
        }
        outOrc251.setOrderingProviderAddress(orderingProviderAddressList);
        return outOrc251;
    }

    private static CommonOrder orderControlCheck(ORC inOrc231, CommonOrder outOrc251) {
        if (
                (inOrc231.getOrderControl() == null) ||
                        (inOrc231.getOrderControl() != null && inOrc231.getOrderControl().getValue() == null) ||
                        (inOrc231.getOrderControl() != null && inOrc231.getOrderControl().getValue() != null && inOrc231.getOrderControl().getValue().isEmpty())
        ) {
            outOrc251.setOrderControl("RE");
        } else {
            outOrc251.setOrderControl(inOrc231.getOrderControl().getValue());
        }
        return outOrc251;
    }
    public static CommonOrder mapCommonOrder(ORC inOrc231, CommonOrder outOrc251) {
        orderControlCheck(inOrc231, outOrc251);

        if (inOrc231.getPlacerOrderNumber() != null) {
            outOrc251.setPlacerGroupNumber(mapEi231(inOrc231.getPlacerOrderNumber(), outOrc251.getPlacerGroupNumber()));
        }

        if (inOrc231.getFillerOrderNumber() != null) {
            outOrc251.setFillerOrderNumber(mapEi231(inOrc231.getFillerOrderNumber(), outOrc251.getFillerOrderNumber()));
        }

        if (inOrc231.getPlacerGroupNumber() != null) {
            outOrc251.setPlacerGroupNumber(mapEi231(inOrc231.getPlacerGroupNumber(), outOrc251.getPlacerGroupNumber()));
        }

        outOrc251.setOrderStatus(inOrc231.getOrderStatus().getValue());
        outOrc251.setResponseFlag(inOrc231.getResponseFlag().getValue());

        if (inOrc231.getQuantityTiming() != null) {
            Tq timeQty = mapTq231(inOrc231.getQuantityTiming(), new Tq());
            if(outOrc251.getQuantityTiming() != null) {
                outOrc251.getQuantityTiming().add(timeQty);
            } else {
                var quantityTimeList = new ArrayList<Tq>();
                quantityTimeList.add(timeQty);
                outOrc251.setQuantityTiming(quantityTimeList);
            }
        }

        if(inOrc231.getParentOrder() != null) {
            outOrc251.setParentOrder(mapEip231(inOrc231.getParentOrder(), outOrc251.getParentOrder()));
        }

        if(inOrc231.getDateTimeOfTransaction() != null) {
            outOrc251.setDateTimeOfTransaction(mapTs231(inOrc231.getDateTimeOfTransaction(), outOrc251.getDateTimeOfTransaction()));
        }

        outOrc251 =  mapCommonOrderListHelper1st(inOrc231, outOrc251);

        if(inOrc231.getAdvancedBeneficiaryNoticeCode() != null) {
            outOrc251.setAdvancedBeneficiaryNoticeCode(mapCe231(inOrc231.getAdvancedBeneficiaryNoticeCode(), outOrc251.getAdvancedBeneficiaryNoticeCode()));
        }

        outOrc251 = mapCommonOrderListHelper2st( inOrc231, outOrc251);

        return outOrc251;
    }
    //endregion

    //region Map OBR2and3ToORC2and3
    public static CommonOrder mapOBR2and3ToORC2and3(OBR in, CommonOrder out) {
        out.setPlacerGroupNumber(mapEi231(in.getPlacerOrderNumber(), out.getPlacerOrderNumber()));
        out.setFillerOrderNumber(mapEi231(in.getFillerOrderNumber(), out.getFillerOrderNumber()));
        return out;
    }

    public static Xad mapXad231(XAD in, Xad out) {
        Sad sad = new Sad();
        sad.setStreetMailingAddress(in.getStreetAddress().getValue());
        out.setStreetAddress(sad);
        out.setOtherDesignation(in.getOtherDesignation().getValue());
        out.setCity(in.getCity().getValue());
        out.setState(in.getStateOrProvince().getValue());
        out.setZip(in.getZipOrPostalCode().getValue());
        out.setCountry(in.getCountry().getValue());
        out.setAddressType(in.getAddressType().getValue());
        out.setOtherDesignation(in.getOtherDesignation().getValue());
        out.setCensusTract(in.getCensusTract().getValue());
        out.setAddressRepresentationCode(in.getAddressRepresentationCode().getValue());
        out.setCountyCode(in.getCountyParishCode().getValue());
        out.setOtherGeographic(in.getOtherDesignation().getValue());
        return out;

    }

    public static Xon mapXon231(XON in, Xon out) {
        out.setOrganizationName(in.getOrganizationName().getValue());
        out.setOrganizationNameTypeCode(in.getOrganizationNameTypeCode().getValue());
        out.setOrganizationIdentifier(in.getIDNumber().getValue());
        out.setCheckDigitScheme(in.getCodeIdentifyingTheCheckDigitSchemeEmployed().getValue());
        if(in.getAssigningAuthority() != null) {
            out.setAssignAuthority(mapHd231(in.getAssigningAuthority(), out.getAssignAuthority()));
        }
        out.setIdentifierTypeCode(in.getIdentifierTypeCode().getValue());
        out.setNameRepresentationCode(in.getNameRepresentationCode().getValue());
        if(in.getAssigningFacilityID() != null) {
            out.setAssignFacility(mapHd231(in.getAssigningFacilityID(), out.getAssignFacility()));
        }
        return out;
    }

    public static Xtn mapXtn231(XTN in, Xtn out) {
        out.setTelephoneNumber(in.getPhoneNumber().getValue());
        out.setTeleComCode(in.getTelecommunicationUseCode().getValue());
        out.setTeleComEquipmentType(in.getXtn3_TelecommunicationEquipmentType().getValue());
        out.setEmailAddress(in.getEmailAddress().getValue());
        out.setCountryCode(in.getCountryCode().getValue());
        out.setCityCode(in.getAreaCityCode().getValue());
        out.setLocalNumber(in.getPhoneNumber().getValue());
        out.setExtension(in.getExtension().getValue());
        out.setAnyText(in.getAnyText().getValue());
        return out;
    }

    public static Pl mapPl231(PL in, Pl out) {
        out.setPointOfCare(in.getPointOfCare().getValue());
        out.setRoom(in.getRoom().getValue());
        out.setBed(in.getBed().getValue());
        if(in.getFacility() != null) {
            out.setFacility(mapHd231(in.getFacility(), out.getFacility()));
        }
        out.setLocationStatus(in.getLocationStatus().getValue());
        out.setBuilding(in.getBuilding().getValue());
        out.setFloor(in.getFloor().getValue());
        out.setLocationDescription(in.getLocationDescription().getValue());
        out.setPersonLocationType(in.getPersonLocationType().getValue());
        return out;
    }

    public static Xcn mapXcn231(XCN in, Xcn out) {
        out.setIdNumber(in.getIDNumber().getValue());
        Fn familyName = new Fn();
        familyName.setSurname(in.getFamilyLastName().getFamilyName().getValue());
        out.setFamilyName(familyName);
        out.setGivenName(in.getGivenName().getValue());
        out.setSecondAndFurtherGivenNameOrInitial(in.getMiddleInitialOrName().getValue());
        out.setSuffix(in.getSuffixEgJRorIII().getValue());
        out.setPrefix(in.getPrefixEgDR().getValue());
        out.setDegree(in.getDegreeEgMD().getValue());
        out.setSourceTable(in.getSourceTable().getValue());

        if (in.getAssigningAuthority() != null) {
            out.setAssignAuthority(mapHd231(in.getAssigningAuthority(), out.getAssignAuthority()));
        }

        out.setNameTypeCode(in.getNameTypeCode().getValue());
        out.setIdentifierCheckDigit(in.getIdentifierCheckDigit().getValue());
        out.setCheckDigitScheme(in.getCodeIdentifyingTheCheckDigitSchemeEmployed().getValue());
        out.setIdentifierTypeCode(in.getIdentifierTypeCode().getValue());

        if(in.getAssigningFacility() != null) {
            out.setAssignFacility(mapHd231(in.getAssigningFacility(), out.getAssignFacility()));
        }
        out.setNameRepresentationCode(in.getNameRepresentationCode().getValue());
        return out;
    }

    public static Hd mapHd231(HD in, Hd out) {
        out.setNameSpaceId(in.getNamespaceID().getValue());
        out.setUniversalId(in.getUniversalID().getValue());
        out.setUniversalIdType(in.getUniversalIDType().getValue());
        return out;
    }

    public static Eip mapEip231(EIP in, Eip out) {
        out.setPlacerAssignedIdentifier(mapEi231(in.getParentSPlacerOrderNumber(), out.getPlacerAssignedIdentifier()));
        out.setFillerAssignedIdentifier(mapEi231(in.getParentSFillerOrderNumber(), out.getFillerAssignedIdentifier()));
        return out;
    }

    public static Tq mapTq231(TQ in, Tq out) {

        if (in.getQuantity() != null) {
            out.setQuantity(mapCq231(in.getQuantity(), out.getQuantity()));
        }

        if (in.getInterval() != null) {
            out.setInterval(mapRi231(in.getInterval(), out.getInterval()));
        }

        out.setDuration(in.getDuration().getValue());

        if (in.getStartDateTime() != null) {
            out.setStartDateTime(mapTs231(in.getStartDateTime(), out.getStartDateTime()));
        }

        if (in.getEndDateTime() != null) {
            out.setEndDateTime(mapTs231(in.getEndDateTime(), out.getEndDateTime()));
        }

        out.setPriority(in.getPriority().getValue());
        out.setCondition(in.getCondition().getValue());
        out.setText(in.getText().getValue());
        out.setConjunction(in.getConjunction().getValue());

        if(in.getOrderSequencing() != null) {
            out.setOrderSequencing(mapOsd231(in.getOrderSequencing(), out.getOrderSequencing()));
        }

        if(in.getOccurrenceDuration() != null) {
            out.setOccurrenceDuration(mapCe231(in.getOccurrenceDuration(), out.getOccurrenceDuration()));
        }

        out.setTotalOccurrences(in.getTotalOccurences().getValue());
        return out;
    }

    public static Osd mapOsd231(OSD in, Osd out) {
        out.setSequenceResultFlag(in.getSequenceResultsFlag().getValue());
        out.setPlacerOrderNumberEntityIdentifier(in.getPlacerOrderNumberEntityIdentifier().getValue());
        out.setPlacerOrderNumberUniversalId(in.getPlacerOrderNumberUniversalID().getValue());
        out.setFillerOrderNumberEntityIdentifier(in.getFillerOrderNumberEntityIdentifier().getValue());
        out.setFillerOrderNumberNamespaceId(in.getFillerOrderNumberNamespaceID().getValue());
        out.setSequenceConditionValue(in.getSequenceConditionValue().getValue());
        out.setPlacerOrderNumberUniversalId(in.getPlacerOrderNumberUniversalID().getValue());
        out.setPlacerOrderNumberUniversalIdType(in.getPlacerOrderNumberUniversalIDType().getValue());
        out.setFillerOrderNumberNamespaceId(in.getFillerOrderNumberNamespaceID().getValue());
        out.setFillerOrderNumberUniversalIdType(in.getFillerOrderNumberUniversalIDType().getValue());
        out.setMaximumNumberOfRepeats(in.getMaximumNumberOfRepeats().getValue());
        return out;
    }

    public static Ts mapTs231(TS in, Ts out) {
        out.setTime(in.getTimeOfAnEvent().getValue());
        out.setDegreeOfPrecision(in.getDegreeOfPrecision().getValue());
        return out;
    }

    public static Ri mapRi231(RI in, Ri out) {
        out.setRepeatPattern(in.getRepeatPattern().getValue());
        out.setExplicitTimeInterval(in.getExplicitTimeInterval().getValue());
        return out;
    }

    public static Cq mapCq231(CQ in, Cq out) {
        out.setQuantity(in.getQuantity().getValue());
        out.setUnits(mapCe231(in.getUnits(), out.getUnits()));
        return out;
    }

    public static Ce mapCe231(CE in, Ce out) {
        out.setIdentifier(in.getIdentifier().getValue());
        out.setText(in.getText().getValue());
        out.setNameOfCodingSystem(in.getNameOfCodingSystem().getValue());
        out.setAlternateIdentifier(in.getAlternateIdentifier().getValue());
        out.setAlternateText(in.getAlternateText().getValue());
        out.setNameOfAlternateCodingSystem(
                in.getNameOfAlternateCodingSystem().getValue()
        );
        return out;
    }

    public static Ei mapEi231(EI in, Ei out) {
        out.setEntityIdentifier(in.getEntityIdentifier().getValue());
        out.setNameSpaceId(in.getNamespaceID().getValue());
        out.setUniversalId(in.getUniversalID().getValue());
        out.setUniversalIdType(in.getUniversalIDType().getValue());
        return out;
    }

    public static Cx mapSocialSecurityToPatientIdentifier(String ssnNumber, Cx out) {
        out.setIdNumber(ssnNumber);
        Hd hd = new Hd();
        hd.setNameSpaceId("SSA");
        hd.setUniversalId("2.16.840.1.113883.4.1");
        hd.setUniversalIdType("ISO");
        out.setAssignAuthority(hd);
        out.setIdentifierTypeCode("SS");

        // DI native
        out.setAssignFacility(mapAssignedFacility(out.getAssignFacility(), out.getAssignAuthority()));
        return out;
    }

    private static Cx mapCxWithNullToCxAuthority(CX inOri,  Cx cxOut) {
        if(inOri.getAssigningAuthority().getUniversalID() == null
                || inOri.getAssigningAuthority().getUniversalID().getValue() == null
                || (inOri.getAssigningAuthority().getUniversalID().getValue() != null &&
                inOri.getAssigningAuthority().getUniversalID().getValue().isEmpty())
        ) {
            cxOut.getAssignAuthority().setUniversalId(UNIVERSAL_ID);
        } else {
            cxOut.getAssignAuthority().setUniversalId(inOri.getAssigningAuthority().getUniversalID().getValue());
        }

        if (inOri.getAssigningAuthority().getUniversalIDType() == null
                || inOri.getAssigningAuthority().getUniversalIDType().getValue() == null
                || (inOri.getAssigningAuthority().getUniversalIDType().getValue() != null &&
                inOri.getAssigningAuthority().getUniversalIDType().getValue().isEmpty())) {
            cxOut.getAssignAuthority().setUniversalIdType("NI");
        } else {
            cxOut.getAssignAuthority().setUniversalIdType(inOri.getAssigningAuthority().getUniversalIDType().getValue());
        }
        return cxOut;
    }
    private static Cx mapCxWithNullToCxFacility(CX inOri,  Cx cxOut) {
        if(inOri.getAssigningFacility() != null
                && inOri.getAssigningFacility().getNamespaceID() != null
                && inOri.getAssigningFacility().getNamespaceID().getValue() != null
                &&  !inOri.getAssigningFacility().getNamespaceID().getValue().isEmpty()) {
            cxOut.getAssignFacility().setNameSpaceId(inOri.getAssigningFacility().getNamespaceID().getValue());
            if(inOri.getAssigningFacility().getUniversalID() == null
                    || inOri.getAssigningFacility().getUniversalID().getValue() == null
                    || (inOri.getAssigningFacility().getUniversalID().getValue() != null &&
                    inOri.getAssigningFacility().getUniversalID().getValue().isEmpty()) ) {
                cxOut.getAssignFacility().setUniversalId(UNIVERSAL_ID);
            } else {
                cxOut.getAssignFacility().setUniversalId(inOri.getAssigningFacility().getUniversalID().getValue());
            }

            if(inOri.getAssigningFacility().getUniversalIDType() == null
                    || inOri.getAssigningFacility().getUniversalIDType().getValue() == null
                    || (inOri.getAssigningFacility().getUniversalIDType().getValue() != null &&
                    inOri.getAssigningFacility().getUniversalIDType().getValue().isEmpty()) ) {
                cxOut.getAssignFacility().setUniversalIdType("NI");
            } else {
                cxOut.getAssignFacility().setUniversalIdType(inOri.getAssigningFacility().getUniversalIDType().getValue());
            }
        }
        return cxOut;
    }
    public static Cx mapCxWithNullToCx (CX inOri, Cx cxOut, String defaultValue) {
        cxOut.setIdNumber(inOri.getID().getValue());
        cxOut.setCheckDigit(inOri.getCheckDigit().getValue());
        cxOut.setCheckDigitScheme(inOri.getCodeIdentifyingTheCheckDigitSchemeEmployed().getValue());
        cxOut.setIdentifierTypeCode(defaultValue);

        // not null and not empty
        if (inOri.getAssigningAuthority() != null
                && inOri.getAssigningAuthority().getNamespaceID() != null
                && inOri.getAssigningAuthority().getNamespaceID().getValue() != null
                && !inOri.getAssigningAuthority().getNamespaceID().getValue().isEmpty()) {
            cxOut.getAssignAuthority().setNameSpaceId(inOri.getAssigningAuthority().getNamespaceID().getValue());
            mapCxWithNullToCxAuthority(inOri, cxOut);
            mapCxWithNullToCxFacility(inOri, cxOut);
        }
        else  if (inOri.getAssigningFacility() != null
                && inOri.getAssigningFacility().getNamespaceID() != null
                && inOri.getAssigningFacility().getNamespaceID().getValue() != null
                &&  !inOri.getAssigningFacility().getNamespaceID().getValue().isEmpty()) {

            // Not from RHAP Map definition
            cxOut.setAssignFacility(mapHd231(inOri.getAssigningFacility(), cxOut.getAssignFacility()));

            cxOut.getAssignAuthority().setNameSpaceId(inOri.getAssigningFacility().getNamespaceID().getValue());

            if(inOri.getAssigningFacility().getUniversalID() == null
                    || inOri.getAssigningFacility().getUniversalID().getValue() == null
                    || (inOri.getAssigningFacility().getUniversalID().getValue() != null &&
                    inOri.getAssigningFacility().getUniversalID().getValue().isEmpty()) ) {
                cxOut.getAssignAuthority().setUniversalId(UNIVERSAL_ID);
            }
            else {
                cxOut.getAssignAuthority().setUniversalId(inOri.getAssigningFacility().getUniversalID().getValue());
            }


            if(inOri.getAssigningFacility().getUniversalIDType() == null
                    || inOri.getAssigningFacility().getUniversalIDType().getValue() == null
                    || (inOri.getAssigningFacility().getUniversalIDType().getValue() != null &&
                    inOri.getAssigningFacility().getUniversalIDType().getValue().isEmpty()) )  {
                cxOut.getAssignAuthority().setUniversalIdType("NI");
            }
            else {
                cxOut.getAssignAuthority().setUniversalIdType(inOri.getAssigningFacility().getUniversalIDType().getValue());
            }


        }
        else  {
            cxOut.getAssignAuthority().setNameSpaceId("");
            cxOut.getAssignAuthority().setUniversalId(UNIVERSAL_ID);
            cxOut.getAssignAuthority().setUniversalIdType("NI");
        }

        // DI native
        cxOut.setAssignFacility(mapAssignedFacility(cxOut.getAssignFacility(), cxOut.getAssignAuthority()));

        return cxOut;
    }

    //DI's native
    public static Hd mapAssignedFacility(Hd out, Hd inAssignAuthority) {
        if (out.getUniversalId() == null
                || out.getUniversalId().isEmpty()
        ) {
            out.setUniversalId(inAssignAuthority.getUniversalId());
        }

        // DI native
        if (out.getUniversalIdType() == null
                || out.getUniversalIdType().isEmpty())
        {
            out.setUniversalIdType(inAssignAuthority.getUniversalIdType());
        }

        if (out.getNameSpaceId() == null
                || out.getNameSpaceId().isEmpty()
        )
        {
            out.setNameSpaceId(inAssignAuthority.getNameSpaceId());
        }

        return out;
    }

    public static Xpn mapXpnToXpn(Xpn xpnIn, Xpn xpnOut, String defaultValue) {
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


    public static Cx mapDlnToCx(Dln dln, Cx cx) {
        cx.setIdNumber(dln.getLicenseNumber());
        if(dln.getIssuedStateCountry() != null && !dln.getIssuedStateCountry().isEmpty()) {
            cx.getAssignAuthority().setNameSpaceId(dln.getIssuedStateCountry());
            cx.getAssignAuthority().setUniversalId("OID");
            cx.getAssignAuthority().setUniversalIdType("ISO");
        } else {
            cx.getAssignAuthority().setNameSpaceId("");
            cx.getAssignAuthority().setUniversalId(UNIVERSAL_ID);
            cx.getAssignAuthority().setUniversalIdType("NI");
        }

        if (dln.getExpirationDate() != null) {
            cx.setExpirationDate(dln.getExpirationDate());
        }

        cx.setIdentifierTypeCode("DL");

        cx.setAssignFacility(mapAssignedFacility(cx.getAssignFacility(), cx.getAssignAuthority()));

        return cx;
    }

    public static boolean mapObservationResultAddressInfoCheck(ObservationResult obxIn) {
        return obxIn.getPerformingOrganizationAddress().getStreetAddress() != null && (
                obxIn.getPerformingOrganizationAddress().getStreetAddress().getStreetName() == null || obxIn.getPerformingOrganizationAddress().getStreetAddress().getStreetMailingAddress() == null || obxIn.getPerformingOrganizationAddress().getStreetAddress().getStreetName().isEmpty() || obxIn.getPerformingOrganizationAddress().getStreetAddress().getStreetMailingAddress().isEmpty());
    }

    public static boolean mapObservationResultCityInfoCheck(ObservationResult obxIn) {
        return obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getCity() == null || obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getCity().isEmpty();
    }

    public static boolean mapObservationResultStateInfoCheck(ObservationResult obxIn) {
        return obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getState() == null || obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getState().isEmpty();
    }

    public static boolean mapObservationResultCountryInfoCheck(ObservationResult obxIn) {
        return obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getCountry() == null || obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getCountry().isEmpty();
    }

    public static boolean mapObservationResultZipInfoCheck(ObservationResult obxIn) {
        return obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getZip() == null || obxIn.getPerformingOrganizationAddress() != null && obxIn.getPerformingOrganizationAddress().getZip().isEmpty();
    }

    public static ObservationResult mapObservationResultToObservationResult(ObservationResult obxIn, ObservationResult obxOut) {

        if (obxIn.getProducerId() == null) {
            var orgXon = new Xon();
            orgXon.setOrganizationName("Not present in v2.3.1 message");
            obxOut.setPerformingOrganizationName(orgXon);
        } else {
            obxOut.setPerformingOrganizationName(mapCeToXon(obxIn.getProducerId(), new Xon()));
        }

        if (
                obxIn.getPerformingOrganizationAddress() == null ||
                        (mapObservationResultAddressInfoCheck(obxIn))
                        ||
                        (mapObservationResultCityInfoCheck(obxIn))
                        ||
                        (mapObservationResultStateInfoCheck(obxIn))
                        ||
                        (mapObservationResultCountryInfoCheck(obxIn))
                        ||
                        (mapObservationResultZipInfoCheck(obxIn))

        )
        {
            obxOut.getPerformingOrganizationAddress().getStreetAddress().setStreetMailingAddress("Not present in v2.3.1 message");
        } else {
            obxOut.setPerformingOrganizationAddress(mapXad(obxIn.getPerformingOrganizationAddress(), obxOut.getPerformingOrganizationAddress()));
        }

        if(
                obxIn.getDateTimeOfTheAnalysis() == null ||
                        obxIn.getDateTimeOfTheAnalysis() != null && obxIn.getDateTimeOfTheAnalysis().getTime() == null
        ) {
            obxOut.setDateTimeOfTheAnalysis(obxIn.getDateTimeOfTheObservation());
        }


        return obxOut;
    }

    public static ObservationRequest observationRequestToObservationRequest(OBR in231, ObservationRequest in, ObservationRequest out, Ts timestamp) throws DiHL7Exception
    {
        if(in.getResultRptStatusChngDateTime() == null ||
            (in.getResultRptStatusChngDateTime().getTime() != null && in.getResultRptStatusChngDateTime().getTime().isEmpty())
        ) {
            out.setResultRptStatusChngDateTime(timestamp);
        }

        if(in.getParentResult() != null) {
            out.setParentResult(mapPrl231(in231.getParentResult(), new Prl()));
        }
        return out;
    }

    public static Prl mapPrl231(PRL in, Prl out) throws DiHL7Exception {
        if(in.getOBX3ObservationIdentifierOfParentResult() != null) {
            out.setParentObservationIdentifier(mapCe231(in.getOBX3ObservationIdentifierOfParentResult(), out.getParentObservationIdentifier()));
        }

        out.setParentObservationSubIdentifier(in.getOBX4SubIDOfParentResult().getValue());

        var descriptor = (in.getPartOfOBX5ObservationResultFromParent().getValue());
        var compositeResult = in.getPartOfOBX5ObservationResultFromParent();
        if(compositeResult != null && compositeResult.getExtraComponents() != null && compositeResult.getExtraComponents().numComponents() > 0) {
            for(int i = 0; i < compositeResult.getExtraComponents().numComponents(); i++) {
                try {
                    descriptor = descriptor + "&" + compositeResult.getExtraComponents().getComponent(i).getData().encode();
                } catch (Exception e) {
                    throw new DiHL7Exception(e.getMessage());
                }
            }
        }

        out.setParentObservationValueDescriptor(descriptor);

        return out;
    }



    public static Specimen observationRequestToSpecimen(ObservationRequest in, Specimen out) {
        out.setSetIdSpm(in.getSetIdObr());
        out.getSpecimenId().setPlacerAssignedIdentifier(in.getPlacerOrderNumber());
        out.getSpecimenId().setFillerAssignedIdentifier(in.getFillerOrderNumber());

        if(in.getSpecimenSource().getSpecimenSourceNameOrCode() == null ||
                (in.getSpecimenSource().getSpecimenSourceNameOrCode() != null && in.getSpecimenSource().getSpecimenSourceNameOrCode().getIdentifier() == null)
        ) {
            out.getSpecimenType().setIdentifier("UNK");
            out.getSpecimenType().setText("Unknown");
            out.getSpecimenType().setNameOfCodingSystem("NullFlavor");
        } else {
            out.setSpecimenType(in.getSpecimenSource().getSpecimenSourceNameOrCode());
        }

        if(in.getSpecimenSource().getAdditives().getIdentifier() != null) {
            var spcAdditive = new Cwe();
            spcAdditive.setText(in.getSpecimenSource().getAdditives().getIdentifier());
            spcAdditive.setIdentifier(in.getSpecimenSource().getAdditives().getIdentifier());
            out.getSpecimenAdditives().add(spcAdditive);
        }



        out.setSpecimenCollectionMethod(in.getSpecimenSource().getCollectionMethodModifierCode());
        out.setSpecimenSourceSite(in.getSpecimenSource().getBodySite());

        if (in.getSpecimenSource().getSiteModifier() != null) {
            out.getSpecimenSourceSiteModifier().add(in.getSpecimenSource().getSiteModifier());
        }
        out.getSpecimenCollectionAmount().setQuantity(in.getCollectionVolume().getQuantity());
        out.getSpecimenCollectionAmount().setUnits(in.getCollectionVolume().getUnits());
        out.getSpecimenDescription().add(in.getSpecimenSource().getSpecimenCollectionMethod());
        out.getSpecimenCollectionDateTime().setRangeStartDateTime(in.getObservationDateTime());
        out.getSpecimenCollectionDateTime().setRangeEndDateTime(in.getObservationEndDateTime());
        out.setSpecimenReceivedDateTime(in.getSpecimenReceivedDateTime());
        out.setNumberOfSpecimenContainers(in.getNumberOfSampleContainers());

        return out;
    }

    public static Xad mapXad(Xad in, Xad out) {
        out.setStreetAddress(in.getStreetAddress());
        out.setOtherDesignation(in.getOtherDesignation());
        out.setCity(in.getCity());
        out.setState(in.getState());
        out.setZip(in.getZip());
        out.setCountry(in.getCountry());
        out.setAddressType(in.getAddressType());
        out.setOtherGeographic(in.getOtherGeographic());
        out.setCensusTract(in.getCensusTract());
        out.setAddressRepresentationCode(in.getAddressRepresentationCode());
        out.setCountyCode(in.getCountyCode());
        return out;
    }

    public static Xon mapCeToXon(Ce in, Xon out) {
        out.setOrganizationName(in.getText());
        out.setOrganizationIdentifier(in.getIdentifier());
        out.getAssignAuthority().setNameSpaceId(in.getNameOfCodingSystem());
        return out;
    }
}
