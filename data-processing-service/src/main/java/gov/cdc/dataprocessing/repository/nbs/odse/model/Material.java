package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Material")
public class Material {

    @Id
    @Column(name = "material_uid")
    private Long materialUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "description")
    private String description;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "form_cd")
    private String formCd;

    @Column(name = "form_desc_txt")
    private String formDescTxt;

    @Column(name = "handling_cd")
    private String handlingCd;

    @Column(name = "handling_desc_txt")
    private String handlingDescTxt;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Date lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "nm")
    private String nm;

    @Column(name = "qty")
    private String qty;

    @Column(name = "qty_unit_cd")
    private String qtyUnitCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "risk_cd")
    private String riskCd;

    @Column(name = "risk_desc_txt")
    private String riskDescTxt;

    @Column(name = "status_cd")
    private Character statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "version_ctrl_nbr")
    private Short versionCtrlNbr;

    // Relationships if needed
}
