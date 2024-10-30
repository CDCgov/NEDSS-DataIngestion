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
