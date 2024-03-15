package gov.cdc.dataprocessing.repository.nbs.odse.model.material;

import gov.cdc.dataprocessing.model.dto.material.ManufacturedMaterialDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ManufacturedMaterialId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Manufactured_material")
@Data
@IdClass(ManufacturedMaterialId.class)
public class ManufacturedMaterial {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "material_uid")
    private Long materialUid;

    @Id
    @Column(name = "manufactured_material_seq")
    private Integer manufacturedMaterialSeq;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "expiration_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp expirationTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "lot_nm")
    private String lotNm;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp recordStatusTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "stability_from_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp stabilityFromTime;

    @Column(name = "stability_to_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp stabilityToTime;

    @Column(name = "stability_duration_cd")
    private String stabilityDurationCd;

    @Column(name = "stability_duration_unit_cd")
    private String stabilityDurationUnitCd;

    // Define constructors, getters, and setters if needed
    public ManufacturedMaterial() {

    }

    public ManufacturedMaterial(ManufacturedMaterialDto materialDT) {
        this.materialUid = materialDT.getMaterialUid();
        this.manufacturedMaterialSeq = materialDT.getManufacturedMaterialSeq();
        this.addReasonCd = materialDT.getAddReasonCd();
        this.addTime = materialDT.getAddTime();
        this.addUserId = materialDT.getAddUserId();
        this.expirationTime = materialDT.getExpirationTime();
        this.lastChgReasonCd = materialDT.getLastChgReasonCd();
        this.lastChgTime = materialDT.getLastChgTime();
        this.lastChgUserId = materialDT.getLastChgUserId();
        this.lotNm = materialDT.getLotNm();
        this.recordStatusCd = materialDT.getRecordStatusCd();
        this.recordStatusTime = materialDT.getRecordStatusTime();
        this.userAffiliationTxt = materialDT.getUserAffiliationTxt();
        this.stabilityFromTime = materialDT.getStabilityFromTime();
        this.stabilityToTime = materialDT.getStabilityToTime();
        this.stabilityDurationCd = materialDT.getStabilityDurationCd();
        this.stabilityDurationUnitCd = materialDT.getStabilityDurationUnitCd();
        // Other fields
    }

}
