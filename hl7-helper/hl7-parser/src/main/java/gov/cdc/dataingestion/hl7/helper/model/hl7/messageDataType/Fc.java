package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Fc {
    String financialClassCode;
    Ts effectiveDate;
    public Fc(ca.uhn.hl7v2.model.v251.datatype.FC fc) {
        this.financialClassCode = fc.getFinancialClassCode().getValue();
        this.effectiveDate = new Ts(fc.getEffectiveDate());
    }
}
