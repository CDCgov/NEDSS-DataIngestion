package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.PatientId;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.PatientIdentifierList;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.PatientName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatientIdentification {
    // Set ID - PID
    String setPid;
    PatientId patientId;
    List<PatientIdentifierList> patientIdentifierList;
    List<PatientIdentifierList> alternativePatientId;
    List<PatientName> patientName;
    public PatientIdentification(ca.uhn.hl7v2.model.v251.segment.PID patientIdentification) {
        this.setPid = patientIdentification.getSetIDPID().getValue();
        this.patientId = new PatientId(patientIdentification.getPatientID());

        patientIdentifierList = new ArrayList<>();
        for(var data:  patientIdentification.getPatientIdentifierList()) {
            PatientIdentifierList item = new PatientIdentifierList(data);
            patientIdentifierList.add(item);
        }

        alternativePatientId = new ArrayList<>();
        for(var data:  patientIdentification.getAlternatePatientIDPID()) {
            PatientIdentifierList item = new PatientIdentifierList(data);
            alternativePatientId.add(item);
        }

        var test = patientIdentification.getPatientName();

        this.patientName = new ArrayList<>();
        for(var data: patientIdentification.getPatientName()) {
            PatientName item = new PatientName(data);
            this.patientName.add(item);
        }
    }


}
