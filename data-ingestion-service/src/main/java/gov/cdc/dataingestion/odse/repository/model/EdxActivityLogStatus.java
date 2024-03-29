package gov.cdc.dataingestion.odse.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxActivityLogStatus {
    private String recordId;
    private String recordType;
    private String logType;
    private String logComment;
}
