package gov.cdc.dataprocessing.model.dto.dsm;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
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
public class DSMAlgorithmDto extends BaseContainer {
    private static final long serialVersionUID = 4546705321489806575L;
    private Long dsmAlgorithmUid;
    private String algorithmNm;
    private String eventType;
    private String conditionList;
    private String resultedTestList;
    private String frequency;
    private String applyTo;
    private String sendingSystemList;
    private String reportingSystemList;
    private String eventAction;
    private String algorithmPayload;
    private String adminComment;
    private Timestamp statusTime;
    private String statusCd;
    private Long lastChgUserId;
    private Timestamp lastChgTime;

    public DSMAlgorithmDto(DsmAlgorithm dsmAlgorithm) {
        this.dsmAlgorithmUid = dsmAlgorithm.getDsmAlgorithmUid();
        this.algorithmNm = dsmAlgorithm.getAlgorithmNm();
        this.eventType = dsmAlgorithm.getEventType();
        this.conditionList = dsmAlgorithm.getConditionList();
        this.frequency = dsmAlgorithm.getFrequency();
        this.applyTo = dsmAlgorithm.getApplyTo();
        this.sendingSystemList = dsmAlgorithm.getSendingSystemList();
        this.reportingSystemList = dsmAlgorithm.getReportingSystemList();
        this.eventAction = dsmAlgorithm.getEventAction();
        this.algorithmPayload = dsmAlgorithm.getAlgorithmPayload();
        this.adminComment = dsmAlgorithm.getAdminComment();
        this.statusCd = dsmAlgorithm.getStatusCd();
        this.statusTime = dsmAlgorithm.getStatusTime();
        this.lastChgUserId = dsmAlgorithm.getLastChgUserId();
        this.lastChgTime = dsmAlgorithm.getLastChgTime();
        this.resultedTestList = dsmAlgorithm.getResultedTestList();
    }
}
