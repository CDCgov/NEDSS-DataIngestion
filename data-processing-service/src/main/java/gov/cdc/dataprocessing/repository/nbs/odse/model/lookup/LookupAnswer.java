package gov.cdc.dataprocessing.repository.nbs.odse.model.lookup;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "LOOKUP_ANSWER")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class LookupAnswer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_ANSWER_UID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOOKUP_QUESTION_UID", referencedColumnName = "LOOKUP_QUESTION_uid")
    private LookupQuestion lookupQuestion;

    @Column(name = "FROM_ANSWER_CODE")
    private String fromAnswerCode;

    @Column(name = "FROM_ANS_DISPLAY_NM")
    private String fromAnsDisplayNm;

    @Column(name = "FROM_CODE_SYSTEM_CD")
    private String fromCodeSystemCd;

    @Column(name = "FROM_CODE_SYSTEM_DESC_TXT")
    private String fromCodeSystemDescTxt;

    @Column(name = "TO_ANSWER_CODE")
    private String toAnswerCode;

    @Column(name = "TO_ANS_DISPLAY_NM")
    private String toAnsDisplayNm;

    @Column(name = "TO_CODE_SYSTEM_CD")
    private String toCodeSystemCd;

    @Column(name = "TO_CODE_SYSTEM_DESC_TXT")
    private String toCodeSystemDescTxt;

    @Column(name = "ADD_TIME")
    private Timestamp addTime;

    @Column(name = "ADD_USER_ID")
    private Long addUserId;

    @Column(name = "LAST_CHG_TIME")
    private Timestamp lastChgTime;

    @Column(name = "LAST_CHG_USER_ID")
    private Long lastChgUserId;

    @Column(name = "STATUS_CD")
    private String statusCd;

    @Column(name = "STATUS_TIME")
    private Timestamp statusTime;

    // Getters and setters
}