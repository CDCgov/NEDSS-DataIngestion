package gov.cdc.dataingestion.hl7.helper.model.hl7.messageType;

import gov.cdc.dataingestion.hl7.helper.model.hl7.container.PatientResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OruR1 {
    PatientResult patientResult;
}
