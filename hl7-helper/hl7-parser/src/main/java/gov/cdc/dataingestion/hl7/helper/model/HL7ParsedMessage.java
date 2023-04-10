package gov.cdc.dataingestion.hl7.helper.model;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventTrigger() {
        return eventTrigger;
    }

    public void setEventTrigger(String eventTrigger) {
        this.eventTrigger = eventTrigger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PatientIdentification getPatientIdentification() {
        return patientIdentification;
    }

    public void setPatientIdentification(PatientIdentification patientIdentification) {
        this.patientIdentification = patientIdentification;
    }
}
