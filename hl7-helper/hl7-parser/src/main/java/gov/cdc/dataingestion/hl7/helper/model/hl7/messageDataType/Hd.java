package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Hd {
    String nameSpaceId;
    String universalId;
    String universalIdType;

    public Hd(ca.uhn.hl7v2.model.v251.datatype.HD hd) {
        this.nameSpaceId = hd.getNamespaceID().getValue();
        this.universalId = hd.getUniversalID().getValue();
        this.universalIdType = hd.getUniversalIDType().getValue();
    }
}
