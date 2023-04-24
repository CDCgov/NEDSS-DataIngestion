package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Fn;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xpn;

public class PatientName extends Xpn {
    public PatientName(ca.uhn.hl7v2.model.v251.datatype.XPN patientName) {
        Fn familyName = new Fn();
        familyName.setSurname(patientName.getFamilyName().getSurname().getValue());
        familyName.setOwnSurnamePrefix(patientName.getFamilyName().getOwnSurnamePrefix().getValue());
        familyName.setOwnSurname(patientName.getFamilyName().getOwnSurname().getValue());
        familyName.setSurnamePrefixFromPartner(patientName.getFamilyName().getSurnamePrefixFromPartnerSpouse().getValue());
        familyName.setSurnameFromPartner(patientName.getFamilyName().getSurnameFromPartnerSpouse().getValue());
        setFamilyName(familyName);
        setGivenName(patientName.getGivenName().getValue());
        setSecondAndFurtherGivenNameOrInitial(patientName.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue());
        setSuffix(patientName.getSuffixEgJRorIII().getValue());
        setPrefix(patientName.getPrefixEgDR().getValue());
        setDegree(patientName.getDegreeEgMD().getValue());
        setNameTypeCode(patientName.getNameTypeCode().getValue());
        setNameRepresentationCode(patientName.getNameRepresentationCode().getValue());
      //  setNameContext(patientName.getNameContext().getValue());

        NameContext nameContext = new NameContext(patientName.getNameContext().getMessage());
        setNameContext(nameContext);

        nameContext.getIdentifier().
    }
}
