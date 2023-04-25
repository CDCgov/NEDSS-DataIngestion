package gov.cdc.dataingestion.hl7.helper.model.hl7.messageType;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.PatientResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OruR1 {
    List<PatientResult> patientResult;

    public OruR1(ca.uhn.hl7v2.model.v251.message.ORU_R01 oruR01) throws HL7Exception {

        this.patientResult = new ArrayList<>();
        for(var item : oruR01.getPATIENT_RESULTAll()) {
            this.patientResult.add(new PatientResult(item));
        }

    }
}
