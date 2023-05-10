package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.XCN;
import lombok.Getter;

@Getter
public class Xcn {
    String idNumber;
    Fn familyName;
    String givenName;
    String secondAndFurtherGivenNameOrInitial;
    String suffix;
    String prefix;
    String degree;
    String sourceTable;
    Hd assignAuthority;
    String nameTypeCode;
    String identifierCheckDigit;
    String checkDigitScheme;
    String identifierTypeCode;
    Hd assignFacility;
    String nameRepresentationCode;
    Ce nameContext;
    Dr nameValidityRange;
    String nameAssemblyOrder;
    Ts effectiveDate;
    Ts expirationDate;
    String professionalSuffix;
    Cwe assignJurisdiction;
    Cwe assignAgencyDept;

    public Xcn(XCN xcn) {
        this.idNumber = xcn.getIDNumber().getValue();
        this.familyName = new Fn(xcn.getFamilyName());
        this.givenName = xcn.getGivenName().getValue();
        this.secondAndFurtherGivenNameOrInitial = xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
        this.suffix = xcn.getSuffixEgJRorIII().getValue();
        this.prefix = xcn.getPrefixEgDR().getValue();
        this.degree = xcn.getDegreeEgMD().getValue();
        this.sourceTable = xcn.getSourceTable().getValue();
        this.assignAuthority = new Hd(xcn.getAssigningAuthority());
        this.nameTypeCode = xcn.getNameTypeCode().getValue();
        this.identifierCheckDigit = xcn.getIdentifierCheckDigit().getValue();
        this.checkDigitScheme = xcn.getCheckDigitScheme().getValue();
        this.identifierTypeCode = xcn.getIdentifierTypeCode().getValue();
        this.assignFacility = new Hd(xcn.getAssigningFacility());
        this.nameRepresentationCode = xcn.getNameRepresentationCode().getValue();
        this.nameContext = new Ce(xcn.getNameContext());
        this.nameValidityRange = new Dr(xcn.getNameValidityRange());
        this.nameAssemblyOrder = xcn.getNameAssemblyOrder().getValue();
        this.effectiveDate = new Ts(xcn.getEffectiveDate());
        this.expirationDate = new Ts(xcn.getExpirationDate());
        this.professionalSuffix = xcn.getProfessionalSuffix().getValue();
        this.assignJurisdiction = new Cwe(xcn.getAssigningJurisdiction());
        this.assignAgencyDept = new Cwe(xcn.getAssigningAgencyOrDepartment());
    }
}
