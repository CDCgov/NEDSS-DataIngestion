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
public class ValidatedMessageStatus {
    private String validatedMessageId;
    private String validatedMessage;
    private Timestamp validatedCreatedOn;
    private String validatedPipeLineStatus;
    private DltMessageStatus dltInfo;

}
