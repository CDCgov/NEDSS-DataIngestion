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
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class NbsMessageStatus {
    private Integer nbsInterfaceId;
    private String nbsInterfacePipeLineStatus;
    private String nbsInterfaceStatus;
    private String nbsInterfacePayload;
    private Timestamp nbsCreatedOn;
    private DltMessageStatus dltInfo;

}
