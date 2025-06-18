package gov.cdc.dataingestion.share.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
}
