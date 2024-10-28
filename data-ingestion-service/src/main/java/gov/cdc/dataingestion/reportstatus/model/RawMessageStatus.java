package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class RawMessageStatus {
    private String rawMessageId;
    private String rawPayload;
    private String rawCreatedBy;
    private Timestamp rawCreatedOn;
    private String rawPipeLineStatus;
    private DltMessageStatus dltInfo;

}
