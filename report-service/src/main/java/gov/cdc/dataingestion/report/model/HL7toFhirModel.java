package gov.cdc.dataingestion.report.model;

public class HL7toFhirModel {
    public String getRawHL7Message() {
        return rawHL7Message;
    }

    public void setRawHL7Message(String rawHL7Message) {
        this.rawHL7Message = rawHL7Message;
    }

    public String getConvertedFhirMessage() {
        return convertedFhirMessage;
    }

    public void setConvertedFhirMessage(String convertedFhirMessage) {
        this.convertedFhirMessage = convertedFhirMessage;
    }

    private String rawHL7Message;
    private String convertedFhirMessage;
}
