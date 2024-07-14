package gov.cdc.dataprocessing.repository.nbs.odse.model.dsm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "dsm_algorithm")
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