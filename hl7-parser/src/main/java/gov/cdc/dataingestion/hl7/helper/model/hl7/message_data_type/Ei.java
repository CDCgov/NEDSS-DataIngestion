package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public Ei() {

    }
}
