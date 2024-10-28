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
public class NbsMessageStatus {
    private Integer nbsInterfaceId;
    private String nbsInterfacePipeLineStatus;
    private String nbsInterfaceStatus;
    private String nbsInterfacePayload;
    private Timestamp nbsCreatedOn;
    private DltMessageStatus dltInfo;

}
