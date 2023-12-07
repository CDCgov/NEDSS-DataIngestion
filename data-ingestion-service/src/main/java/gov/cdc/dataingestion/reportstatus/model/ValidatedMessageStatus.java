package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ValidatedMessageStatus {
    private String validatedMessageId;
    private String validatedMessage;
    private Timestamp validatedCreatedOn;
    private String validatedPipeLineStatus;
    private DltMessageStatus dltInfo;

}
