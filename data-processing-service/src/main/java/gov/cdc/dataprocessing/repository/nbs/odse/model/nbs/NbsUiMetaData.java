package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;


@Entity
@Table(name = "NBS_ui_metadata")
@Data

public class NbsUiMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nbs_ui_metadata_uid")
    private Long id;

    @Column(name = "nbs_question_uid")
    private Long questionUid;

    @Column(name = "parent_uid")
    private Long parentUid;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "default_value", length = 2000)
    private String defaultValue;

    @Column(name = "display_ind", length = 1)
    private String displayInd;

    @Column(name = "enable_ind", length = 1)
    private String enableInd;

    @Column(name = "field_size", length = 10)
    private String fieldSize;

    @Column(name = "investigation_form_cd", length = 50)
    private String investigationFormCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "ldf_page_id", length = 20)
    private String ldfPageId;

    @Column(name = "order_nbr")
    private Integer orderNbr;

    @Column(name = "question_label", length = 300)
    private String questionLabel;

    @Column(name = "question_tool_tip", length = 2000)
    private String questionToolTip;

    @Column(name = "required_ind", length = 2)
    private String requiredInd;

    @Column(name = "tab_order_id")
    private Integer tabOrderId;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    @Column(name = "nbs_table_uid")
    private Long tableUid;

    @Column(name = "code_set_group_id")
    private Long codeSetGroupId;

    @Column(name = "data_cd", length = 50)
    private String dataCd;

    @Column(name = "data_type", length = 20)
    private String dataType;

    @Column(name = "data_use_cd", length = 20)
    private String dataUseCd;


    @Column(name = "part_type_cd", length = 50)
    private String partTypeCd;

    @Column(name = "question_group_seq_nbr")
    private Integer questionGroupSeqNbr;

    @Column(name = "question_identifier", length = 50)
    private String questionIdentifier;

    @Column(name = "question_oid", length = 150)
    private String questionOid;

    @Column(name = "question_oid_system_txt", length = 100)
    private String questionOidSystemTxt;

    @Column(name = "question_unit_identifier", length = 20)
    private String questionUnitIdentifier;

    @Column(name = "sub_group_nm", length = 50)
    private String subGroupNm;

    @Column(name = "mask", length = 50)
    private String mask;

    @Column(name = "standard_nnd_ind_cd", length = 1)
    private String standardNndIndCd;

    @Column(name = "unit_type_cd", length = 20)
    private String unitTypeCd;

    @Column(name = "unit_value", length = 50)
    private String unitValue;

    @Column(name = "coinfection_ind_cd", length = 1)
    private String coinfectionIndCd;

    private Long nndMetadataUid;
    private String questionIdentifierNnd;
    private String questionRequiredNnd;
    private String codeSetNm;
    private String codeSetClassCd;
    private Long nbsUiComponentUid;
    private String hl7SegmentField;
    private String dataLocation;


}
