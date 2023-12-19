package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dld {
    String dischargeLocation;
    Ts effectiveDate = new Ts();
    public Dld(ca.uhn.hl7v2.model.v251.datatype.DLD dld) {
        this.dischargeLocation = dld.getDischargeLocation().getValue();
        this.effectiveDate = new Ts(dld.getEffectiveDate());
    }

    public Dld() {

    }
}
