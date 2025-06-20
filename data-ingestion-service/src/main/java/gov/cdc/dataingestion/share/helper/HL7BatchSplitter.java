package gov.cdc.dataingestion.share.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HL7BatchSplitter {
    private HL7BatchSplitter() {
    }

    public static List<String> splitHL7Batch(String batchHL7Msg) {
        List<String> hl7Messages = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        log.info("BTS value:" + StringUtils.substringBetween(batchHL7Msg, "BTS|", "|"));
        //Check if input message has batch - BTS|5|Batch Message Count
        boolean isBatchFile = isHl7Batch(batchHL7Msg);
        log.info("isBatchFile:" + isBatchFile);
        if (isBatchFile) {
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
        } else {
            log.info("This is not a batch file. No ELR batch split is needed.");
            hl7Messages.add(batchHL7Msg);
        }
        return hl7Messages;
    }

    private static boolean isHl7Batch(String batchHL7Msg) {
        return StringUtils.contains(batchHL7Msg, "FHS|")
                && StringUtils.contains(batchHL7Msg, "BHS|");
    }
}