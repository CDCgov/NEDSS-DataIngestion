package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dr {
    Ts rangeStartDateTime = new Ts();
    Ts rangeEndDateTime = new Ts();

    public Dr(ca.uhn.hl7v2.model.v251.datatype.DR dr) {
        this.rangeStartDateTime = new Ts(dr.getRangeStartDateTime());
        this.rangeEndDateTime = new Ts(dr.getRangeEndDateTime());
    }

    public Dr() {

    }
}
