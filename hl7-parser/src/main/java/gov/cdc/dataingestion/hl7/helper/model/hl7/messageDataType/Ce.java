package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ce {
    String identifier;
    String text;
    String nameOfCodingSystem;
    String alternateIdentifier;
    String alternateText;
    String nameOfAlternateCodingSystem;

    public Ce(ca.uhn.hl7v2.model.v251.datatype.CE ce) {
        this.identifier = ce.getIdentifier().getValue();
        this.text = ce.getText().getValue();
        this.nameOfCodingSystem = ce.getNameOfCodingSystem().getValue();
        this.alternateIdentifier = ce.getAlternateIdentifier().getValue();
        this.alternateText = ce.getAlternateText().getValue();
        this.nameOfAlternateCodingSystem = ce.getNameOfAlternateCodingSystem().getValue();
    }

    public Ce(){

    }
}
