package gov.cdc.dataingestion.validation.unitTest;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.model.RawERLModel;
import gov.cdc.dataingestion.validation.model.enums.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HL7v2ValidatorTests
{
    private IHL7v2Validator target;
    private HapiContext context;

    @BeforeEach
    public void setUp() {
        context = new DefaultHapiContext();
        target = new HL7v2Validator(context);
    }

    @Test
    public void MessageValidation_Success_ValidMessage_NotContainNewLine() throws HL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);
        var result = target.MessageValidation(id, model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5",result.getMessageVersion());
        Assertions.assertEquals(data, result.getRawMessage());
        Assertions.assertEquals(MessageType.HL7.name(), result.getMessageType());
    }

    @Test
    public void MessageValidation_Success_ValidMessage_ContainNewLine() throws HL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\n"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
                + "OBX|1|ST|||Test Value";

        String dataAfterValidated = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);

        var result = target.MessageValidation(id, model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5",result.getMessageVersion());
        Assertions.assertEquals(dataAfterValidated, result.getRawMessage());
        Assertions.assertEquals(MessageType.HL7.name(), result.getMessageType());
    }

    @Test
    public void MessageValidation_InvalidMessage_ThrowException() {

        String data = "Invalid Message";
        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);

        Exception exception = Assertions.assertThrows(
                HL7Exception.class, () -> {
                    target.MessageValidation(id, model);
                }
        );

        String expectedMessage = "Determine encoding for message. The following is the first 50 chars of the message for reference, although this may not be where the issue is: Invalid Message";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}