package gov.cdc.dataingestion.nbs.converter;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.nbs.TestHelper;
import gov.cdc.dataingestion.nbs.converters.RhapsodysXmlToHl7Converter;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class RhapsodysXmlToHl7ConverterTest {
    RhapsodysXmlToHl7Converter target = new RhapsodysXmlToHl7Converter();

    @Test
    void convertXmlToHL7Test() throws Exception {
        String xmlMessage = TestHelper.testFileReading();
        var result = target.convertToHl7(xmlMessage);
        Assertions.assertEquals(TestData.expected251HL7, result);
    }

}
