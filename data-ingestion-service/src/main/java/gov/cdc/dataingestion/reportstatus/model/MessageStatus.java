package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;

@Getter
public class MessageStatus {
    private RawMessageStatus rawInfo = new RawMessageStatus();
    private ValidatedMessageStatus validatedInfo = new ValidatedMessageStatus();
    private NbsMessageStatus nbsInfo = new NbsMessageStatus();
    private EdxActivityLogStatus odseActivityLogStatus=new EdxActivityLogStatus();
}
