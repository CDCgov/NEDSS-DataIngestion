package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class EdxActivityLogStatus {
    private String recordType;
    private String logType;
    private String logComment;
    private String recordStatusTime;
}
