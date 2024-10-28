package gov.cdc.dataingestion.nbs.ecr.model.patient;

import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/// TO USE LATER FOR REFACTOR
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class CdaPatientField {
    public CdaPatientField(POCDMT000040ClinicalDocument1 clinicalDocument,
                           int phoneCounter)
    {
        this.phoneCounter = phoneCounter;
        this.clinicalDocument = clinicalDocument;
    }

    public CdaPatientField()
    {
        // Default
    }

    private int patientIdentifier;
    private String address1;
    private String address2;
    private int k;
    private int patientComponentCounter;

    private POCDMT000040ClinicalDocument1 clinicalDocument;

    private String workPhoneExt;
    private String homePhoneNumber;
    private String wpNumber;
    private String phoneCountryCode;
    private String cellNumber;
    private String ptPrefix;
    private String ptFirstName;
    private String ptMiddleName;
    private String ptLastName;
    private String ptSuffix;

    private String email;
    private String urlAddress;
    private String phoneAsDateTime;

    private String inv168;

    private int phoneCounter;


}
