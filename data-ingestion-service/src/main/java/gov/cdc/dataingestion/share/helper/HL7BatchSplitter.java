package gov.cdc.dataingestion.share.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HL7BatchSplitter {

    public static List<String> splitHL7Batch(String batchHL7Msg) {
        List<String> hl7Messages = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        System.out.println("BTS value:" + StringUtils.substringBetween(batchHL7Msg, "BTS|", "|"));
        //Check if input message has batch - BTS|5|Batch Message Count
        int batchMsgCount = getNumberOfMessages(batchHL7Msg);
        System.out.println("batchMsgCount:" + batchMsgCount);
        if (batchMsgCount > 1) {
            String[] msgAllLines = batchHL7Msg.split("\\R");
            for (String line : msgAllLines) {
                if (line.startsWith("MSH|")) {
                    if (currentMessage.length() > 0) {
                        hl7Messages.add(currentMessage.toString());
                    }
                    currentMessage = new StringBuilder(line + "\n");
                } else if (!line.startsWith("FHS|") && !line.startsWith("BHS|") && !line.startsWith("BTS|") && !line.startsWith("FTS|")) {
                    currentMessage.append(line).append("\n");
                }
            }
            if (currentMessage.length() > 0) {
                hl7Messages.add(currentMessage.toString());
            }
        }else{
            System.out.println("Only one HL7 message. No ELR batch split is needed.");
            hl7Messages.add(batchHL7Msg);
        }
        return hl7Messages;
    }

    private static int getNumberOfMessages(String hl7Str) {
        String batchMsgCount = StringUtils.substringBetween(hl7Str, "BTS|", "|");
        System.out.println("BTS value.HL7 message count:" + batchMsgCount);
        if (NumberUtils.isCreatable(batchMsgCount)) {
            return Double.valueOf(batchMsgCount).intValue();
        }
        return 0;
    }

    public static void main(String[] args) {
        String hl7BatchMsg = "FHS|^~\\&|MESA|XYZ_HOSPITAL|IHIE|IHIE|20120703094005||||||\n" +
                "BHS|^~\\&|MESA|XYZ_HOSPITAL|IHIE|IHIE|20120703094005||||||\n" +
                "MSH|^~\\&|MESA_ADT|XYZ_ADMITTING|MESA_IS|XYZ_HOSPITAL|||ADT^A04|101102|P|2.3.1||||||||\n" +
                "EVN||200004211000||||200004210950\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||E||||||5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1|||||||||||||||||||||||||200004210950||||||||\n" +
                "MSH|^~\\&|MESA_OP|XYZ_HOSPITAL|MESA_OF|XYZ_RADIOLOGY|||ORM^O01|101104|P|2.3.1||||||||\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||E|$PATIENT_LOCATION$||||$ATTENDING_DOCTOR$|5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1|||||||||||||||||||||||||200004210950||||||||\n" +
                "ORC|NW|A101Z^MESA_ORDPLC|||||1^once^^^^S||200004210955|^ROSEWOOD^RANDOLPH||7101^ESTRADA^JAIME^P^^DR||3145551212|200004210955||922229-10^IHE-RAD^IHE-CODE-231||\n" +
                "OBR|1|A101Z^MESA_ORDPLC||P1^Procedure 1^ERL_MESA|||||||||xxx||Radiology^^^^R|7101^ESTRADA^JAIME^P^^DR|||||||||||1^once^^^^S|||WALK|Project Manager||||||||||A||\n" +
                "MSH|^~\\&|MESA_OF|XYZ_RADIOLOGY|MESA_IM|XYZ_IMAGE_MANAGER|||ORM^O01|$MESSAGE_CONTROL_ID$|P|2.3.1||||||||\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||E||||||5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1|||||||||||||||||||||||||200004210950||||||||\n" +
                "ORC|NW|A101Z^MESA_ORDPLC|B101Z^MESA_ORDFIL||SC||1^once^^^^S||200004210950|^ROSEWOOD^RANDOLPH||7101^ESTRADA^JAIME^P^^DR||3145551212|200004210950||922229-10^IHE-RAD^IHE-CODE-231||\n" +
                "OBR|1|A101Z^MESA_ORDPLC|B101Z^MESA_ORDFIL|P1^Procedure 1^ERL_MESA^X1_A1^SP Action Item X1_A1^DSS_MESA|||||||||xxx||Radiology^^^^R|7101^ESTRADA^JAIME^P^^DR||ACCESSION_NUMBER|REQUESTED_PROCEDURE_ID|SCHEDULED_PROCEDURE_STEP_ID||||MR|||1^once^^^^S|||WALK|||||||||||A||\n" +
                "ZDS|1.2.1^100^Application^DICOM\n" +
                "MSH|^~\\&|MESA_ADT|XYZ_ADMITTING|MESA_IS|XYZ_HOSPITAL|||ADT^A06|101126|P|2.3.1||||||||\n" +
                "EVN||200004211000||||200004210950\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||I|1E^111^1^XYZ_HOSPITAL|||$PRIOR_LOCATION$|1234^WEAVER^TIMOTHY^P^^DR|5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1|||||||||||||||||||||||||200004210950||||||||\n" +
                "MSH|^~\\&|MESA_OF|XYZ_RADIOLOGY|MESA_IM|XYZ_RADIOLOGY|||ADT^A06|101128|P|2.3.1||||||||\n" +
                "EVN||200004211000||||200004210950\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||I|1E^111^1^XYZ_HOSPITAL|||$PRIOR_LOCATION$|1234^WEAVER^TIMOTHY^P^^DR|5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1|||||||||||||||||||||||||200004210950||||||||\n" +
                "MSH|^~\\&|MESA_ADT|XYZ_ADMITTING|MESA_IS|XYZ_HOSPITAL|||ADT^A03|101130|P|2.3.1||||||||\n" +
                "EVN||200004212200||||200004211950\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||I|1E^111^1^XYZ_HOSPITAL||||1234^WEAVER^TIMOTHY^P^^DR|5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1||||||||||||||||||||||||||$DISCHARGE_DATE_TIME$|||||||\n" +
                "MSH|^~\\&|MESA_OF|XYZ_RADIOLOGY|MESA_IM|XYZ_RADIOLOGY|||ADT^A03|101132|P|2.3.1||||||||\n" +
                "EVN||200004212200||||200004211950\n" +
                "PID|||583020^^^ADT1||WHITE^CHARLES||19980704|M||AI|7616 STANFORD AVE^^ST. LOUIS^MO^63130|||||||20-98-1701||||||||||||\n" +
                "PV1||I|1E^111^1^XYZ_HOSPITAL||||1234^WEAVER^TIMOTHY^P^^DR|5101^NELL^FREDERICK^P^^DR|||||||||||V1002^^^ADT1||||||||||||||||||||||||||$DISCHARGE_DATE_TIME$|||||||\n" +
                "MSH|^~\\&|MESA_ADT|XYZ_ADMITTING|MESA_IS|XYZ_HOSPITAL|||ADT^A04|101160|P|2.3.1||||||||\n" +
                "EVN||200004230800||||200004230800\n" +
                "PID|||583220^^^ADT1||DOE^J2|||M||AI||||||||20-98-3210||||||||||||\n" +
                "PV1||E||||||5101^NELL^FREDERICK^P^^DR|||||||||||V1102^^^ADT1|||||||||||||||||||||||||200004230800||||||||\n" +
                "BTS|8|Batch Message Count\n" +
                "FTS|1|Have a Nice Day";
        try {
            List<String> messages = splitHL7Batch(hl7BatchMsg);
            System.out.println("After split. Number of hl5 messages:" + messages.size());
            PipeParser parser = new PipeParser();
            for (String messageString : messages) {
                //System.out.println(messageString);
                try {
                    Message message = parser.parse(messageString);
                    //System.out.println("Parsed message: " + message.printStructure());
                } catch (ca.uhn.hl7v2.parser.EncodingNotSupportedException e) {
                    System.err.println("Error parsing message: " + e.getMessage());
                } catch (HL7Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.err.println("Error splitting the message file: " + e.getMessage());
        }
    }

}
