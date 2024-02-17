package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EdxPatientMatchDT extends AbstractVO {

    private Long edxPatientMatchUid;
    private Long patientUid;
    private String matchString;
    private String typeCd;
    private Long matchStringHashCode;

    private Long addUserId;
    private Long lastChgUserId;
    private Timestamp addTime;
    private Timestamp lastChgTime;
    private boolean multipleMatch = false;


}
