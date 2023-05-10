package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisit;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import lombok.Getter;

@Getter
public class Visit {
    PatientVisit patientVisit;
    PatientVisitAdditional patientVisitAdditional;

    public Visit(ca.uhn.hl7v2.model.v251.group.ORU_R01_VISIT oruR01Visit) {
        this.patientVisit = new PatientVisit(oruR01Visit.getPV1());
        this.patientVisitAdditional = new PatientVisitAdditional(oruR01Visit.getPV2());
    }
}
