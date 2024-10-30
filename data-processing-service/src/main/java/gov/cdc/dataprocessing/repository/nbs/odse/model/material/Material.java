package gov.cdc.dataprocessing.repository.nbs.odse.model.material;

import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;


@Data
@Entity
@Table(name = "Material")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
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

    public Material(MaterialDto materialDto) {
        this.materialUid = materialDto.getMaterialUid();
        this.addReasonCd = materialDto.getAddReasonCd();
        this.addTime = materialDto.getAddTime();
        this.addUserId = materialDto.getAddUserId();
        this.cd = materialDto.getCd();
        this.cdDescTxt = materialDto.getCdDescTxt();
        this.description = materialDto.getDescription();
        this.effectiveDurationAmt = materialDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = materialDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = materialDto.getEffectiveFromTime();
        this.effectiveToTime = materialDto.getEffectiveToTime();
        this.formCd = materialDto.getFormCd();
        this.formDescTxt = materialDto.getFormDescTxt();
        this.handlingCd = materialDto.getHandlingCd();
        this.handlingDescTxt = materialDto.getHandlingDescTxt();
        this.lastChgReasonCd = materialDto.getLastChgReasonCd();
        this.lastChgTime = materialDto.getLastChgTime();
        this.lastChgUserId = materialDto.getLastChgUserId();
        this.localId = materialDto.getLocalId();
        this.nm = materialDto.getNm();
        this.qty = materialDto.getQty();
        this.qtyUnitCd = materialDto.getQtyUnitCd();
        this.recordStatusCd = materialDto.getRecordStatusCd();
        this.recordStatusTime = materialDto.getRecordStatusTime();
        this.riskCd = materialDto.getRiskCd();
        this.riskDescTxt = materialDto.getRiskDescTxt();
        this.statusCd = materialDto.getStatusCd();
        this.statusTime = materialDto.getStatusTime();
        this.userAffiliationTxt = materialDto.getUserAffiliationTxt();
        this.versionCtrlNbr = materialDto.getVersionCtrlNbr();
    }

}
