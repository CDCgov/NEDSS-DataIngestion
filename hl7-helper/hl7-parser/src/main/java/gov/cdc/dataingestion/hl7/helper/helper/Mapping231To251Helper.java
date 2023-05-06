package gov.cdc.dataingestion.hl7.helper.helper;

import ca.uhn.hl7v2.model.v231.datatype.*;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PID;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
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
                if (patientIdentifier.getIdentifierTypeCode() == null ||
                        (patientIdentifier.getIdentifierTypeCode() != null && patientIdentifier.getIdentifierTypeCode().isEmpty())
                ) {
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

    //region Map ORC to ORC - 231 to 251
    public static CommonOrder MapCommonOrder(ORC inOrc231, CommonOrder outOrc251) {
        if (inOrc231.getOrderControl() != null && inOrc231.getOrderControl().getValue().isEmpty()) {
            outOrc251.setOrderControl("RE");
        } else {
            outOrc251.setOrderControl(inOrc231.getOrderControl().getValue());
        }

        if (inOrc231.getPlacerOrderNumber() != null) {
            outOrc251.setPlacerGroupNumber(MapEi231(inOrc231.getPlacerOrderNumber(), outOrc251.getPlacerGroupNumber()));
        }

        if (inOrc231.getFillerOrderNumber() != null) {
            outOrc251.setFillerOrderNumber(MapEi231(inOrc231.getFillerOrderNumber(), outOrc251.getFillerOrderNumber()));
        }

        if (inOrc231.getPlacerGroupNumber() != null) {
            outOrc251.setPlacerGroupNumber(MapEi231(inOrc231.getPlacerGroupNumber(), outOrc251.getPlacerGroupNumber()));
        }

        outOrc251.setOrderStatus(inOrc231.getOrderStatus().getValue());
        outOrc251.setResponseFlag(inOrc231.getResponseFlag().getValue());

        if (inOrc231.getQuantityTiming() != null) {
            Tq timeQty = MapTq231(inOrc231.getQuantityTiming(), new Tq());
            if(outOrc251.getQuantityTiming() != null) {
                outOrc251.getQuantityTiming().add(timeQty);
            } else {
                var quantityTimeList = new ArrayList<Tq>();
                quantityTimeList.add(timeQty);
                outOrc251.setQuantityTiming(quantityTimeList);
            }
        }

        if(inOrc231.getParentOrder() != null) {
            outOrc251.setParentOrder(MapEip231(inOrc231.getParentOrder(), outOrc251.getParentOrder()));
        }

        if(inOrc231.getDateTimeOfTransaction() != null) {
            outOrc251.setDateTimeOfTransaction(MapTs231(inOrc231.getDateTimeOfTransaction(), outOrc251.getDateTimeOfTransaction()));
        }

        List<Xcn> enterBys = new ArrayList<>();
        for(int a = 0; a < inOrc231.getEnteredBy().length; a++) {
            if(inOrc231.getEnteredBy()[a] != null) {
                enterBys.add(MapXcn231(inOrc231.getEnteredBy(a), new Xcn()));
            }
        }
        outOrc251.setEnteredBy(enterBys);

        List<Xcn> verifiedBys = new ArrayList<>();
        for (int b = 0; b < inOrc231.getVerifiedBy().length; b++) {
            if(inOrc231.getVerifiedBy()[b] != null) {
                verifiedBys.add(MapXcn231(inOrc231.getVerifiedBy()[b], new Xcn()));
            }
        }
        outOrc251.setVerifiedBy(verifiedBys);

        List<Xcn> orderProviders = new ArrayList<>();
        for(int c = 0; c < inOrc231.getOrderingProvider().length; c++) {
            if(inOrc231.getOrderingProvider()[c] != null) {
                orderProviders.add(MapXcn231(inOrc231.getOrderingProvider()[c], new Xcn()));
            }
        }
        outOrc251.setOrderingProvider(orderProviders);

        if(inOrc231.getEntererSLocation() != null) {
            outOrc251.setEntererLocation(MapPl231(inOrc231.getEntererSLocation(), outOrc251.getEntererLocation()));
        }

        List<Xtn> phoneList = new ArrayList<>();
        for(int d = 0; d < inOrc231.getCallBackPhoneNumber().length; d++) {
            if(inOrc231.getCallBackPhoneNumber()[d] != null) {
                phoneList.add(MapXtn231(inOrc231.getCallBackPhoneNumber(d), new Xtn()));
            }
        }
        outOrc251.setCallBackPhoneNumber(phoneList);

        if(inOrc231.getOrderEffectiveDateTime() != null) {
            outOrc251.setOrderEffectiveDateTime(MapTs231(inOrc231.getOrderEffectiveDateTime(), outOrc251.getOrderEffectiveDateTime()));
        }

        if(inOrc231.getOrderControlCodeReason() != null) {
            outOrc251.setOrderControlCodeReason(MapCe231(inOrc231.getOrderControlCodeReason(), outOrc251.getOrderControlCodeReason()));
        }

        if(inOrc231.getEnteringOrganization() != null) {
            outOrc251.setEnteringOrganization(MapCe231(inOrc231.getEnteringOrganization(), outOrc251.getEnteringOrganization()));
        }

        if(inOrc231.getEnteringDevice() != null) {
            outOrc251.setEnteringDevice(MapCe231(inOrc231.getEnteringDevice(), outOrc251.getEnteringDevice()));
        }

        List<Xcn> actionbyList = new ArrayList<>();
        for(int e = 0; e < inOrc231.getActionBy().length; e++) {
            if(inOrc231.getActionBy(e) != null) {
                actionbyList.add(MapXcn231(inOrc231.getActionBy(e), new Xcn()));
            }
        }
        outOrc251.setActionBy(actionbyList);

        if(inOrc231.getAdvancedBeneficiaryNoticeCode() != null) {
            outOrc251.setAdvancedBeneficiaryNoticeCode(MapCe231(inOrc231.getAdvancedBeneficiaryNoticeCode(), outOrc251.getAdvancedBeneficiaryNoticeCode()));
        }

        List<Xon> facilityList = new ArrayList<>();
        for(int f= 0; f < inOrc231.getOrderingFacilityName().length; f++) {
            if(inOrc231.getOrderingFacilityName(f) != null) {
                facilityList.add(MapXon231(inOrc231.getOrderingFacilityName(f), new Xon()));
            }
        }
        outOrc251.setOrderingFacilityName(facilityList);

        List<Xad> orderingFacilityList = new ArrayList<>();
        for(int g = 0; g < inOrc231.getOrderingFacilityAddress().length; g++) {
            if(inOrc231.getOrderingFacilityAddress(g) != null) {
                orderingFacilityList.add(MapXad231(inOrc231.getOrderingFacilityAddress(g), new Xad()));
            }
        }
        outOrc251.setOrderingFacilityAddress(orderingFacilityList);


        return outOrc251;
    }
    //endregion

    public static Xad MapXad231(XAD in, Xad out) {
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
        return out;

    }

    public static Xon MapXon231(XON in, Xon out) {
        out.setOrganizationName(in.getOrganizationName().getValue());
        out.setOrganizationNameTypeCode(in.getOrganizationNameTypeCode().getValue());
        out.setOrganizationIdentifier(in.getIDNumber().getValue());
        out.setCheckDigitScheme(in.getCodeIdentifyingTheCheckDigitSchemeEmployed().getValue());
        if(in.getAssigningAuthority() != null) {
            out.setAssignAuthority(MapHd231(in.getAssigningAuthority(), out.getAssignAuthority()));
        }
        out.setIdentifierTypeCode(in.getIdentifierTypeCode().getValue());
        out.setNameRepresentationCode(in.getNameRepresentationCode().getValue());
        if(in.getAssigningFacilityID() != null) {
            out.setAssignFacility(MapHd231(in.getAssigningFacilityID(), out.getAssignFacility()));
        }
        return out;
    }

    public static Xtn MapXtn231(XTN in, Xtn out) {
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

    public static Pl MapPl231(PL in, Pl out) {
        out.setPointOfCare(in.getPointOfCare().getValue());
        out.setRoom(in.getRoom().getValue());
        out.setBed(in.getBed().getValue());
        if(in.getFacility() != null) {
            out.setFacility(MapHd231(in.getFacility(), out.getFacility()));
        }
        out.setLocationStatus(in.getLocationStatus().getValue());
        out.setBuilding(in.getBuilding().getValue());
        out.setFloor(in.getFloor().getValue());
        out.setLocationDescription(in.getLocationDescription().getValue());
        out.setPersonLocationType(in.getPersonLocationType().getValue());
        return out;
    }

    public static Xcn MapXcn231(XCN in, Xcn out) {
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
            out.setAssignAuthority(MapHd231(in.getAssigningAuthority(), out.getAssignAuthority()));
        }

        out.setNameTypeCode(in.getNameTypeCode().getValue());
        out.setIdentifierCheckDigit(in.getIdentifierCheckDigit().getValue());
        out.setCheckDigitScheme(in.getCodeIdentifyingTheCheckDigitSchemeEmployed().getValue());
        out.setIdentifierTypeCode(in.getIdentifierTypeCode().getValue());

        if(in.getAssigningFacility() != null) {
            out.setAssignFacility(MapHd231(in.getAssigningFacility(), out.getAssignFacility()));
        }
        out.setNameRepresentationCode(in.getNameRepresentationCode().getValue());
        return out;
    }

    public static Hd MapHd231(HD in, Hd out) {
        out.setNameSpaceId(in.getNamespaceID().getValue());
        out.setUniversalId(in.getUniversalID().getValue());
        out.setUniversalIdType(in.getUniversalIDType().getValue());
        return out;
    }

    public static Eip MapEip231(EIP in, Eip out) {
        out.setPlacerAssignedIdentifier(MapEi231(in.getParentSPlacerOrderNumber(), out.getPlacerAssignedIdentifier()));
        out.setFillerAssignedIdentifier(MapEi231(in.getParentSFillerOrderNumber(), out.getFillerAssignedIdentifier()));
        return out;
    }

    public static Tq MapTq231(TQ in, Tq out) {

        if (in.getQuantity() != null) {
            out.setQuantity(MapCq231(in.getQuantity(), out.getQuantity()));
        }

        if (in.getInterval() != null) {
            out.setInterval(MapRi231(in.getInterval(), out.getInterval()));
        }

        out.setDuration(in.getDuration().getValue());

        if (in.getStartDateTime() != null) {
            out.setStartDateTime(MapTs231(in.getStartDateTime(), out.getStartDateTime()));
        }

        if (in.getEndDateTime() != null) {
            out.setEndDateTime(MapTs231(in.getEndDateTime(), out.getEndDateTime()));
        }

        out.setPriority(in.getPriority().getValue());
        out.setCondition(in.getCondition().getValue());
        out.setText(in.getText().getValue());
        out.setConjunction(in.getConjunction().getValue());

        if(in.getOrderSequencing() != null) {
            out.setOrderSequencing(MapOsd231(in.getOrderSequencing(), out.getOrderSequencing()));
        }

        if(in.getOccurrenceDuration() != null) {
            out.setOccurrenceDuration(MapCe231(in.getOccurrenceDuration(), out.getOccurrenceDuration()));
        }

        out.setTotalOccurrences(in.getTotalOccurences().getValue());
        return out;
    }

    public static Osd MapOsd231(OSD in, Osd out) {
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

    public static Ts MapTs231(TS in, Ts out) {
        out.setTime(in.getTimeOfAnEvent().getValue());
        out.setDegreeOfPrecision(in.getDegreeOfPrecision().getValue());
        return out;
    }

    public static Ri MapRi231(RI in, Ri out) {
        out.setRepeatPattern(in.getRepeatPattern().getValue());
        out.setExplicitTimeInterval(in.getExplicitTimeInterval().getValue());
        return out;
    }

    public static Cq MapCq231(CQ in, Cq out) {
        out.setQuantity(in.getQuantity().getValue());
        out.setUnits(MapCe231(in.getUnits(), out.getUnits()));
        return out;
    }

    public static Ce MapCe231(CE in, Ce out) {
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

    public static Ei MapEi231(EI in, Ei out) {
        out.setEntityIdentifier(in.getEntityIdentifier().getValue());
        out.setNameSpaceId(in.getNamespaceID().getValue());
        out.setUniversalId(in.getUniversalID().getValue());
        out.setUniversalIdType(in.getUniversalIDType().getValue());
        return out;
    }

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


        if (cx.getAssignAuthority() != null && cx.getAssignAuthority().getNameSpaceId() != null && !cx.getAssignAuthority().getNameSpaceId().isEmpty()) {
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

            if(cx.getAssignFacility() != null && cx.getAssignFacility().getNameSpaceId() != null &&  !cx.getAssignFacility().getNameSpaceId().isEmpty()) {
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
        } else  if (cx.getAssignFacility() != null && cx.getAssignFacility().getNameSpaceId() != null &&  !cx.getAssignFacility().getNameSpaceId().isEmpty()) {
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
                (obxIn.getPerformingOrganizationAddress().getCity() != null &&
            obxIn.getPerformingOrganizationAddress().getCity().isEmpty() )||
                (obxIn.getPerformingOrganizationAddress().getState() != null &&
            obxIn.getPerformingOrganizationAddress().getState().isEmpty()) ||
                (obxIn.getPerformingOrganizationAddress().getCountry() != null &&
            obxIn.getPerformingOrganizationAddress().getCountry().isEmpty()) ||
                (obxIn.getPerformingOrganizationAddress().getZip() != null &&
            obxIn.getPerformingOrganizationAddress().getZip().isEmpty())
        ) {
            obxOut.getPerformingOrganizationAddress().getStreetAddress().setStreetMailingAddress("Not present in v2.3.1 message");
        }

        return obxOut;
    }

    public static ObservationRequest ObservationRequestToObservationRequest(ObservationRequest in, ObservationRequest out, Ts timestamp) {
        if(in.getResultRptStatusChngDateTime() == null ||
                (in.getResultRptStatusChngDateTime() != null &&  in.getResultRptStatusChngDateTime().getTime() != null && in.getResultRptStatusChngDateTime().getTime().isEmpty())
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
