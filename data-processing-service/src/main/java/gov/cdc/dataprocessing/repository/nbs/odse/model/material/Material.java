package gov.cdc.dataprocessing.repository.nbs.odse.model.material;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MaterialDT;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;


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
    private Timestamp addTime;

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
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

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
    private Timestamp lastChgTime;

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
    private Timestamp recordStatusTime;

    @Column(name = "risk_cd")
    private String riskCd;

    @Column(name = "risk_desc_txt")
    private String riskDescTxt;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    // Relationships if needed
    public Material() {

    }

    public Material(MaterialDT materialDT) {
        this.materialUid = materialDT.getMaterialUid();
        this.addReasonCd = materialDT.getAddReasonCd();
        this.addTime = materialDT.getAddTime();
        this.addUserId = materialDT.getAddUserId();
        this.cd = materialDT.getCd();
        this.cdDescTxt = materialDT.getCdDescTxt();
        this.description = materialDT.getDescription();
        this.effectiveDurationAmt = materialDT.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = materialDT.getEffectiveDurationUnitCd();
        this.effectiveFromTime = materialDT.getEffectiveFromTime();
        this.effectiveToTime = materialDT.getEffectiveToTime();
        this.formCd = materialDT.getFormCd();
        this.formDescTxt = materialDT.getFormDescTxt();
        this.handlingCd = materialDT.getHandlingCd();
        this.handlingDescTxt = materialDT.getHandlingDescTxt();
        this.lastChgReasonCd = materialDT.getLastChgReasonCd();
        this.lastChgTime = materialDT.getLastChgTime();
        this.lastChgUserId = materialDT.getLastChgUserId();
        this.localId = materialDT.getLocalId();
        this.nm = materialDT.getNm();
        this.qty = materialDT.getQty();
        this.qtyUnitCd = materialDT.getQtyUnitCd();
        this.recordStatusCd = materialDT.getRecordStatusCd();
        this.recordStatusTime = materialDT.getRecordStatusTime();
        this.riskCd = materialDT.getRiskCd();
        this.riskDescTxt = materialDT.getRiskDescTxt();
        this.statusCd = materialDT.getStatusCd();
        this.statusTime = materialDT.getStatusTime();
        this.userAffiliationTxt = materialDT.getUserAffiliationTxt();
        this.versionCtrlNbr = materialDT.getVersionCtrlNbr();
    }

}
