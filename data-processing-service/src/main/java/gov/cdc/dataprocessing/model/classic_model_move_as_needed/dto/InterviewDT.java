package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class InterviewDT extends BaseContainer {
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

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }
}
