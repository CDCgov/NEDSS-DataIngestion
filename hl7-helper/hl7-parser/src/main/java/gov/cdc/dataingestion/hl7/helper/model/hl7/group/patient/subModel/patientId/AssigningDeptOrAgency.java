package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cwe;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssigningDeptOrAgency extends Cwe {

    public AssigningDeptOrAgency(ca.uhn.hl7v2.model.v251.datatype.CWE data) {
        this.setIdentifier(data.getIdentifier().getValue());
        this.setText(data.getText().getValue());
        this.setNameOfCodingSystem(data.getNameOfCodingSystem().getValue());
        this.setAlternateIdentifier(data.getAlternateIdentifier().getValue());
        this.setAlternateText(data.getAlternateText().getValue());
        this.setNameOfAlterCodeSystem(data.getNameOfAlternateCodingSystem().getValue());
        this.setCodeSystemVerId(data.getCodingSystemVersionID().getValue());
        this.setAlterCodeSystemVerId(data.getAlternateCodingSystemVersionID().getValue());
        this.setOriginalText(data.getOriginalText().getValue());
    }
}
