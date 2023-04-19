package gov.cdc.dataingestion.hl7.helper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientName {

    // PID.5.1
    private String familyName;

    // PID.5.2
    private String givenName;

    // PID.5.3
    private String furtherGivenName;

    // PID.5.4
    private String suffix;

    // PID.5.5
    private String prefix;

    // PID.5.6
    private String degree;

    //..MORE

}
