package gov.cdc.dataingestion.hl7.helper.unitTest;

import com.google.gson.Gson;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.*;
import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.messageOriginal;

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
    public void hl7StringParser_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(testMessageForXmlIssue);
        Gson gson = new Gson();
        String json = gson.toJson(result);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParser_ReturnValidFromRhapsodyMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(messageByRhapsody);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }


    @Test
    public void hl7StringConvert231To251_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.convert231To251(testMessage);

        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParserWith231_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(testMessage);

        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParserWith251_ReturnValidMessage_RandomV1() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated251WithDataInAllField);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParserWith251_ReturnValidMessage_RandomV2() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated251WithDataInAllFieldV2);
        var oru = (OruR1) result.getParsedMessage();
        Gson gson = new Gson();
        var test = gson.toJson(result);
        Assertions.assertEquals("R01", result.getEventTrigger());
        Assertions.assertEquals("20230615",oru.getContinuationPointer().getContinuationPointer());
        Assertions.assertEquals("20230615",oru.getContinuationPointer().getContinuationStyle());

        Assertions.assertEquals("|",oru.getMessageHeader().getFieldSeparator());
        Assertions.assertEquals("^~\\&",oru.getMessageHeader().getEncodingCharacters());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getSecurity());
        Assertions.assertEquals("123456789",oru.getMessageHeader().getMessageControlId());

        Assertions.assertEquals("20230615",oru.getMessageHeader().getSequenceNumber());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getContinuationPointer());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getAcceptAckType());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getApplicationAckType());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getCountryCode());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getAlternateCharacterSetHandlingScheme());

        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareCertifiedVersionOrReleaseNumber());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareProductName());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareBinaryId());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareProductInformation());

        Assertions.assertEquals("1",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSetIdNte());
        Assertions.assertEquals("20230615",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSourceOfComment());


        // Code coverage for setter
        oru.getContinuationPointer().setContinuationPointer("AA");
        oru.getContinuationPointer().setContinuationStyle("AA");
        Assertions.assertEquals("AA",oru.getContinuationPointer().getContinuationPointer());
        Assertions.assertEquals("AA",oru.getContinuationPointer().getContinuationStyle());

        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setSetIdNte("3");
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setSourceOfComment("AA");
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setComment(new ArrayList<>());
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setCommentType(new Ce());
        Assertions.assertEquals("3",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSetIdNte());
        Assertions.assertEquals("AA",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSourceOfComment());


    }

    @Test
    public void hl7StringParserWith231_ReturnValidMessage_RandomV1() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated231WithDataInAllFieldV1);
        Gson gson = new Gson();
        var test = gson.toJson(result);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

}
