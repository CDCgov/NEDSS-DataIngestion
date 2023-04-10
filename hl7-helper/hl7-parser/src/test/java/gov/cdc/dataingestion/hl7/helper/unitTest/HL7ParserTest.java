package gov.cdc.dataingestion.hl7.helper.unitTest;

import gov.cdc.dataingestion.hl7.helper.HL7ParserLibrary;
import gov.cdc.dataingestion.hl7.helper.integration.DiHL7Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HL7ParserTest {
    private HL7ParserLibrary target;
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

    private String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ORU_R01 xmlns=\"urn:hl7-org:v2xml\">\n" +
            "    <MSH>\n" +
            "        <MSH.1>|</MSH.1>\n" +
            "        <MSH.2>^~\\&amp;</MSH.2>\n" +
            "        <MSH.3>\n" +
            "            <HD.1>ULTRA</HD.1>\n" +
            "        </MSH.3>\n" +
            "        <MSH.4>\n" +
            "            <HD.1>TML</HD.1>\n" +
            "        </MSH.4>\n" +
            "        <MSH.5>\n" +
            "            <HD.1>OLIS</HD.1>\n" +
            "        </MSH.5>\n" +
            "        <MSH.6>\n" +
            "            <HD.1>OLIS</HD.1>\n" +
            "        </MSH.6>\n" +
            "        <MSH.7>\n" +
            "            <TS.1>200905011130</TS.1>\n" +
            "        </MSH.7>\n" +
            "        <MSH.9>\n" +
            "            <MSG.1>ORU</MSG.1>\n" +
            "            <MSG.2>R01</MSG.2>\n" +
            "        </MSH.9>\n" +
            "        <MSH.10>20169838-v25</MSH.10>\n" +
            "        <MSH.11>\n" +
            "            <PT.1>T</PT.1>\n" +
            "        </MSH.11>\n" +
            "        <MSH.12>\n" +
            "            <VID.1>2.5</VID.1>\n" +
            "        </MSH.12>\n" +
            "    </MSH>\n" +
            "    <ORU_R01.PATIENT_RESULT>\n" +
            "        <ORU_R01.PATIENT>\n" +
            "            <PID>\n" +
            "                <PID.3>\n" +
            "                    <CX.1>7005728</CX.1>\n" +
            "                    <CX.4>\n" +
            "                        <HD.1>TML</HD.1>\n" +
            "                    </CX.4>\n" +
            "                    <CX.5>MR</CX.5>\n" +
            "                </PID.3>\n" +
            "                <PID.5>\n" +
            "                    <XPN.1>\n" +
            "                        <FN.1>TEST</FN.1>\n" +
            "                    </XPN.1>\n" +
            "                    <XPN.2>RACHEL</XPN.2>\n" +
            "                    <XPN.3>DIAMOND</XPN.3>\n" +
            "                </PID.5>\n" +
            "                <PID.7>\n" +
            "                    <TS.1>19310313</TS.1>\n" +
            "                </PID.7>\n" +
            "                <PID.8>F</PID.8>\n" +
            "                <PID.11>\n" +
            "                    <XAD.1>\n" +
            "                        <SAD.1>200 ANYWHERE ST</SAD.1>\n" +
            "                    </XAD.1>\n" +
            "                    <XAD.3>TORONTO</XAD.3>\n" +
            "                    <XAD.4>ON</XAD.4>\n" +
            "                    <XAD.5>M6G 2T9</XAD.5>\n" +
            "                </PID.11>\n" +
            "                <PID.13>\n" +
            "                    <XTN.1>(416)888-8888</XTN.1>\n" +
            "                </PID.13>\n" +
            "                <PID.19>1014071185</PID.19>\n" +
            "            </PID>\n" +
            "            <ORU_R01.VISIT>\n" +
            "                <PV1>\n" +
            "                    <PV1.1>1</PV1.1>\n" +
            "                    <PV1.3>\n" +
            "                        <PL.1>OLIS</PL.1>\n" +
            "                    </PV1.3>\n" +
            "                    <PV1.7>\n" +
            "                        <XCN.1>OLIST</XCN.1>\n" +
            "                        <XCN.2>\n" +
            "                            <FN.1>BLAKE</FN.1>\n" +
            "                        </XCN.2>\n" +
            "                        <XCN.3>DONALD</XCN.3>\n" +
            "                        <XCN.4>THOR</XCN.4>\n" +
            "                        <XCN.9>\n" +
            "                            <HD.1>921379</HD.1>\n" +
            "                        </XCN.9>\n" +
            "                        <XCN.13>OLIST</XCN.13>\n" +
            "                    </PV1.7>\n" +
            "                </PV1>\n" +
            "            </ORU_R01.VISIT>\n" +
            "        </ORU_R01.PATIENT>\n" +
            "        <ORU_R01.ORDER_OBSERVATION>\n" +
            "            <ORC>\n" +
            "                <ORC.1>RE</ORC.1>\n" +
            "                <ORC.3>\n" +
            "                    <EI.1>T09-100442-RET-0</EI.1>\n" +
            "                    <EI.3>OLIS_Site_ID</EI.3>\n" +
            "                    <EI.4>ISO</EI.4>\n" +
            "                </ORC.3>\n" +
            "                <ORC.12>\n" +
            "                    <XCN.1>OLIST</XCN.1>\n" +
            "                    <XCN.2>\n" +
            "                        <FN.1>BLAKE</FN.1>\n" +
            "                    </XCN.2>\n" +
            "                    <XCN.3>DONALD</XCN.3>\n" +
            "                    <XCN.4>THOR</XCN.4>\n" +
            "                    <XCN.8>L</XCN.8>\n" +
            "                    <XCN.9>\n" +
            "                        <HD.1>921379</HD.1>\n" +
            "                    </XCN.9>\n" +
            "                </ORC.12>\n" +
            "            </ORC>\n" +
            "            <OBR>\n" +
            "                <OBR.1>0</OBR.1>\n" +
            "                <OBR.3>\n" +
            "                    <EI.1>T09-100442-RET-0</EI.1>\n" +
            "                    <EI.3>OLIS_Site_ID</EI.3>\n" +
            "                    <EI.4>ISO</EI.4>\n" +
            "                </OBR.3>\n" +
            "                <OBR.4>\n" +
            "                    <CE.1>RET</CE.1>\n" +
            "                    <CE.2>RETICULOCYTE COUNT</CE.2>\n" +
            "                    <CE.3>HL79901 literal</CE.3>\n" +
            "                </OBR.4>\n" +
            "                <OBR.7>\n" +
            "                    <TS.1>200905011106</TS.1>\n" +
            "                </OBR.7>\n" +
            "                <OBR.14>\n" +
            "                    <TS.1>200905011106</TS.1>\n" +
            "                </OBR.14>\n" +
            "                <OBR.16>\n" +
            "                    <XCN.1>OLIST</XCN.1>\n" +
            "                    <XCN.2>\n" +
            "                        <FN.1>BLAKE</FN.1>\n" +
            "                    </XCN.2>\n" +
            "                    <XCN.3>DONALD</XCN.3>\n" +
            "                    <XCN.4>THOR</XCN.4>\n" +
            "                    <XCN.8>L</XCN.8>\n" +
            "                    <XCN.9>\n" +
            "                        <HD.1>921379</HD.1>\n" +
            "                    </XCN.9>\n" +
            "                </OBR.16>\n" +
            "                <OBR.18>7870279</OBR.18>\n" +
            "                <OBR.19>7870279</OBR.19>\n" +
            "                <OBR.20>T09-100442</OBR.20>\n" +
            "                <OBR.21>MOHLTC</OBR.21>\n" +
            "                <OBR.22>\n" +
            "                    <TS.1>200905011130</TS.1>\n" +
            "                </OBR.22>\n" +
            "                <OBR.24>B7</OBR.24>\n" +
            "                <OBR.25>F</OBR.25>\n" +
            "                <OBR.27>\n" +
            "                    <TQ.1>\n" +
            "                        <CQ.1>1</CQ.1>\n" +
            "                    </TQ.1>\n" +
            "                    <TQ.4>\n" +
            "                        <TS.1>200905011106</TS.1>\n" +
            "                    </TQ.4>\n" +
            "                    <TQ.6>R</TQ.6>\n" +
            "                </OBR.27>\n" +
            "            </OBR>\n" +
            "            <ORU_R01.OBSERVATION>\n" +
            "                <OBX>\n" +
            "                    <OBX.1>1</OBX.1>\n" +
            "                    <OBX.2>ST</OBX.2>\n" +
            "                    <OBX.5>Test Value</OBX.5>\n" +
            "                </OBX>\n" +
            "            </ORU_R01.OBSERVATION>\n" +
            "        </ORU_R01.ORDER_OBSERVATION>\n" +
            "    </ORU_R01.PATIENT_RESULT>\n" +
            "</ORU_R01>\n";

    private String invalidData = "TEST TEST";
    @BeforeEach
    public void setUp() {
        target = new HL7ParserLibrary();
    }


    @Test
    public void convertHL7ToXml_ReturnValidMessage() throws DiHL7Exception {
        var result = target.convertHL7ToXml(validData);
        Assertions.assertEquals(validXml, result);
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
        var result = target.hl7StringParser(validData);
        Assertions.assertEquals("RACHEL", result.getPatientIdentification().getPatientName().getGivenName());
    }

    @Test
    public void hl7StringParser_ReturnException() {
        Exception exception = Assertions.assertThrows(
                DiHL7Exception.class, () -> {
                    target.hl7StringParser(data);
                }
        );
        String expectedMessage = "The HL7 version 2.5\n" +
                "PID is not recognized";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
