package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.EIP;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Eip {
    Ei placerAssignedIdentifier = new Ei();
    Ei fillerAssignedIdentifier = new Ei();
    public Eip(EIP eip) {
        this.placerAssignedIdentifier = new Ei(eip.getPlacerAssignedIdentifier());
        this.fillerAssignedIdentifier = new Ei(eip.getFillerAssignedIdentifier());
    }

    public Eip() {

    }
}
