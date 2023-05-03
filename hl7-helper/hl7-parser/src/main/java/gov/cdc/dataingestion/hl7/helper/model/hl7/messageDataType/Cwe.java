package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cwe {
    String identifier;
    String text;
    String nameOfCodingSystem;
    String alternateIdentifier;
    String alternateText;
    String nameOfAlterCodeSystem;
    String codeSystemVerId;
    String alterCodeSystemVerId;
    String originalText;

    public Cwe(ca.uhn.hl7v2.model.v251.datatype.CWE cwe) {
        identifier = cwe.getIdentifier().getValue();
        text = cwe.getText().getValue();
        nameOfCodingSystem = cwe.getNameOfCodingSystem().getValue();
        alternateIdentifier = cwe.getAlternateIdentifier().getValue();
        alternateText = cwe.getAlternateText().getValue();
        nameOfAlterCodeSystem = cwe.getNameOfAlternateCodingSystem().getValue();
        codeSystemVerId = cwe.getCodingSystemVersionID().getValue();
        alterCodeSystemVerId = cwe.getAlternateCodingSystemVersionID().getValue();
        originalText = cwe.getOriginalText().getValue();
    }
}
