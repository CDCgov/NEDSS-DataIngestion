package gov.cdc.dataingestion.hl7.helper.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientAddress {

    // PID-11-1-1
    private String streetAddress;

    // PID-11-1-2
    private String streetName;

    // PID-11-1-3
    private String dwellingNumber;

    // PID-11-2
    private String otherDesignation;

    // PID-11-3
    private String city;

    // PID-11-4
    private String state;

    // PID-11-5
    private String zipCode;

    // PID-11-6
    private String country;

    // PID-11-7
    private String addressType;

    // ..more

}
