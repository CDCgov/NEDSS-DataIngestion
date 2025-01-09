package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;

@Getter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class MessageStatus {
    private RawMessageStatus rawInfo = new RawMessageStatus();
    private ValidatedMessageStatus validatedInfo = new ValidatedMessageStatus();
    private NbsMessageStatus nbsInfo = new NbsMessageStatus();
//    private List<EdxActivityLogStatus> nbsIngestionInfo=new ArrayList<>();
    private EdxLogStatus edxLogStatus = new EdxLogStatus();
}
