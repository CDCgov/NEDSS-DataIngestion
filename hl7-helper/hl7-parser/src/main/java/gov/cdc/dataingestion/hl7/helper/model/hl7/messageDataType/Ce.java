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
}
