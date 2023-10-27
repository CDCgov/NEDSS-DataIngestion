package gov.cdc.dataingestion.validation.integration.validator;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class HL7v2ValidatorTests
{
    private IHL7v2Validator target;
    private HL7Helper hl7Helper;

    @BeforeEach
    public void setUp() {
        hl7Helper = new HL7Helper();
        target = new HL7v2Validator(hl7Helper);
    }

    @Test
    void MessageValidation_Success_ValidMessage_ValidatorActivated_ReturnError_MissingOBR() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379";
        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);


        Exception exception = Assertions.assertThrows(DiHL7Exception.class, () -> {
            target.MessageValidation(id, model, "test", true);
        });

        String expectedMessage = "Invalid Message ca.uhn.hl7v2.HL7Exception: Error Occurred at OBR-4";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    public void MessageValidation_Success_ValidMessage_NotContainNewLine() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.MessageValidation(id, model, "test", false);

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
    public void MessageValidation_Success_ValidMessage_NotContainNewLine_231() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.3.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);
        model.setCreatedOn(null);
        model.setUpdatedOn(null);
        var result = target.MessageValidation(id, model, "test", false);

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
    public void MessageValidation_Success_ValidMessage_ContainNewLine() throws DiHL7Exception {

        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\n"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
                + "OBX|1|ST|||Test Value";

        String dataAfterValidated = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);

        var result = target.MessageValidation(id, model, "test", false);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("2.5.1",result.getMessageVersion());
        Assertions.assertEquals(dataAfterValidated, result.getRawMessage());
        Assertions.assertEquals(EnumMessageType.HL7.name(), result.getMessageType());
    }

    @Test
    public void MessageValidation_Exception_NotSupportedVersion()  {

        String data = "MSH|^~\\&|OneAbbottSol.STAG^2.16.840.1.113883.3.8589.4.2.7.2^ISO|AbbottInformatics^00Z0000002^CLIA|AIMS.INTEGRATION.STG^2.16.840.1.114222.4.3.15.2^ISO|AIMS.PLATFORM^2.16.840.1.114222.4.1.217446^ISO|20210128162413-0500||ORU^R01^ORU_R01|20210128162413.806_P21-0000105078|T|2.3|||NE|NE|||||PHLabReport-NoAck^ELR251R1_Rcvr_Prof^2.16.840.1.113883.9.11^ISO\n" +
                "SFT|Abbott Informatics|PH12.1|STARLIMS PH|Binary ID Unknown\n" +
                "PID|1||P21-0000105078^^^OneAbbottSol.STAG&2.16.840.1.113883.3.8589.4.2.7.2&ISO^SID^OneAbbottSol.STAG&2.16.840.1.113883.3.8589.4.2.7.2&ISO||Trevor^Stacy^^||19880121190000-0500|F||2054-5^Black or African American^HL70005^^^^Vunknown|11 Norman drive^^Palatine^IL^60067^USA^^^||^^^^^847^2260356|||||||||U^Unknown^HL70189^^^^Vunknown||||||||\n" +
                "ORC|RE|2gcxDYvIHHLr+e6hO9Lxrg^OneAbbottSol.STAG^2.16.840.1.113883.3.8589.4.2.7.2^ISO|P21-0000105078^OneAbbottSol.STAG^2.16.840.1.113883.3.8589.4.2.7.2^ISO|||||||||^^SA.OverTheCounter|||20210128160603-0500||||||SA.OverTheCounter|11 Norman drive^^Palatine^IL^60067^USA^^^|^^^^^847^2260356|\n" +
                "OBR|1|2gcxDYvIHHLr+e6hO9Lxrg^OneAbbottSol.STAG^2.16.840.1.113883.3.8589.4.2.7.2^ISO|P21-0000105078^OneAbbottSol.STAG^2.16.840.1.113883.3.8589.4.2.7.2^ISO|95209-3^SARS-CoV+SARS-CoV-2 (COVID-19) Ag [Presence] in Respiratory specimen by Rapid immunoassay^LN^1^SARS Coronavirus 2 Ag^L^Vunknown^Vunknown|||20210128160603-0500|||||||||^^SA.OverTheCounter||||||20210128160621-0500||LAB|F|||||||||||\n" +
                "OBX|1|SN|30525-0^Age^LN^^^^Vunknown||^33|a^year^UCUM^^^^Vunknown|||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|2|CWE|95417-2^Whether this is the patient's first test for the condition of interest^LN^^^^2.69||N^No^HL70136^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|3|CWE|95418-0^Whether patient is employed in a healthcare setting^LN^^^^2.69||N^No^HL70136^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounterr^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|4|CWE|95419-8^Whether patient has symptoms related to condition of interest^LN^^^^2.69||Y^Yes^HL70136^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|5|CWE|77974-4^Whether patient was hospitalized because of this condition^LN^^^^2.69||N^No^HL70136^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounterr^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|6|CWE|95421-4^Whether patient resides in a congregate care setting^LN^^^^2.69||Y^Yes^HL70136^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|7|CWE|82810-3^Pregnancy status^LN^^^^2.69||60001007^Not pregnant^SCT^^^^Vunknown||||||F|||20210128160603-0500|00Z0000014||||||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||QST\n" +
                "OBX|8|CWE|95209-3^SARS-CoV+SARS-CoV-2 (COVID-19) Ag [Presence] in Respiratory specimen by Rapid immunoassay^LN^^^^Vunknown^||260373001^Detected^SCT^SARS Coronavirus 2 A^LA6576-8^L^Vunknown^Vunknown^LA6576-8||||||F|||20210128160603-0500|00Z0000014||Ellume COVID-19 Home Test_Ellume Limited_EUA^^99ELR^^^^Vunknown||20210128160621-0500||||SA.OverTheCounter^^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^00Z0000014|11 Fake AtHome Test Street^^Yakutat^AK^99689^^^^02282|||||\n" +
                "NTE|1|L|Ellume COVID-19 Home Test_Ellume Limited_EUA\n" +
                "SPM|1|^P21-0000105078&OneAbbottSol.STAG&2.16.840.1.113883.3.8589.4.2.7.2&ISO||445297001^Swab of internal nose^SCT^^^^Vunknown|||||||||||||20210128160603-0500|20210128160603-0500";


        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);


        Exception exception = Assertions.assertThrows(
                DiHL7Exception.class, () -> {
                    target.MessageValidation(id, model, "test", false);
                }
        );

        String expectedMessage = "Unsupported HL7 Version, please only specify either 2.3.1 or 2.5.1.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void MessageValidation_InvalidMessage_ThrowException() {

        String data = "Invalid Message";
        String id = "1";

        RawERLModel model = new RawERLModel();
        model.setPayload(data);
        model.setId(id);

        Exception exception = Assertions.assertThrows(
                DiHL7Exception.class, () -> {
                    target.MessageValidation(id, model, "test", false);
                }
        );
        String expectedMessage = "Determine encoding for message. The following is the first 50 chars of the message for reference, although this may not be where the issue is: Invalid Message";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}