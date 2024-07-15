package gov.cdc.dataingestion.hl7.helper.unitTest.model;


import gov.cdc.dataingestion.hl7.helper.model.FhirConvertedMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FhirConvertedMessageTest {

    @Test
    public void testGettersAndSetters() {
        FhirConvertedMessage message = new FhirConvertedMessage();
        message.setHl7Message("HL7 test message");
        message.setFhirMessage("FHIR test message");

        assertEquals("HL7 test message", message.getHl7Message());
        assertEquals("FHIR test message", message.getFhirMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        FhirConvertedMessage message1 = new FhirConvertedMessage();
        message1.setHl7Message("HL7 test message");
        message1.setFhirMessage("FHIR test message");

        FhirConvertedMessage message2 = new FhirConvertedMessage();
        message2.setHl7Message("HL7 test message");
        message2.setFhirMessage("FHIR test message");

        FhirConvertedMessage message3 = new FhirConvertedMessage();
        message3.setHl7Message("Different HL7 message");
        message3.setFhirMessage("Different FHIR message");

        assertNotEquals(message1, message2);
        assertNotEquals(message1, message3);
    }

    @Test
    public void testToString() {
        FhirConvertedMessage message = new FhirConvertedMessage();
        message.setHl7Message("HL7 test message");
        message.setFhirMessage("FHIR test message");

        assertNotNull( message.toString());
    }
}
