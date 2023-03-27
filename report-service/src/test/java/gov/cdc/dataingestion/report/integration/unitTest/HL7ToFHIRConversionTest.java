package gov.cdc.dataingestion.report.integration.unitTest;

import gov.cdc.dataingestion.report.integration.conversion.HL7ToFHIRConversion;
import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HL7ToFHIRConversionTest {
    private IHL7ToFHIRConversion target;

    @BeforeEach
    public void setUp() {
        target = new HL7ToFHIRConversion(new HL7ToFHIRConverter());
    }

    @Test
    public void ConvertHL7v2ToFhir_Success() {
        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        var result = target.ConvertHL7v2ToFhir(data);
    }

    @Test
    public void ConvertHL7v2ToFhir_Fail_ThrowException() {

        String data = "Invalid Message";

        Exception exception = Assertions.assertThrows(
                UnsupportedOperationException.class, () -> {
                    target.ConvertHL7v2ToFhir(data);
                }
        );

        String expectedMessage = "Determine encoding for message. The following is the first 50 chars of the message for reference, although this may not be where the issue is: Invalid Message";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
