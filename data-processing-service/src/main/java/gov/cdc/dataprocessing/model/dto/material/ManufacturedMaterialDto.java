package gov.cdc.dataprocessing.model.dto.material;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.ManufacturedMaterial;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
public class ManufacturedMaterialDto extends BaseContainer
{

    private static final long serialVersionUID = 1L;

    private Long materialUid;

    private Integer manufacturedMaterialSeq;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private Timestamp expirationTime;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String lotNm;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private String userAffiliationTxt;

    private Timestamp stabilityFromTime;

    private Timestamp stabilityToTime;

    private String stabilityDurationCd;

    private String stabilityDurationUnitCd;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;


    public ManufacturedMaterialDto() {

        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public ManufacturedMaterialDto(ManufacturedMaterial material) {
        this.materialUid = material.getMaterialUid();
        this.manufacturedMaterialSeq = material.getManufacturedMaterialSeq();
        this.addReasonCd = material.getAddReasonCd();
        this.addTime = material.getAddTime();
        this.addUserId = material.getAddUserId();
        this.expirationTime = material.getExpirationTime();
        this.lastChgReasonCd = material.getLastChgReasonCd();
        this.lastChgTime = material.getLastChgTime();
        this.lastChgUserId = material.getLastChgUserId();
        this.lotNm = material.getLotNm();
        this.recordStatusCd = material.getRecordStatusCd();
        this.recordStatusTime = material.getRecordStatusTime();
        this.userAffiliationTxt = material.getUserAffiliationTxt();
        this.stabilityFromTime = material.getStabilityFromTime();
        this.stabilityToTime = material.getStabilityToTime();
        this.stabilityDurationCd = material.getStabilityDurationCd();
        this.stabilityDurationUnitCd = material.getStabilityDurationUnitCd();
        // Other fields
    }


}
