package gov.cdc.dataingestion.hl7.helper.helper;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cx;

public class Mapping231To251Helper {
    public static Cx MapCxWithNullToCx (Cx cx, String defaultValue) {
        if(cx.getIdentifierTypeCode() == null || cx.getIdentifierTypeCode().isEmpty()) {
            cx.setIdentifierTypeCode(defaultValue);
        }

        if (cx.getAssignAuthority() != null && !cx.getAssignAuthority().getNameSpaceId().isEmpty()) {
            if(cx.getAssignAuthority().getUniversalId() == null || cx.getAssignAuthority().getUniversalId().isEmpty()) {
                cx.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            }

            if (cx.getAssignAuthority().getUniversalIdType() == null || cx.getAssignAuthority().getUniversalIdType().isEmpty()) {
                cx.getAssignAuthority().setUniversalIdType("NI");
            }

            if(cx.getAssignFacility() != null && cx.getAssignFacility().getNameSpaceId().isEmpty()) {
                if(cx.getAssignFacility().getUniversalId() == null || cx.getAssignFacility().getUniversalId().isEmpty()) {
                    cx.getAssignFacility().setUniversalId("2.16.840.1.113883.5.1008");
                }
                if(cx.getAssignFacility().getUniversalIdType() == null || cx.getAssignFacility().getUniversalIdType().isEmpty()) {
                    cx.getAssignFacility().setUniversalIdType("NI");
                }
            }
        } else  if (cx.getAssignFacility() != null && !cx.getAssignFacility().getNameSpaceId().isEmpty()) {
            if(cx.getAssignFacility().getUniversalId() == null || cx.getAssignFacility().getUniversalId().isEmpty()) {
                cx.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            }

            if(cx.getAssignFacility().getUniversalIdType() == null || cx.getAssignFacility().getUniversalIdType().isEmpty()) {
                cx.getAssignFacility().setUniversalIdType("NI");
            }
        } else  {
            cx.getAssignAuthority().setNameSpaceId("");
            cx.getAssignAuthority().setUniversalId("2.16.840.1.113883.5.1008");
            cx.getAssignAuthority().setUniversalIdType("NI");
        }

        return cx;
    }
}
