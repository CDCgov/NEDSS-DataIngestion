package gov.cdc.dataingestion.hl7.helper.unitTest;

import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiFhirException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FhirConverterTest {
    private HL7Helper target;
    private String validData = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
            + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
            + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
            + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
            + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
            + "OBX|1|ST|||Test Value";


    @BeforeEach
    public void setUp() {
        target = new HL7Helper();
    }


    @Test
    public void convertingHL7ToFhir_Success() throws DiFhirException {
        var result = target.convertHl7ToFhir(validData);
        Assertions.assertNotNull(result.getFhirMessage());
        Assertions.assertNotNull(result.getHl7Message());
    }

    @Test
    public void convertingHL7ToFhir_ReturnException() {
        Exception exception = Assertions.assertThrows(
                DiFhirException.class, () -> {
                    target.convertHl7ToFhir("Bad Data");
                }
        );
        String expectedMessage = "Parsed HL7 message was null.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
