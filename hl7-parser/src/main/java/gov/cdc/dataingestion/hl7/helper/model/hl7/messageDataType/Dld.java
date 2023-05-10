package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Dld {
    String dischargeLocation;
    Ts effectiveDate;
    public Dld(ca.uhn.hl7v2.model.v251.datatype.DLD dld) {
        this.dischargeLocation = dld.getDischargeLocation().getValue();
        this.effectiveDate = new Ts(dld.getEffectiveDate());
    }
}
