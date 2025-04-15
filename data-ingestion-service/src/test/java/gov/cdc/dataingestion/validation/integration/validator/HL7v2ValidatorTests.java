package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.constant.enums.EnumMessageType;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class HL7v2ValidatorTests
{
    private IHL7v2Validator target;
    private HL7Helper hl7Helper;

    @BeforeEach
    void setUp() {
        hl7Helper = new HL7Helper();
        target = new HL7v2Validator(hl7Helper);
    }




    @Test
    void MessageValidation_Success_ValidMessage_NotContainNewLine() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000015||||||||SA.Prescription^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000015|12 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST";

        String id = "1";

        RawElrModel model = new RawElrModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.messageValidation(id, model, "test", false,"");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5.1",result.getMessageVersion());
        Assertions.assertEquals(data, result.getRawMessage());
        Assertions.assertEquals(EnumMessageType.HL7.name(), result.getMessageType());
        Assertions.assertNull(result.getCreatedOn());
        Assertions.assertNull(result.getUpdatedOn());
        Assertions.assertEquals("test", result.getCreatedBy());
        Assertions.assertEquals("test", result.getUpdatedBy());
    }

    @Test
    void MessageValidation_Success_ValidMessage_NotContainNewLine_231() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.3.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawElrModel model = new RawElrModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.messageValidation(id, model, "test", false,"");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.3.1",result.getMessageVersion());
        Assertions.assertEquals(data, result.getRawMessage());
        Assertions.assertEquals(EnumMessageType.HL7.name(), result.getMessageType());
        Assertions.assertNull(result.getCreatedOn());
        Assertions.assertNull(result.getUpdatedOn());
        Assertions.assertEquals("test", result.getCreatedBy());
        Assertions.assertEquals("test", result.getUpdatedBy());
    }

    @Test
    void MessageValidation_Success_ValidMessage_ContainNewLine() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\n"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
                + "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000015||||||||SA.Prescription^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000015|12 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST";

        String dataAfterValidated = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000015||||||||SA.Prescription^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000015|12 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST";

        String id = "1";

        RawElrModel model = new RawElrModel();
        model.setPayload(data);
        model.setId(id);

        var result = target.messageValidation(id, model, "test", false,"");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5.1",result.getMessageVersion());
        Assertions.assertEquals(dataAfterValidated, result.getRawMessage());
        Assertions.assertEquals(EnumMessageType.HL7.name(), result.getMessageType());
    }

    @Test
    void MessageValidation_InvalidMessage_ThrowException() {

        String data = "Invalid Message";
        String id = "1";

        RawElrModel model = new RawElrModel();
        model.setPayload(data);
        model.setId(id);

        Exception exception = Assertions.assertThrows(
                DiHL7Exception.class, () -> {
                    target.messageValidation(id, model, "test", false,"");
                }
        );
        String expectedMessage = "Determine encoding for message. The following is the first 50 chars of the message for reference, although this may not be where the issue is: Invalid Message";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void messageValidation_Success_ValidMessage_validationActive() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000015||||||||SA.Prescription^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000015|12 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST";

        String id = "1";

        RawElrModel model = new RawElrModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.messageValidation(id, model, "test", true,"");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5.1",result.getMessageVersion());
        Assertions.assertEquals(data, result.getRawMessage());
        Assertions.assertEquals(EnumMessageType.HL7.name(), result.getMessageType());
        Assertions.assertNull(result.getCreatedOn());
        Assertions.assertNull(result.getUpdatedOn());
        Assertions.assertEquals("test", result.getCreatedBy());
        Assertions.assertEquals("test", result.getUpdatedBy());
    }
    @Test
    void messageValidation_Success_fhsMsg_validation() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000015||||||||SA.Prescription^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000015|12 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST";

        String processedMsg =target.processFhsMessage (data);
        String validatedMsg=target.hl7MessageValidation(processedMsg);
        Assertions.assertNotNull(validatedMsg);
    }
}