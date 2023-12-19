package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cp {
    Mo price = new Mo();
    String priceType;
    String fromValue;
    String toValue;
    Ce rangeUnits = new Ce();
    String rangeType;
    public Cp(ca.uhn.hl7v2.model.v251.datatype.CP cp) {
        this.price = new Mo(cp.getPrice());
        this.priceType = cp.getPriceType().getValue();
        this.fromValue = cp.getFromValue().getValue();
        this.toValue = cp.getToValue().getValue();
        this.rangeUnits = new Ce(cp.getRangeUnits());
        this.rangeType = cp.getRangeType().getValue();
    }

    public Cp() {

    }
}
