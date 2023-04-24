package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.DLN;

public class Dln {
    String licenseNumber;
    String issuedStateCountry;
    String expirationDate;
    public Dln(DLN dln) {
        licenseNumber = dln.getLicenseNumber().getValue();
        issuedStateCountry = dln.getIssuingStateProvinceCountry().getValue();
        expirationDate = dln.getExpirationDate().getValue();
    }
}
