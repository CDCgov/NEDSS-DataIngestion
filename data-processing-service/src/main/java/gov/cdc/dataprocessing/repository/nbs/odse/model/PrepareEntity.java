package gov.cdc.dataprocessing.repository.nbs.odse.model;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class PrepareEntity {
    private String localId = null;
    private Long addUserId = null;
    private Timestamp addUserTime = null;
    private String recordStatusState = null;
    private String objectStatusState = null;
}