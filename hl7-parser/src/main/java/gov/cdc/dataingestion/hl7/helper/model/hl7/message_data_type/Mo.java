package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.MO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mo {
    String quantity;
    String denomination;
    public Mo(MO mo) {
        this.quantity = mo.getQuantity().getValue();
        this.denomination = mo.getDenomination().getValue();
    }

    public Mo() {

    }
}
