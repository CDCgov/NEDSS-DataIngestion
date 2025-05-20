package gov.cdc.dataingestion.share.helper;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import gov.cdc.dataingestion.share.repository.IObxIdStdLookupRepository;
import gov.cdc.dataingestion.share.repository.model.ObxIdStdLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ElrSplitterTest {

    @Mock
    private IObxIdStdLookupRepository obxIdStdLookupRepository;

    @InjectMocks
    private ElrSplitter splitter;

    private String testHL7Single = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r" +
            "PID|||7005728^^^TML^MR||JOHN^DOE^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r" +
            "OBX|1|ST|||Test Demo CDC 2-8-16";

    private String testHL7Multi = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r" +
            "PID|||7005728^^^TML^MR||JOHN^DOE^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r" +
            "OBR|1|EX41B1FE^EHR^2.16.840.1.113883.3.2226^ISO|14061001|94559-2^SARS coronavirus 2 ORF1ab region [Presence] in Respiratory specimen by NAA with probe detect^LN^COV2_Pnthr^Aptima SARS-CoV-2 (Panther)^L|||202202241139|||||||||^Ellis^Esther||||||20220224164200.000-0800|||F\r" +
            "OBX|1|CWE|94559-2^SARS coronavirus 2 ORF1ab region [Presence] in Respiratory specimen by NAA with probe detect^LN^SARSINT^SARSCoV2 Interpretation^L||260415000^Not detected^LN^260415000^SARS-CoV2 Not Detected^L||||||F|||202202241139|||^^^05^TMA^L||202202241210||||USVIPHL Territorial Public Health Laboratory^L^^^^CLIA&48D2179122|3500 Richmond Estate^^Christiansted^VI^00820-4370\r" +
            "OBR|2|EX41B1FE^EHR^2.16.840.1.113883.3.2226^ISO|14061001_1|68991-9^Epidemiologically important information for public health reporting panel^LN|||202202241139|||||||||^Ellis^Esther||||||20220224164200.000-0800|||F\r" +
            "OBX|1|CWE|95417-2^Whether this is the patients first test for the condition of interest^LN||UNK^Unknown^NULLFL||||||F|||202202241139|||||||||USVIPHL Territorial Public Health Laboratory^L^^^^CLIA&48D2179122|3500 Richmond Estate^^Christiansted^VI^00820-4370\r";

    private String testHL7ForOBX = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5.1\r" +
            "PID|||7005728^^^TML^MR||JOHN^DOE^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r" +
            "OBR|1|EX41B1FE^EHR^2.16.840.1.113883.3.2226^ISO|14061001|94559-2^SARS coronavirus 2 ORF1ab region [Presence] in Respiratory specimen by NAA with probe detect^LN^COV2_Pnthr^Aptima SARS-CoV-2 (Panther)^L|||202202241139|||||||||^Ellis^Esther||||||20220224164200.000-0800|||F\r" +
            "OBX|1|CWE|94559-2^SARS coronavirus 2 ORF1ab region [Presence] in Respiratory specimen by NAA with probe detect^LN^SARSINT^SARSCoV2 Interpretation^L||260415000^Not detected^LN^260415000^SARS-CoV2 Not Detected^L||||||F|||202202241139|||^^^05^TMA^L||202202241210||||USVIPHL Territorial Public Health Laboratory^L^^^^CLIA&48D2179122|3500 Richmond Estate^^Christiansted^VI^00820-4370\r" +
            "OBX|2|CWE|95417-2^Whether this is the patients first test for the condition of interest^LN||UNK^Unknown^NULLFL||||||F|||202202241139|||||||||USVIPHL Territorial Public Health Laboratory^L^^^^CLIA&48D2179122|3500 Richmond Estate^^Christiansted^VI^00820-4370\r"+
            "OBR|2|EX41B1FE^EHR^2.16.840.1.113883.3.2226^ISO|14061001_1|68991-9^Epidemiologically important information for public health reporting panel^LN|||202202241139|||||||||^Ellis^Esther||||||20220224164200.000-0800|||F\r" +
            "OBX|1|CWE|95417-2^Whether this is the patients first test for the condition of interest^LN||UNK^Unknown^NULLFL||||||F|||202202241139|||||||||USVIPHL Territorial Public Health Laboratory^L^^^^CLIA&48D2179122|3500 Richmond Estate^^Christiansted^VI^00820-4370\r";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void splitElr_singleObr() throws DiHL7Exception {
        HL7ParsedMessage<OruR1> parsedMessageOrig = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(testHL7Single);
        List<HL7ParsedMessage<OruR1>> result = splitter.splitElr(parsedMessageOrig);
        assertEquals(1, result.size());
    }

    @Test
    void splitElr_multiObr() throws DiHL7Exception {
        HL7ParsedMessage<OruR1> parsedMessageOrig = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(testHL7Multi);
        List<HL7ParsedMessage<OruR1>> result = splitter.splitElr(parsedMessageOrig);
        assertEquals(2, result.size());
    }

    @Test
    void splitElr_byOBX() throws DiHL7Exception {
        HL7ParsedMessage<OruR1> parsedMessageOrig = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(testHL7ForOBX);
        ObxIdStdLookup obxIdStdLookup=new ObxIdStdLookup();
        obxIdStdLookup.setId(1L);
        obxIdStdLookup.setObxValueTypeId("94559-2");
        when(obxIdStdLookupRepository.findByObxValueTypeId("94559-2"))
                .thenReturn(Optional.of(obxIdStdLookup));

        List<HL7ParsedMessage<OruR1>> result = splitter.splitElr(parsedMessageOrig);
        assertEquals(2, result.size());
    }
}