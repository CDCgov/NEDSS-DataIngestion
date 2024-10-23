package gov.cdc.dataprocessing.model.dto.material;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class MaterialDto extends BaseContainer implements RootDtoInterface {

    private Long materialUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cd;
    private String cdDescTxt;
    private String description;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private String formCd;
    private String formDescTxt;
    private String handlingCd;
    private String handlingDescTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String nm;
    private String qty;
    private String qtyUnitCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String riskCd;
    private String riskDescTxt;
    private String statusCd;
    private Timestamp statusTime;
    private String userAffiliationTxt;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;


    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return materialUid;
    }

    public MaterialDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public MaterialDto(Material material) {
        this.materialUid = material.getMaterialUid();
        this.addReasonCd = material.getAddReasonCd();
        this.addTime = material.getAddTime();
        this.addUserId = material.getAddUserId();
        this.cd = material.getCd();
        this.cdDescTxt = material.getCdDescTxt();
        this.description = material.getDescription();
        this.effectiveDurationAmt = material.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = material.getEffectiveDurationUnitCd();
        this.effectiveFromTime = material.getEffectiveFromTime();
        this.effectiveToTime = material.getEffectiveToTime();
        this.formCd = material.getFormCd();
        this.formDescTxt = material.getFormDescTxt();
        this.handlingCd = material.getHandlingCd();
        this.handlingDescTxt = material.getHandlingDescTxt();
        this.lastChgReasonCd = material.getLastChgReasonCd();
        this.lastChgTime = material.getLastChgTime();
        this.lastChgUserId = material.getLastChgUserId();
        this.localId = material.getLocalId();
        this.nm = material.getNm();
        this.qty = material.getQty();
        this.qtyUnitCd = material.getQtyUnitCd();
        this.recordStatusCd = material.getRecordStatusCd();
        this.recordStatusTime = material.getRecordStatusTime();
        this.riskCd = material.getRiskCd();
        this.riskDescTxt = material.getRiskDescTxt();
        this.statusCd = material.getStatusCd();
        this.statusTime = material.getStatusTime();
        this.userAffiliationTxt = material.getUserAffiliationTxt();
        this.versionCtrlNbr = material.getVersionCtrlNbr();
    }

}
