//package gov.cdc.dataingestion.hl7.helper;
//
//import ca.uhn.hl7v2.HL7Exception;
//import gov.cdc.dataingestion.hl7.helper.integration.DiHL7Exception;
//
//public class MainClassTest {
//    public static void main(String[] args) throws DiHL7Exception {
//        String hl7TestMessage = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\n" +
//                "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n" +
//                "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n" +
//                "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n" +
//                "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n" +
//                "OBX|1|ST|||Test Value TEST";
//
//        HL7ParserLibrary lib = new HL7ParserLibrary();
//
//        var valid = lib.hl7StringValidator(hl7TestMessage);
//        var msg =  lib.hl7StringParser(valid);
//
//        System.out.println("XX");
//    }
//}
