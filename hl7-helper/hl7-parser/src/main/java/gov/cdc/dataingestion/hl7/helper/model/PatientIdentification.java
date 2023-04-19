package gov.cdc.dataingestion.hl7.helper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientIdentification {

    // pid.1
    private String pId;

    // pid.2
    private String id;

    // pid.5
    private PatientName patientName;

    // pid.6
    private PatientName motherMaidenName;

    // pid.7
    private String dateTimeOfBirth;

    private PatientAddress patientAddress;

    //..MORE
}
