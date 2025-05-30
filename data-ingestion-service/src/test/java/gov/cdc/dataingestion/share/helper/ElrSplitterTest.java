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
    @Test
    void splitElr_obr_parentchild() throws DiHL7Exception {
        String testHL7ParentChild="MSH|^~\\&#|Intermountain Healthcare ELR Message Builder^1.11.111.111111.1.11.1.11^ISO|Intermountain Healthcare^99ID00049^CLIA|NBS^2.16.840.1.114222.4.5.1^ISO|IDHOH^2.16.840.1.114222.4.1.3651^ISO|20230317004909||ORU^R01^ORU_R01|1111111111111111111|P|2.5.1|||NE|NE|USA||||PHLabReport-NoAck^HL7^2.16.840.1.113883.9.11^ISO\r" +
                "SFT|Intermountain Healthcare^L^^^^Intermountain Assigning Authority&1.11.111.111111.1.11.1.11&ISO^XX^^^1234544|1|Intermountain Healthcare ELR Message Builder|12345||20220216\r" +
                "PID|1||1111111111^^^Intermountain Healthcare&2.16.840.1.113883.3.1205.2.1.3.100&ISO^MR^Logan Regional Hospital&2.16.840.1.113883.3.1205.6.1.3.700.13&ISO~22222222^^^Intermountain Healthcare&2.16.840.1.113883.3.1205.2.1.3.100&ISO^PI^Logan Regional Hospital&2.16.840.1.113883.3.1205.6.1.3.700.13&ISO||PATIENT1^SUSCONE1^M^^^^L~PATIENT^SUSCONE^MORGAN^^^^U~PATIENT^JOSH^^^^^N||19950211|M||2106-3^White^CDCREC|PO BOX 000^^FRANKLIN^ID^83237^US^H^^16041~0000 E WEST ROSE WAY^^FRANKLIN^ID^83237^US^C^^16041||^ORN^PH^^1^801^5551212~^PRN^PH^^1^208^5551212|^WPN^PH^^1^801^5551212||||||||2186-5^Not Hispanic or Latino^CDCREC||||||||N|||20230316184901|Logan Regional Hospital^2.16.840.1.113883.3.1205.6.1.3.700.13^ISO|337915000^Homo sapiens (organism)^SCT\r" +
                "PV1|1|O|^^^Logan Regional Hospital&2.16.840.1.113883.3.1205.6.1.3.700.13&ISO|C|||||||||||||||1111111111|||||||||||||||||||||||||20230314175439|20230314235959\r" +
                "ORC|RE|111111111^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|T111111111^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|||||||||1861789356^WASHBURN^JESSE^C.^^^^^NPI&2.16.840.1.113883.4.6&ISO^L^^^NPI^South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||^WPN^PH^^1^435^7553300|||||||South Cache Valley Clinic-Family Medicine^L^^^^CMS&2.16.840.1.113883.3.249&ISO^XX^^^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1|190 S HIGHWAY 165^^Providence^UT^84332^US^O|^WPN^PH^^1^435^7553300|190 S HWY 165^^PROVIDENCE^UT^84332^US^O\r" +
                "OBR|1|111111111^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|T111110000^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|640171-7^C trach+GC pnl Spec NAA+probe^LN|||20250508145500|||||||||1457890162^PRICE^SHEILA^LYNN^^^^^NPI^^^^NPI|^^^^^208^8858886|||||20250510040700||LAB|F||||||N89.8^Other specified noninflammatory disorders of vagina^ICD-10-CM|&Owens&Casey\r" +
                "NTE|1|L|Performed at:  01 - Labcorp Seattle\r" +
                "OBX|1|ST|433041-5^C trach rRNA Spec Ql NAA+probe^LN||Positive||Negative|A|||F|||20250508145500||253328^OWENS^CASEY^\"CASEY JO\"|||20250510040700||||LABCORP^D^^^^^^^^LABCORPBEAKER|531 SOUTH SPRING STREET^^BURLINGTON^NC^27215^USA^B^^37001\r" +
                "SPM|1|T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO^T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||255226008^Random (qualifier value)^SCT^39931131^Random^L||||255226008^Random (qualifier value)^SCT^480829001^Random Urine^L|||P^Patient^HL70369^^^^2.5.1||||||20230314175500^20230314175500|20230314181800\r" +
                "OBR|2|^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|T111111111^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|6301-4^Bacteria identified in Urine by Culture^LN^21647486^Urine Culture^L|||20230314175501|20230314175500|||||Dysuria|||1861789356^WASHBURN^JESSE^C.^^^^^NPI&2.16.840.1.113883.4.6&ISO^L^^^NPI^South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO|^WPN^PH^^1^435^7553300|||||20230317004900|||F||||||R30.0^Dysuria^I10C^13272106^Dysuria^L|1861789356&WASHBURN&JESSE&C.&&&&&NPI&2.16.840.1.113883.4.6&ISO\r" +
                "OBX|1|ST|6301-4^Bacteria identified in Urine by Culture^LN^21647486^Urine Culture^L|1|Klebsiella pneumoniae||||||F|||20230314160500|||||20230317004900||||UNKNOWN, PERSONNEL^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^253362\r" +
                "OBX|2|ST|6301-4^Bacteria identified in Urine by Culture^LN^21647486^Urine Culture^L|2|Streptococcus pneumoniae||||||F|||20230314160500|||||20230317004900||||UNKNOWN, PERSONNEL^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^253362\r" +
                "SPM|1|T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO^T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||255226008^Random (qualifier value)^SCT^39931131^Random^L||||255226008^Random (qualifier value)^SCT^480829001^Random Urine^L|||P^Patient^HL70369^^^^2.5.1||||||20230314175500^20230314175500|20230314181800\r" +
                "OBR|3||T111111111002^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|505451-3^Bacterial susceptibility panel by Minimum inhibitory concentration (MIC)^LN^312370^MIC^L|||20230314175500|20230314175500|||||Dysuria|||1861789356^WASHBURN^JESSE^C.^^^^^NPI&2.16.840.1.113883.4.6&ISO^L^^^NPI^South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO|^WPN^PH^^1^435^7553300|||||20230317004900|||F|6301-4&Bacteria identified in Urine by Culture&LN&21647486&Urine Culture&L^1^Klebsiella pneumoniae|||^T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||R30.0^Dysuria^I10C^13272106^Dysuria^L|1861789356&WASHBURN&JESSE&C.&&&&&NPI&2.16.840.1.113883.4.6&ISO\r" +
                "OBX|1|ST|18862-3^Amoxicillin+Clavulanate [Susceptibility]^LN^309704^Amox/Cla^L||S||||||F|||20230314160500|||||20230314160500||||UNKNOWN, PERSONNEL^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^253362\r" +
                "SPM|1|T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO^T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||255226008^Random (qualifier value)^SCT^39931131^Random^L||||255226008^Random (qualifier value)^SCT^480829001^Random Urine^L|||P^Patient^HL70369^^^^2.5.1||||||20230314175500^20230314175500|20230314181800\r" +
                "OBR|4||T111111111002^South Cache Valley Clinic-Family Medicine^2.16.840.1.113883.3.1205.6.1.3.700.13.21.1^ISO|505451-3^Bacterial susceptibility panel by Minimum inhibitory concentration (MIC)^LN^312370^MIC^L|||20230314175500|20230314175500|||||Dysuria|||1861789356^WASHBURN^JESSE^C.^^^^^NPI&2.16.840.1.113883.4.6&ISO^L^^^NPI^South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO|^WPN^PH^^1^435^7553300|||||20230317004900|||F|6301-4&Bacteria identified in Urine by Culture&LN&21647486&Urine Culture&L^2^Streptococcus pneumoniae|||^T111111111&South Cache Valley Clinic-Family Medicine&2.16.840.1.113883.3.1205.6.1.3.700.13.21.1&ISO||R30.0^Dysuria^I10C^13272106^Dysuria^L|1861789356&WASHBURN&JESSE&C.&&&&&NPI&2.16.840.1.113883.4.6&ISO\r" +
                "OBX|1|ST|18862-3^Amoxicillin+Clavulanate [Susceptibility]^LN^309704^Amox/Cla^L||S||||||F|||20230314160500|||||20230314160500||||UNKNOWN, PERSONNEL^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^253362";
        HL7ParsedMessage<OruR1> parsedMessageOrig = Hl7ToRhapsodysXmlConverter.getInstance().parsedStringToHL7(testHL7ParentChild);
        List<HL7ParsedMessage<OruR1>> result = splitter.splitElr(parsedMessageOrig);
        assertEquals(2, result.size());
    }
}