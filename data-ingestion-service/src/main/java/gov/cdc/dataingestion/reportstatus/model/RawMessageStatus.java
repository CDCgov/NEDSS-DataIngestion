package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class RawMessageStatus {
    private String rawMessageId;
    private String rawPayload;
    private String rawCreatedBy;
    private Timestamp rawCreatedOn;
    private String rawPipeLineStatus;
    private DltMessageStatus dltInfo;

}
