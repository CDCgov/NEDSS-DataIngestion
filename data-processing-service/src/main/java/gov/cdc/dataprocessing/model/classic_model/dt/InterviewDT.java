package gov.cdc.dataprocessing.model.classic_model.dt;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class InterviewDT extends AbstractVO {
    private static final long serialVersionUID = 1L;


    private Long interviewUid;

    private String intervieweeRoleCd;

    private Timestamp interviewDate;

    private String interviewTypeCd;

    private String interviewStatusCd;

    private String interviewLocCd;

    private Timestamp addTime;

    private Long addUserId;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String localId;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private Integer versionCtrlNbr;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    private String addUserName;

    private String lastChgUserName;

    private boolean associated;
}
