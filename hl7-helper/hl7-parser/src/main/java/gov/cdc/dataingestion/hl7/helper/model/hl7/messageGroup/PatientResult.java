package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import ca.uhn.hl7v2.HL7Exception;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatientResult {
    Patient patient;
    List<OrderObservation> orderObservation;
    public PatientResult(ca.uhn.hl7v2.model.v251.group.ORU_R01_PATIENT_RESULT oruR01PatientResult) throws HL7Exception {
        this.patient = new Patient(oruR01PatientResult.getPATIENT());
        this.orderObservation = new ArrayList<>();
        for(var item : oruR01PatientResult.getORDER_OBSERVATIONAll()) {
            orderObservation.add(new OrderObservation(item));
        }
    }
}
