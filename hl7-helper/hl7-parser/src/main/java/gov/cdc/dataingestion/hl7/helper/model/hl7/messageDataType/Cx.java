package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Cx {
    String idNumber;
    String checkDigit;
    String checkDigitScheme;
    String identifierTypeCode;
    String effectiveDate;
    String expirationDate;
    Hd assignAuthority;
    Hd assignFacility;
    Cwe assignJurisdiction;
    Cwe assignAgentOrDept;

    public Cx(ca.uhn.hl7v2.model.v251.datatype.CX cx) {
        this.idNumber = cx.getIDNumber().getValue();
        this.checkDigit = cx.getCheckDigit().getValue();
        this.checkDigitScheme = cx.getCheckDigitScheme().getValue();
        this.identifierTypeCode = cx.getIdentifierTypeCode().getValue();
        this.effectiveDate = cx.getEffectiveDate().getValue();
        this.expirationDate = cx.getExpirationDate().getValue();
        this.assignAuthority = new Hd(cx.getAssigningAuthority());
        this.assignFacility = new Hd(cx.getAssigningAuthority());
        this.assignJurisdiction = new Cwe(cx.getAssigningJurisdiction());
        this.assignAgentOrDept = new Cwe(cx.getAssigningAgencyOrDepartment());
    }
}
