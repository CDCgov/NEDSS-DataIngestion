package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MessageStatus {
    private RawMessageStatus rawInfo = new RawMessageStatus();
    private ValidatedMessageStatus validatedInfo = new ValidatedMessageStatus();
    private NbsMessageStatus nbsInfo = new NbsMessageStatus();
    private List<EdxActivityLogStatus> nbsIngestionInfo=new ArrayList<>();
}
