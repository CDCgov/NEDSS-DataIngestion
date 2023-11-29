package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.CNE;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cne {
    String identifier;
    String text;
    String nameOfCodingSystem;
    String alternateIdentifier;
    String alternateText;
    String nameOfAlternateCodingSystem;
    String codingSystemVersionId;
    String alternateCodingSystemVersionId;
    String originalText;
    public Cne(CNE cne) {
        this.identifier = cne.getIdentifier().getValue();
        this.text = cne.getText().getValue();
        this.nameOfCodingSystem = cne.getNameOfCodingSystem().getValue();
        this.alternateIdentifier = cne.getAlternateIdentifier().getValue();
        this.alternateText = cne.getAlternateText().getValue();
        this.nameOfAlternateCodingSystem = cne.getNameOfAlternateCodingSystem().getValue();
        this.codingSystemVersionId = cne.getCodingSystemVersionID().getValue();
        this.alternateCodingSystemVersionId = cne.getAlternateCodingSystemVersionID().getValue();
        this.originalText = cne.getOriginalText().getValue();
    }

    public Cne() {

    }
}
