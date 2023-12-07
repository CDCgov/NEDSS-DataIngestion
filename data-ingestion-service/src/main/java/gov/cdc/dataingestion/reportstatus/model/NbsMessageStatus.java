package gov.cdc.dataingestion.reportstatus.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NbsMessageStatus {
    private Integer nbsInterfaceId;
    private String nbsInterfacePipeLineStatus;
    private String nbsInterfaceStatus;
    private String nbsInterfacePayload;
    private Timestamp nbsCreatedOn;
    private DltMessageStatus dltInfo;

}
