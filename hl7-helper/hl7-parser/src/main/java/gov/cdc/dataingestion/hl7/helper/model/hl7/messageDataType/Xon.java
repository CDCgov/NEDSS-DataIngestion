package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.XON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Xon {
    String organizationName;
    String organizationNameTypeCode;
    String idNumber;
    String checkDigit;
    String checkDigitScheme;
    Hd assignAuthority;
    String identifierTypeCode;
    Hd assignFacility;
    String nameRepresentationCode;
    String organizationIdentifier;
    public Xon(XON xon) {
        this.organizationName = xon.getOrganizationName().getValue();
        this.organizationNameTypeCode = xon.getOrganizationNameTypeCode().getValue();
        this.idNumber = xon.getIDNumber().getValue();
        this.checkDigit = xon.getCheckDigit().getValue();
        this.checkDigitScheme = xon.getCheckDigitScheme().getValue();
        this.assignAuthority = new Hd(xon.getAssigningAuthority());
        this.identifierTypeCode = xon.getIdentifierTypeCode().getValue();
        this.assignFacility = new Hd(xon.getAssigningFacility());
        this.nameRepresentationCode = xon.getNameRepresentationCode().getValue();
        this.organizationIdentifier = xon.getOrganizationIdentifier().getValue();
    }

    public Xon() {

    }
}
