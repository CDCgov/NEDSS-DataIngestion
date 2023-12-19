package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fc {
    String financialClassCode;
    Ts effectiveDate = new Ts();
    public Fc(ca.uhn.hl7v2.model.v251.datatype.FC fc) {
        this.financialClassCode = fc.getFinancialClassCode().getValue();
        this.effectiveDate = new Ts(fc.getEffectiveDate());
    }

    public Fc() {

    }
}
