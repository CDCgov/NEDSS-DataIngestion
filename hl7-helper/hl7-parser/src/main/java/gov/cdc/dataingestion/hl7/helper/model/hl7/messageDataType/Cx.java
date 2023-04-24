package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.AssigningAuthOrFacility;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId.AssigningDeptOrAgency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cx {
    String idNumber;
    String checkDigit;
    String checkDigitScheme;
    String identifierTypeCode;
    String effectiveDate;
    String expirationDate;
    AssigningAuthOrFacility assignAuthority;
    AssigningAuthOrFacility assignFacility;
    AssigningDeptOrAgency assignJurisdiction;
    AssigningDeptOrAgency assignAgentOrDept;
}
