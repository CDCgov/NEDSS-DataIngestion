package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.NameContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Xpn {
    Fn familyName;
    String givenName;
    String secondAndFurtherGivenNameOrInitial;
    String suffix;
    String prefix;
    String degree;
    String nameTypeCode;
    String nameRepresentationCode;
    NameContext nameContext;
    String nameValidityRange;
    String nameAssemblyOrder;
    String effectiveDate;
    String expirationDate;
    String professionalSuffix;
}
