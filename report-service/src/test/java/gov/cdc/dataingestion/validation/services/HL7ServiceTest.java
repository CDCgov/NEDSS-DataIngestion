package gov.cdc.dataingestion.validation.services;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class HL7ServiceTest {
    @Mock
    private IHL7v2Validator hl7v2Validator;

    @InjectMocks
    private HL7Service target;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHl7Validator() throws DiHL7Exception {
        String hl7Msg = "MSH|^~\\&|UHS|Centennial Hills Hospital Medical Center^TBD^CLIA|NV|NVDOH|20150224114200||ORU^R01|2015022411420626264_1001_6|P|2.3.1|||||USA\n" +
                "PID|1||0000000^^^Summerlin Hospital Medical Center&TBD&CLIA^MR^Summerlin Hospital Medical Center&TBD&CLIA~19116381^^^Summerlin Hospital Medical Center&TBD&CLIA^PI^Summerlin Hospital Medical Center&TBD&CLIA~XxxXx3681^^^SSN&TBD&ISO^SS||XXXXLast^XXXXFirst|||M||W|XXXXXXXXX^^YYYY^NV^89000^USA^C||^PRN^PH^^^555^5555555|||||||||N\n" +
                "ORC|RE|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|||||||||^Adrian^Adrian^^^^^^^L|SHM CVCU|^WPN^PH^^^702^5089199|||||||SHM- Summerlin Hospital Medical Center^L^^^^SHM- Summerlin Hospital Medical Center&TBD&CLIA|657 Town Center Drive^^Las Vegas^NV^89144-6367^^B^^32003|^^PH^^^702^2337000|5940 S RAINBOW BLVD^^LAS VEGAS^NV^89118-0000^USA^B\n" +
                "OBR|1|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|24325-3^Acute Hepatitis Panel^LN|||20150101||||||None|20150101091010-0200|119297000&Blood&SNM|^Adrian^Adrian^^^^^^^L|^WPN^PH^^^702^5089199|||||20150223141500||LAB|F\n" +
                "OBX|1|CE|22327-1^Hepatitis C Antibody (Anti HCV)^LN^408^Hepatitis C Antibody (Anti HCV)^L|1|11214006^Reactive^SCT||||||F|||20150101|29D1070766^CHH Medical Center^CLIA\n" +
                "NTE|1||Performing Lab AddresAAs: 6900 NoorthAA Durango Drive, Las Vegas, NV  89149";

        String hl7MsgAfterStringSet = "MSH|^~\\&|UHS|Centennial Hills Hospital Medical Center^TBD^CLIA|NV|NVDOH|20150224114200||ORU^R01|2015022411420626264_1001_6|P|2.3.1|||||USA\r" +
                "PID|1||0000000^^^Summerlin Hospital Medical Center&TBD&CLIA^MR^Summerlin Hospital Medical Center&TBD&CLIA~19116381^^^Summerlin Hospital Medical Center&TBD&CLIA^PI^Summerlin Hospital Medical Center&TBD&CLIA~XxxXx3681^^^SSN&TBD&ISO^SS||XXXXLast^XXXXFirst|||M||W|XXXXXXXXX^^YYYY^NV^89000^USA^C||^PRN^PH^^^555^5555555|||||||||N\r" +
                "ORC|RE|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|||||||||^Adrian^Adrian^^^^^^^L|SHM CVCU|^WPN^PH^^^702^5089199|||||||SHM- Summerlin Hospital Medical Center^L^^^^SHM- Summerlin Hospital Medical Center&TBD&CLIA|657 Town Center Drive^^Las Vegas^NV^89144-6367^^B^^32003|^^PH^^^702^2337000|5940 S RAINBOW BLVD^^LAS VEGAS^NV^89118-0000^USA^B\r" +
                "OBR|1|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|24325-3^Acute Hepatitis Panel^LN|||20150101||||||None|20150101091010-0200|119297000&Blood&SNM|^Adrian^Adrian^^^^^^^L|^WPN^PH^^^702^5089199|||||20150223141500||LAB|F\r" +
                "OBX|1|CE|22327-1^Hepatitis C Antibody (Anti HCV)^LN^408^Hepatitis C Antibody (Anti HCV)^L|1|11214006^Reactive^SCT||||||F|||20150101|29D1070766^CHH Medical Center^CLIA\r" +
                "NTE|1||Performing Lab AddresAAs: 6900 NoorthAA Durango Drive, Las Vegas, NV  89149";

        when(hl7v2Validator.MessageStringValidation(hl7Msg))
                .thenReturn(hl7MsgAfterStringSet);

        when(hl7v2Validator.hl7MessageValidation(hl7MsgAfterStringSet))
                .thenReturn(hl7MsgAfterStringSet);

        String result   = target.hl7Validator(hl7Msg);
        assertEquals(hl7MsgAfterStringSet, result);

    }
}
