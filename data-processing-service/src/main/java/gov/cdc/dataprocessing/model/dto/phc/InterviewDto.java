package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class InterviewDto extends BaseContainer {
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


    private String addUserName;

    private String lastChgUserName;

    private boolean associated;

    public InterviewDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }
}
