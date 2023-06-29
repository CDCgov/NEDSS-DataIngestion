package gov.cdc.dataingestion.nbs.converter;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Hl7ToRhapsodysXmlConverterTest {
    Hl7ToRhapsodysXmlConverter target = new Hl7ToRhapsodysXmlConverter();

    @Test
    void convertHL7ToXMLTest() throws DiHL7Exception, JAXBException, IOException {
        String rawId = "whatever";
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;

        var result = target.convert(rawId, hl7Message);

        Assertions.assertTrue(result.contains(rawId));
    }
}
