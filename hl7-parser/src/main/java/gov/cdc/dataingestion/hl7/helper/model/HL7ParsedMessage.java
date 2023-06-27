package gov.cdc.dataingestion.hl7.helper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HL7ParsedMessage<T> {
    public HL7ParsedMessage() {
        // Default constructor
    }

    private String originalVersion;
    private String type;
    private String eventTrigger;
    private String message;
    private T parsedMessage;

}
