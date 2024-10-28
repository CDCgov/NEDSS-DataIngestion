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
public class DltMessageStatus {
    private String dltId;
    private String dltStatus;
    private String dltOrigin;
    private Timestamp dltCreatedOn;
    private String dltShortTrace;
}
