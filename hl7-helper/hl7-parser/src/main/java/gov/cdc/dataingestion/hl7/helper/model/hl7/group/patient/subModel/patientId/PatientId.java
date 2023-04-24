package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cx;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientId extends Cx {
    public PatientId(ca.uhn.hl7v2.model.v251.datatype.CX patientId) {
        this.setIdNumber(patientId.getIDNumber().getValue());
        this.setCheckDigit(patientId.getCheckDigit().getValue());
        this.setCheckDigitScheme(patientId.getCheckDigitScheme().getValue());
        this.setIdentifierTypeCode(patientId.getIdentifierTypeCode().getValue());
        this.setEffectiveDate(patientId.getEffectiveDate().getValue());
        this.setExpirationDate(patientId.getExpirationDate().getValue());
        this.setAssignAuthority(new AssigningAuthOrFacility(patientId.getAssigningAuthority()));
        this.setAssignFacility(new AssigningAuthOrFacility(patientId.getAssigningFacility()));
        this.setAssignJurisdiction(new AssigningDeptOrAgency(patientId.getAssigningJurisdiction()));
        this.setAssignAgentOrDept(new AssigningDeptOrAgency(patientId.getAssigningAgencyOrDepartment()));
    }

}