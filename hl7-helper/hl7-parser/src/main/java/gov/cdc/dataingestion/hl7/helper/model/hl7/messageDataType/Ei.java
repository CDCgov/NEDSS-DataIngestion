package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Ei {
    String entityIdentifier;
    String nameSpaceId;
    String universalId;
    String universalIdType;
    public Ei(ca.uhn.hl7v2.model.v251.datatype.EI ei) {
        this.entityIdentifier = ei.getEntityIdentifier().getValue();
        this.nameSpaceId = ei.getNamespaceID().getValue();
        this.universalId = ei.getUniversalID().getValue();
        this.universalIdType = ei.getUniversalIDType().getValue();
    }
}
