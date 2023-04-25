package gov.cdc.dataingestion.hl7.helper.model.hl7.messageType;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.PatientResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OruR1 {
    PatientResult patientResult;

    public OruR1(ca.uhn.hl7v2.model.v251.message.ORU_R01 oruR01) throws HL7Exception {
        this.patientResult = new PatientResult(oruR01.getPATIENT_RESULT());
    }
}
