package gov.cdc.dataprocessing.repository.nbs.odse.model.question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "WA_question")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class WAQuestion {
    @Id
    @Column(name = "wa_question_uid")
    private Long waQuestionUid;

    @Column(name = "code_set_group_id")
    private Long codeSetGroupId;

    @Column(name = "data_cd")
    private String dataCd;

    @Column(name = "data_location")
    private String dataLocation;

    @Column(name = "question_identifier")
    private String questionIdentifier;

    @Column(name = "question_oid")
    private String questionOid;

    @Column(name = "question_oid_system_txt")
    private String questionOidSystemTxt;

    @Column(name = "question_unit_identifier")
    private String questionUnitIdentifier;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "data_use_cd")
    private String dataUseCd;

    @Column(name = "question_tool_tip")
    private String questionToolTip;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    @Column(name = "question_group_seq_nbr")
    private Integer questionGroupSeqNbr;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "question_nm")
    private String questionNm;

    @Column(name = "mask")
    private String mask;

    @Column(name = "field_size")
    private String fieldSize;

    @Column(name = "standard_nnd_ind_cd")
    private String standardNndIndCd;

    @Column(name = "unit_type_cd")
    private String unitTypeCd;

    @Column(name = "unit_value")
    private String unitValue;

    @Column(name = "coinfection_ind_cd")
    private String coinfectionIndCd;

    private String investigationFormCd;
    private String codeSetNm;
    private String codeSetClassCd;
    private Long nbsUiComponentUid;
}