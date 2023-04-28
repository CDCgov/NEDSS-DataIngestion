package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientAdditionalDemographic;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Patient {
    PatientIdentification patientIdentification;
    PatientAdditionalDemographic patientAdditionalDemographic;
    List<NoteAndComment> noteAndComment;
    List<NextOfKin> nextOfKin;
    Visit visit;

    public Patient(ca.uhn.hl7v2.model.v251.group.ORU_R01_PATIENT oruR01Patient) throws HL7Exception {
        this.patientIdentification = new PatientIdentification(oruR01Patient.getPID());
        this.patientAdditionalDemographic = new PatientAdditionalDemographic(oruR01Patient.getPD1());

        this.noteAndComment = new ArrayList<>();
        for(var msg: oruR01Patient.getNTEAll()) {
            this.noteAndComment.add(new NoteAndComment(msg));
        }

        this.nextOfKin = new ArrayList<>();
        for(var msg: oruR01Patient.getNK1All()) {
            this.nextOfKin.add(new NextOfKin(msg));

        }
        this.visit = new Visit(oruR01Patient.getVISIT());

    }
}
