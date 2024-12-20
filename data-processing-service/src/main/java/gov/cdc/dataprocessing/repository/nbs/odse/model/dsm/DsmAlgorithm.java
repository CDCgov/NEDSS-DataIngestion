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
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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