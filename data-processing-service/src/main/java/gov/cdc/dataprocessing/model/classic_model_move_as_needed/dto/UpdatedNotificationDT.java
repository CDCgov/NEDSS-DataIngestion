package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UpdatedNotificationDT
        extends BaseContainer {
    private static final long serialVersionUID = 1L;

    private Long notificationUid;
    private boolean caseStatusChg = false;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Integer versionCtrlNbr;
    private String statusCd;
    private String caseClassCd;

}
