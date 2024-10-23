package gov.cdc.dataprocessing.model.dto.notification;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
