package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class MessageStatus {
    private RawMessageStatus rawInfo = new RawMessageStatus();
    private ValidatedMessageStatus validatedInfo = new ValidatedMessageStatus();
    private NbsMessageStatus nbsInfo = new NbsMessageStatus();
    private List<EdxActivityLogStatus> nbsIngestionInfo=new ArrayList<>();
}
