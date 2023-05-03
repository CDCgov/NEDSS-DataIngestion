package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ts {
    public String time;
    public String degreeOfPrecision;

    public Ts(ca.uhn.hl7v2.model.v251.datatype.TS ts) {
        this.time = ts.getTime().getValue();
        this.degreeOfPrecision = ts.getDegreeOfPrecision().getValue();
    }
}
