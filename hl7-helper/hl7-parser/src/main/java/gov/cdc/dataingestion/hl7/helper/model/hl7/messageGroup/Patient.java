package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NoteAndComment;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientAdditionalDemographic;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Patient {
    PatientIdentification patientIdentification;
    PatientAdditionalDemographic patientAdditionalDemographic;
    NoteAndComment noteAndComment;
    NextOfKin nextOfKin;

    public Patient(ca.uhn.hl7v2.model.v251.group.ORU_R01_PATIENT oruR01Patient) {
        this.patientIdentification = new PatientIdentification(oruR01Patient.getPID());
    }
}
