package gov.cdc.nbsDedup.model.dto.material;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.nbs.odse.model.material.ManufacturedMaterial;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
