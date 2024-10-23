package gov.cdc.dataprocessing.repository.nbs.odse.model.dsm;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "dsm_algorithm")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class DsmAlgorithm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dsm_algorithm_uid")
    private Long dsmAlgorithmUid;

    @Column(name = "algorithm_nm")
    private String algorithmNm;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "condition_list")
    private String conditionList;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "apply_to")
    private String applyTo;

    @Column(name = "sending_system_list")
    private String sendingSystemList;

    @Column(name = "reporting_system_list")
    private String reportingSystemList;

    @Column(name = "event_action")
    private String eventAction;

    @Column(name = "algorithm_payload", columnDefinition = "text")
    private String algorithmPayload;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "resulted_test_list")
    private String resultedTestList;
}