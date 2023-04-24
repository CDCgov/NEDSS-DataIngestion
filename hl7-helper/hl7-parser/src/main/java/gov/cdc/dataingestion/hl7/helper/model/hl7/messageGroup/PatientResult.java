package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResult {
    Patient patient;
    public PatientResult(ca.uhn.hl7v2.model.v251.group.ORU_R01_PATIENT_RESULT oruR01PatientResult) {
        this.patient = new Patient(oruR01PatientResult.getPATIENT());
    }
}
