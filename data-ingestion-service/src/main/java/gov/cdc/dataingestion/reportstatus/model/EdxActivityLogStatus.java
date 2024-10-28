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
public class EdxActivityLogStatus {
    private String recordType;
    private String logType;
    private String logComment;
    private Timestamp recordStatusTime;
}
