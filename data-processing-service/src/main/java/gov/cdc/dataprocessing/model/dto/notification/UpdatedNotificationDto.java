package gov.cdc.dataprocessing.model.dto.notification;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UpdatedNotificationDto
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
