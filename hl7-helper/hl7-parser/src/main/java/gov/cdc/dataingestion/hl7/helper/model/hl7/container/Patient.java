package gov.cdc.dataingestion.hl7.helper.model.hl7.container;

import gov.cdc.dataingestion.hl7.helper.model.hl7.patient.NextOfKin;
import gov.cdc.dataingestion.hl7.helper.model.hl7.patient.NoteAndComment;
import gov.cdc.dataingestion.hl7.helper.model.hl7.patient.PatientAdditionalDemographic;
import gov.cdc.dataingestion.hl7.helper.model.hl7.patient.PatientIdentification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Patient {
    PatientIdentification patientIdentification;
    PatientAdditionalDemographic patientAdditionalDemographic;
    NoteAndComment noteAndComment;
    NextOfKin nextOfKin;
}
