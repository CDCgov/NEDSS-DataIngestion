package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.RI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ri {
    String repeatPattern;
    String explicitTimeInterval;
    public Ri(RI ri) {
        this.repeatPattern = ri.getRepeatPattern().getValue();
        this.explicitTimeInterval = ri.getExplicitTimeInterval().getValue();
    }
}
