package gov.cdc.dataingestion.hl7.helper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirConvertedMessage {
    String hl7Message;
    String fhirMessage;
}
