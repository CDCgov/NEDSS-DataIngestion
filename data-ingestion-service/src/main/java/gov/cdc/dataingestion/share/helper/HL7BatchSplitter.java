package gov.cdc.dataingestion.share.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HL7BatchSplitter {
    private static final String MSH="MSH|";
    private static final String FHS="FHS|";
    private static final String BHS="BHS|";
    private static final String BTS="BTS|";
    private static final String FTS="FTS|";

    private HL7BatchSplitter() {
    }

    public static List<String> splitHL7Batch(String batchHL7Msg) {
        List<String> hl7Messages = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        //Check if input message has batch - BTS|5|Batch Message Count
        boolean isBatchFile = isHl7Batch(batchHL7Msg);
        log.info("isBatchFile:" + isBatchFile);
        if (isBatchFile) {
            String[] msgAllLines = batchHL7Msg.split("\\R");
            for (String line : msgAllLines) {
                if (line.startsWith(MSH)) {
                    if (!currentMessage.isEmpty()) {
                        hl7Messages.add(currentMessage.toString());
                    }
                    currentMessage = new StringBuilder(line + "\n");
                } else if (!line.startsWith(FHS) && !line.startsWith(BHS) && !line.startsWith(BTS) && !line.startsWith(FTS)) {
                    currentMessage.append(line).append("\n");
                }
            }
            if (!currentMessage.isEmpty()) {
                hl7Messages.add(currentMessage.toString());
            }
        } else {
            log.info("This is not a batch file, or there is only one message. No ELR batch split is needed.");
            hl7Messages.add(batchHL7Msg);
        }
        return hl7Messages;
    }

    private static boolean isHl7Batch(String batchHL7Msg) {
        int firstIndex= batchHL7Msg.indexOf(MSH);
        if(firstIndex!=-1){
            int secondIndex= batchHL7Msg.indexOf(MSH,firstIndex+MSH.length());
            return secondIndex > firstIndex;
        }
        return false;
    }
}