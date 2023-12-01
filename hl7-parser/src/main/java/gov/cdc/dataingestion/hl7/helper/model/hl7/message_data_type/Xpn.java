package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Xpn {
    Fn familyName = new Fn();
    String givenName;
    String secondAndFurtherGivenNameOrInitial;
    String suffix;
    String prefix;
    String degree;
    String nameTypeCode;
    String nameRepresentationCode;
    Ce nameContext = new Ce();
    Dr nameValidityRange = new Dr();
    String nameAssemblyOrder;
    Ts effectiveDate = new Ts();
    Ts expirationDate = new Ts();
    String professionalSuffix;

    public Xpn(ca.uhn.hl7v2.model.v251.datatype.XPN xpn) {
        this.familyName = new Fn(xpn.getFamilyName());
        this.givenName = xpn.getGivenName().getValue();
        this.secondAndFurtherGivenNameOrInitial = xpn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
        this.suffix = xpn.getSuffixEgJRorIII().getValue();
        this.prefix = xpn.getPrefixEgDR().getValue();
        this.degree = xpn.getDegreeEgMD().getValue();
        this.nameTypeCode = xpn.getNameTypeCode().getValue();
        this.nameRepresentationCode = xpn.getNameRepresentationCode().getValue();
        this.nameContext = new Ce(xpn.getNameContext());
        this.nameValidityRange = new Dr(xpn.getNameValidityRange());
        this.nameAssemblyOrder = xpn.getNameAssemblyOrder().getValue();
        this.effectiveDate = new Ts(xpn.getEffectiveDate());
        this.expirationDate = new Ts(xpn.getExpirationDate());
        this.professionalSuffix = xpn.getProfessionalSuffix().getValue();
    }
    public Xpn() {

    }
}
