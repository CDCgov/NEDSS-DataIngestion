package gov.cdc.dataingestion.nbs.ecr.model.patient;

import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/// TO USE LATER FOR REFACTOR
public class CdaPatientField {
    public CdaPatientField(POCDMT000040ClinicalDocument1 clinicalDocument,
                           int phoneCounter)
    {
        this.phoneCounter = phoneCounter;
        this.clinicalDocument = clinicalDocument;
    }

    public CdaPatientField(int patientIdentifier,
                           String address1,
                           String address2,
                           int k,
                           int patientComponentCounter,
                           POCDMT000040ClinicalDocument1 clinicalDocument,
                           String workPhoneExt,
                           String homePhoneNumber,
                           String wpNumber,
                           String phoneCountryCode,
                           String cellNumber,
                           String ptPrefix,
                           String ptFirstName,
                           String ptMiddleName,
                           String ptLastName,
                           String ptSuffix,
                           String email,
                           String urlAddress,
                           String phoneAsDateTime,
                           String inv168) {
        this.patientIdentifier = patientIdentifier;
        this.address1 = address1;
        this.address2 = address2;
        this.k = k;
        this.patientComponentCounter = patientComponentCounter;
        this.clinicalDocument = clinicalDocument;
        this.workPhoneExt = workPhoneExt;
        this.homePhoneNumber = homePhoneNumber;
        this.wpNumber = wpNumber;
        this.phoneCountryCode = phoneCountryCode;
        this.cellNumber = cellNumber;
        this.ptPrefix = ptPrefix;
        this.ptFirstName = ptFirstName;
        this.ptMiddleName = ptMiddleName;
        this.ptLastName = ptLastName;
        this.ptSuffix = ptSuffix;
        this.email = email;
        this.urlAddress = urlAddress;
        this.phoneAsDateTime = phoneAsDateTime;
        this.inv168 = inv168;

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
