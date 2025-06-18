package gov.cdc.dataingestion.share.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HL7BatchSplitterTest {

    @InjectMocks
    private ElrSplitter splitter;

    String hl7BatchMsg = "FHS|^~\\&|TEST|LABCORP-CORP|IHIE|IHIE|20120703094005||||||\n" +
            "BHS|^~\\&|TEST|LABCORP-CORP|IHIE|IHIE|20120703094005||||||\n" +
            "MSH|^~\\&|HL7 Generator-batch-0618^^|CVS Health FL3486POCT ORANGE PARK^20D2030000^CLIA|ALDOH^OID^ISO|AL^OID^ISO|202504231141||ORU^R01^ORU_R01|20250423114190|P|2.5.1\n" +
            "PID|1|621458572^^^CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA|621458572^^^CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA^SS||Nichols^Leon^SIM_TEST^^^^||198212110000|O||1482-9^Huron Potawatomi^SIM_TEST|4903 Brian Loaf^^Tonyahaven^HI^30342||296-641-5615^^^^|^^^LeonNichols90@hotmail.com^||T^SIM_TEST^^^^|||844-25-8173\n" +
            "PV1||N\n" +
            "ORC|RE||20120601114^LABCORP^20D0649525^CLIA||||||||||||||||||COOSA VALLEY MEDICAL CENTER|315 WEST HICKORY ST.^SUITE 100^SYLACAUGA^AL^35150^USA^^^RICHLAND|^^^^^256^2495780^123|380 WEST HILL ST.^^SYLACAUGA^AL^35150^USA^^^RICHLAND\n" +
            "OBR|1|90^LABCORP^20D0649525^|90^CVS Health FL3486POCT ORANGE PARK^20D2030000^CLIA|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN^90^TestData^L|||202504231141|202504281141||||||202504231141||9600589^Nichols^Leon^III^Dr^DO|296-641-5615^^^LeonNichols90@hotmail.com|||||202504281141|||C|||1609735^Nichols^Leon^II^Ms^PA|||98734^Dissect^49046|27466&Nichols&Leon&&IV&Ms&APRN^202504231141^202504281141\n" +
            "OBX|1|NM|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN||90|mL|||||F||||||||202504231141\n" +
            "SPM|1|5569177&LABCORP&20D0649525&^8678383&CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA||SER^Serum^HL70487^TSMI^Tissue small intestine Tissue ulcer^SCT^2.5.1^Serum|||TSMI^TSMI^TG|||||90^ML|||||202504231141^202504281141|202504231141|||\n" +
            "MSH|^~\\&|HL7 Generator-batch-0618^^|Core Diagnostic Laboratories^77D7733733^CLIA|ALDOH^OID^ISO|AL^OID^ISO|202504231141||ORU^R01^ORU_R01|20250423114119|P|2.5.1\n" +
            "PID|1|314476254^^^Core Diagnostic Laboratories&77D7733733&CLIA|314476254^^^Core Diagnostic Laboratories&77D7733733&CLIA^SS||Torres^Richard^SIM_TEST^^^^||199003300000|M||1689-9^Carson^SIM_TEST|430 Tina Lane Suite 140^^Burnettstad^CT^30342||(636)798-0898^^^^|^^^RichardTorres19@hotmail.com^||T^SIM_TEST^^^^|||093-92-8978\n" +
            "PV1||C\n" +
            "ORC|RE||20120601114^LABCORP^20D0649525^CLIA||||||||||||||||||COOSA VALLEY MEDICAL CENTER|315 WEST HICKORY ST.^SUITE 100^SYLACAUGA^AL^35150^USA^^^RICHLAND|^^^^^256^2495780^123|380 WEST HILL ST.^^SYLACAUGA^AL^35150^USA^^^RICHLAND\n" +
            "OBR|1|19^Core Diagnostic Laboratories^62d2261112^|19^Core Diagnostic Laboratories^77D7733733^CLIA|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN^19^TestData^L|||202504231141|202504281141||||||202504231141||2708962^Torres^Richard^III^Mrs^NP|(636)798-0898^^^RichardTorres19@hotmail.com|||||202504281141|||C|||7017466^Torres^Richard^VIII^Dr^MD|||88732^LNG TRM CURR USE OF INSUL^63544|29156&Torres&Richard&&III&Dr&MD^202504231141^202504281141\n" +
            "OBX|3|TX|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN||This is a test||||||P||||||||202504231141\n" +
            "SPM|1|4200954&Core Diagnostic Laboratories&62d2261112&^2779440&Core Diagnostic Laboratories&77D7733733&CLIA||PLB^Plasma bag^HL70487^UMED^Unknown medicine^SCT^2.5.1^Plasma bag|||UMED^UMED^TG|||||19^ML|||||202504231141^202504281141|202504231141|||\n" +
            "BTS|2|Batch Message Count\n" +
            "FTS|1|End of batch file";
    String hl7MsgNoBatch ="MSH|^~\\&|HL7 Generator-batch-0618^^|CVS Health FL3486POCT ORANGE PARK^20D2030000^CLIA|ALDOH^OID^ISO|AL^OID^ISO|202504231141||ORU^R01^ORU_R01|20250423114190|P|2.5.1\n" +
            "PID|1|621458572^^^CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA|621458572^^^CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA^SS||Nichols^Leon^SIM_TEST^^^^||198212110000|O||1482-9^Huron Potawatomi^SIM_TEST|4903 Brian Loaf^^Tonyahaven^HI^30342||296-641-5615^^^^|^^^LeonNichols90@hotmail.com^||T^SIM_TEST^^^^|||844-25-8173\n" +
            "PV1||N\n" +
            "ORC|RE||20120601114^LABCORP^20D0649525^CLIA||||||||||||||||||COOSA VALLEY MEDICAL CENTER|315 WEST HICKORY ST.^SUITE 100^SYLACAUGA^AL^35150^USA^^^RICHLAND|^^^^^256^2495780^123|380 WEST HILL ST.^^SYLACAUGA^AL^35150^USA^^^RICHLAND\n" +
            "OBR|1|90^LABCORP^20D0649525^|90^CVS Health FL3486POCT ORANGE PARK^20D2030000^CLIA|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN^90^TestData^L|||202504231141|202504281141||||||202504231141||9600589^Nichols^Leon^III^Dr^DO|296-641-5615^^^LeonNichols90@hotmail.com|||||202504281141|||C|||1609735^Nichols^Leon^II^Ms^PA|||98734^Dissect^49046|27466&Nichols&Leon&&IV&Ms&APRN^202504231141^202504281141\n" +
            "OBX|1|NM|77190-7^Hepatitis B virus core and surface Ab and surface Ag panel - Serum^LN||90|mL|||||F||||||||202504231141\n" +
            "SPM|1|5569177&LABCORP&20D0649525&^8678383&CVS Health FL3486POCT ORANGE PARK&20D2030000&CLIA||SER^Serum^HL70487^TSMI^Tissue small intestine Tissue ulcer^SCT^2.5.1^Serum|||TSMI^TSMI^TG|||||90^ML|||||202504231141^202504281141|202504231141|||\n";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void splitHL7Batch_multiELRs() {
        List<String> result= HL7BatchSplitter.splitHL7Batch(hl7BatchMsg);
        System.out.println("size:"+result.size());
        assertEquals(2, result.size());
    }
    @Test
    void splitHL7Batch_singleELR() {
        List<String> result= HL7BatchSplitter.splitHL7Batch(hl7MsgNoBatch);
        assertEquals(1, result.size());
    }
}