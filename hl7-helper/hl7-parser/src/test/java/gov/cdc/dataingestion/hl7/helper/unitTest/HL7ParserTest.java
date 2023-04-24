package gov.cdc.dataingestion.hl7.helper.unitTest;

import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.OruR1Message;
import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.OruR1MessageSmall;

public class HL7ParserTest {
    private HL7Helper target;
    private String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\n"
            + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
            + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
            + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
            + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
            + "OBX|1|ST|||Test Value";

    private String validData = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
            + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
            + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
            + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
            + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
            + "OBX|1|ST|||Test Value";

    private String invalidData = "TEST TEST";
    @BeforeEach
    public void setUp() {
        target = new HL7Helper();
    }



    @Test
    public void hl7StringValidatorTest_ReturnValidMessage() throws DiHL7Exception {
        var result = target.hl7StringValidator(data);
        Assertions.assertEquals(validData, result);
    }

    @Test
    public void hl7StringValidatorTest_ReturnException() {
        Exception exception = Assertions.assertThrows(
                DiHL7Exception.class, () -> {
                    target.hl7StringValidator(invalidData);
                }
        );
        String expectedMessage = "Incorrect raw message format";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void hl7StringParser_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(OruR1Message);
       System.out.println("AAA");
        // Assertions.assertEquals("RACHEL", result.getPatientIdentification().getPatientName().getGivenName());
    }
//
//    @Test
//    public void hl7StringParser_ReturnException() {
//        Exception exception = Assertions.assertThrows(
//                DiHL7Exception.class, () -> {
//                    target.hl7StringParser(data);
//                }
//        );
//        String expectedMessage = "The HL7 version 2.5\n" +
//                "PID is not recognized";
//        String actualMessage = exception.getMessage();
//        Assertions.assertTrue(actualMessage.contains(expectedMessage));
//    }
}
