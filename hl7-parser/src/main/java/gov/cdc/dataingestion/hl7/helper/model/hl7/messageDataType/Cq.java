package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.CQ;
import lombok.Getter;

@Getter
public class Cq {
    String quantity;
    Ce units;
    public Cq(CQ cq) {
        this.quantity = cq.getQuantity().getValue();
        this.units = new Ce(cq.getUnits());
    }
}
