package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EdxActivityLogStatus {
    private String recordType;
    private String logType;
    private String logComment;
    private Timestamp recordStatusTime;
}
