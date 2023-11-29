package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.CQ;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cq {
    String quantity;
    Ce units = new Ce();
    public Cq(CQ cq) {
        this.quantity = cq.getQuantity().getValue();
        this.units = new Ce(cq.getUnits());
    }

    public Cq() {

    }
}
