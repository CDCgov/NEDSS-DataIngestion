package gov.cdc.dataingestion.hl7.helper.helper;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;

public class Mapping231To251Helper {
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
