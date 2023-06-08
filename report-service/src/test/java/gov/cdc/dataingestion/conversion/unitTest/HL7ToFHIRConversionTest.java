package gov.cdc.dataingestion.conversion.unitTest;

import gov.cdc.dataingestion.conversion.integration.HL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

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

        ValidatedELRModel model = new ValidatedELRModel();
        model.setRawId("test");
        model.setMessageType("HL7");
        model.setRawMessage(data);
        model.setCreatedBy("test");
        model.setUpdatedBy("test");
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.ConvertHL7v2ToFhir(model, "test");

        Assertions.assertEquals("test", result.getRawId());
        Assertions.assertNull(result.getUpdatedOn());
        Assertions.assertNull(result.getCreatedOn());
        Assertions.assertEquals("test", result.getCreatedBy());
        Assertions.assertEquals("test", result.getUpdatedBy());
        Assertions.assertNotNull(result.getFhirMessage());
    }

    @Test
    public void ConvertHL7v2ToFhir_Fail_ThrowException() {

        String data = "Invalid Message";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setId("validated_test");
        model.setRawId("test");
        model.setMessageType("HL7");
        model.setRawMessage(data);

        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class, () -> {
                    target.ConvertHL7v2ToFhir(model, "test");
                }
        );

        String expectedMessage = "Parsed HL7 message was null";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
