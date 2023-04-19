package gov.cdc.dataingestion.hl7.helper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HL7ParsedMessage {
    public HL7ParsedMessage() {

    }

    public HL7ParsedMessage(String version, String type, String eventTrigger, String message) {
        this.version = version;
        this.type = type;
        this.eventTrigger = eventTrigger;
        this.message = message;
    }
    private String version;
    private String type;
    private String eventTrigger;
    private String message;

    private PatientIdentification patientIdentification;
}
