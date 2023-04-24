package gov.cdc.dataingestion.hl7.helper.model.hl7.patient.subModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientId {
    String idNumber;
    String checkDigit;
    String checkDigitScheme;

}