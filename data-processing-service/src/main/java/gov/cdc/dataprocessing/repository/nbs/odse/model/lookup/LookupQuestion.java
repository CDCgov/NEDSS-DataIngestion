package gov.cdc.dataprocessing.repository.nbs.odse.model.lookup;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "LOOKUP_QUESTION")

public class LookupQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_QUESTION_uid")
    private Long id;

    @Column(name = "FROM_QUESTION_IDENTIFIER")
    private String fromQuestionIdentifier;

    @Column(name = "FROM_QUESTION_DISPLAY_NAME")
    private String fromQuestionDisplayName;

    @Column(name = "FROM_CODE_SYSTEM_CD")
    private String fromCodeSystemCd;

    @Column(name = "FROM_CODE_SYSTEM_DESC_TXT")
    private String fromCodeSystemDescTxt;

    @Column(name = "FROM_DATA_TYPE")
    private String fromDataType;

    @Column(name = "FROM_CODE_SET")
    private String fromCodeSet;

    @Column(name = "FROM_FORM_CD")
    private String fromFormCd;

    @Column(name = "TO_QUESTION_IDENTIFIER")
    private String toQuestionIdentifier;

    @Column(name = "TO_QUESTION_DISPLAY_NAME")
    private String toQuestionDisplayName;

    @Column(name = "TO_CODE_SYSTEM_CD")
    private String toCodeSystemCd;

    @Column(name = "TO_CODE_SYSTEM_DESC_TXT")
    private String toCodeSystemDescTxt;

    @Column(name = "TO_DATA_TYPE")
    private String toDataType;

    @Column(name = "TO_CODE_SET")
    private String toCodeSet;

    @Column(name = "TO_FORM_CD")
    private String toFormCd;

    @Column(name = "RDB_COLUMN_NM")
    private String rdbColumnNm;

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