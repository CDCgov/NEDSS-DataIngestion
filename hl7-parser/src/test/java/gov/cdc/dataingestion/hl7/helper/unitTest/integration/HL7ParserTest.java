package gov.cdc.dataingestion.hl7.helper.unitTest.integration;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.HL7Parser;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IHL7Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HL7ParserTest {
    public String hl7Message = """
MSH|^~\\&|NBS^2.16.840.1.114222.4.5.1^ISO|SRA^2.16.840.1.114222.4.1.212974^ISO|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|20250509084201.101||ORU^R01^ORU_R01|NOT10163801GA012025-05-09T08:42:00.507|P|2.5.1|||||||||Generic_MMG_V2.0^PHINMsgMapID^2.16.840.1.114222.4.10.4^ISO~Malaria_MMG_V1.0^PHINMsgMapID^2.16.840.1.114222.4.10.4^ISO
PID|1||PSN10311150GA01^^^NBS&2.16.840.1.114222.4.5.1&ISO||^^^^^^S||19480422000000|||1901-8^1901-8^2.16.840.1.114222.4.5.1|^^Bennetttown^36^30342
OBR|1||CAS10167810GA01^NBS^2.16.840.1.114222.4.5.1^ISO|68991-9^Epidemiologic Information^LN|||20250109110152|||||||||||||||20250109110152|||F||||||10130^Malaria^2.16.840.1.114222.4.5.277
OBX|1|CWE|313185002^Symptoms Resolved After Treatment^2.16.840.1.113883.6.96^313185002^Did all signs or symptoms of malaria resolve within 7 days after starting treatment?^L||Y^Yes^2.16.840.1.113883.12.136||||||F
OBX|2|CWE|391103005^Adverse Events After Treatment^2.16.840.1.113883.6.96^391103005^Did the patient experience any adverse events within 4 weeks of starting malaria treatment?^L||N^No^2.16.840.1.113883.12.136||||||F
OBX|3|CWE|67187-5^Complication(s) related to this malaria illness^2.16.840.1.113883.6.1^67187_5^Complication(s) Related to this Malaria Illness^L||53622003^Cerebral malaria^2.16.840.1.113883.6.96||||||F
OBX|4|ST|77969-4^Jurisdiction Code^2.16.840.1.113883.6.1^INV107^Jurisdiction^L||Fulton County||||||F
OBX|5|DT|77995-9^Date Reported^2.16.840.1.113883.6.1^INV111^Date of Report^L||20250109||||||F
OBX|6|TS|77972-8^Earliest Date Reported to County^2.16.840.1.113883.6.1^INV120^Earliest Date Reported to County^L||20250109000000.000||||||F
OBX|7|TS|77973-6^Earliest Date Reported to State^2.16.840.1.113883.6.1^INV121^Earliest Date Reported to State^L||20250109000000.000||||||F
OBX|8|CWE|77978-5^Subject Died^2.16.840.1.113883.6.1^INV145^Did the patient die from this illness?^L||N^No^2.16.840.1.113883.12.136||||||F
OBX|9|DT|77979-3^Case Investigation Start Date^2.16.840.1.113883.6.1^INV147^Investigation Start Date^L||20250109||||||F
OBX|10|CWE|77980-1^Case Outbreak Indicator^2.16.840.1.113883.6.1^INV150^Is this case part of an outbreak?^L||N^No^2.16.840.1.113883.12.136||||||F
OBX|11|CWE|77990-0^Case Class Status Code^2.16.840.1.113883.6.1^INV163^Case Status^L||410605003^Confirmed present^2.16.840.1.113883.6.96||||||F
OBX|12|SN|77991-8^MMWR Week^2.16.840.1.113883.6.1^INV165^MMWR Week^L||^2||||||F
OBX|13|DT|77992-6^MMWR Year^2.16.840.1.113883.6.1^INV166^MMWR Year^L||2025||||||F
OBX|14|ST|74549-7^Person Reporting to CDC - Name^2.16.840.1.113883.6.1^INV190^Person Reporting to CDC - Name^L||Smith,Joe||||||F
OBX|15|ST|74548-9^Person Reporting to CDC - Phone Number^2.16.840.1.113883.6.1^INV191^Person Reporting to CDC - Phone Number^L||111-222-3333||||||F
OBX|16|CWE|77983-5^Country of Usual Residence^2.16.840.1.113883.6.1^INV501^Country of Usual Residence^L||DZA^ALGERIA^1.0.3166.1||||||F
OBX|17|CWE|77988-4^Binational Reporting Criteria^2.16.840.1.113883.6.1^INV515^Binational Reporting Criteria^L||PHC1140^Exposure to suspected product from Canada or Mexico^2.16.840.1.114222.4.5.274~PHC1139^Has case contacts in or from Mexico or Canada^2.16.840.1.114222.4.5.274~PHC1141^Other situations that may require binational notification or coordination of response^2.16.840.1.114222.4.5.274||||||F
OBX|18|CWE|77966-0^Reporting State^2.16.840.1.113883.6.1^NOT109^Reporting State^L||13^Georgia^2.16.840.1.113883.6.92||||||F
OBX|19|CWE|77968-6^National Reporting Jurisdiction^2.16.840.1.113883.6.1^NOT116^National Reporting Jurisdiction^L||13^Georgia^2.16.840.1.113883.6.92||||||F
OBX|20|CWE|77965-2^Immediate National Notifiable Condition^2.16.840.1.113883.6.1^NOT120^Immediate National Notifiable Condition^L||Y^Yes^2.16.840.1.113883.12.136||||||F
OBX|21|CWE|TRAVEL10^Subject Traveled or Lived Outside US^2.16.840.1.114222.4.5.232^TRAVEL10^Has the patient traveled or lived outside the U.S. during the past two years?^L||N^No^2.16.840.1.113883.12.136||||||F
OBX|22|DT|77970-2^Date First Reported PHD^2.16.840.1.113883.6.1^INV177^Date First Reported PHD^L||20250109||||||F
""";

    private HL7Parser target ;

    @BeforeEach
    public void setUp() {
        target = new HL7Parser(new DefaultHapiContext());
    }

    @Test
    void nndOruR01Validator_Test() throws DiHL7Exception, HL7Exception {
        var result = target.nndOruR01Validator(hl7Message.replaceAll("\n", "\r"));

        var test = "";
    }
}
