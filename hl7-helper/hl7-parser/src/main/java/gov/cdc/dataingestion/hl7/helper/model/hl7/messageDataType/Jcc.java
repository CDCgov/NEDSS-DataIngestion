package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.JCC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jcc {
    String jobCode;
    String jobClass;
    String jobDescriptionText;
    public Jcc(JCC jcc) {
        this.jobCode = jcc.getJobCode().getValue();
        this.jobClass = jcc.getJobClass().getValue();
        this.jobDescriptionText = jcc.getJobDescriptionText().getValue();
    }
}
