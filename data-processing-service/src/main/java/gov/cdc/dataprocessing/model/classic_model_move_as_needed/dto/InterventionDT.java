package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.RootDtoInterface;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.intervention.Intervention;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class InterventionDT extends AbstractVO implements RootDtoInterface {
    private static final long serialVersionUID = 1L;


    private Long interventionUid;

    private String activityDurationAmt;

    private String activityDurationUnitCd;

    private Timestamp activityFromTime;

    private Timestamp activityToTime;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String cd;

    private String cdDescTxt;

    private String cdSystemCd;

    private String cdSystemDescTxt;

    private String classCd;

    private String confidentialityCd;

    private String confidentialityDescTxt;

    private String effectiveDurationAmt;

    private String effectiveDurationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String jurisdictionCd;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String localId;

    private String methodCd;

    private String methodDescTxt;

    private String progAreaCd;

    private String priorityCd;

    private String priorityDescTxt;

    private String qtyAmt;

    private String qtyUnitCd;

    private String reasonCd;

    private String reasonDescTxt;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private Integer repeatNbr;

    private String statusCd;

    private Timestamp statusTime;

    private String targetSiteCd;

    private String targetSiteDescTxt;

    private String txt;

    private String userAffiliationTxt;

    private Long programJurisdictionOid;

    private String sharedInd;

    private Integer versionCtrlNbr;

    private String materialCd;

    private Integer ageAtVacc;

    private String ageAtVaccUnitCd;

    private String vaccMfgrCd;

    private String materialLotNm;

    private Timestamp materialExpirationTime;

    private Integer vaccDoseNbr;

    private String vaccInfoSourceCd;

    private String electronicInd;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return interventionUid;
    }

    public InterventionDT() {

    }

    public InterventionDT(Intervention domain) {
        this.interventionUid = domain.getInterventionUid();
        this.activityDurationAmt = domain.getActivityDurationAmt();
        this.activityDurationUnitCd = domain.getActivityDurationUnitCd();
        this.activityFromTime = domain.getActivityFromTime();
        this.activityToTime = domain.getActivityToTime();
        this.addReasonCd = domain.getAddReasonCd();
        this.addTime = domain.getAddTime();
        this.addUserId = domain.getAddUserId();
        this.cd = domain.getCd();
        this.cdDescTxt = domain.getCdDescTxt();
        this.cdSystemCd = domain.getCdSystemCd();
        this.cdSystemDescTxt = domain.getCdSystemDescTxt();
        this.classCd = domain.getClassCd();
        this.confidentialityCd = domain.getConfidentialityCd();
        this.confidentialityDescTxt = domain.getConfidentialityDescTxt();
        this.effectiveDurationAmt = domain.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = domain.getEffectiveDurationUnitCd();
        this.effectiveFromTime = domain.getEffectiveFromTime();
        this.effectiveToTime = domain.getEffectiveToTime();
        this.jurisdictionCd = domain.getJurisdictionCd();
        this.lastChgReasonCd = domain.getLastChgReasonCd();
        this.lastChgTime = domain.getLastChgTime();
        this.lastChgUserId = domain.getLastChgUserId();
        this.localId = domain.getLocalId();
        this.methodCd = domain.getMethodCd();
        this.methodDescTxt = domain.getMethodDescTxt();
        this.progAreaCd = domain.getProgAreaCd();
        this.priorityCd = domain.getPriorityCd();
        this.priorityDescTxt = domain.getPriorityDescTxt();
        this.qtyAmt = domain.getQtyAmt();
        this.qtyUnitCd = domain.getQtyUnitCd();
        this.reasonCd = domain.getReasonCd();
        this.reasonDescTxt = domain.getReasonDescTxt();
        this.recordStatusCd = domain.getRecordStatusCd();
        this.recordStatusTime = domain.getRecordStatusTime();
        this.repeatNbr = domain.getRepeatNbr();
        this.statusCd = domain.getStatusCd();
        this.statusTime = domain.getStatusTime();
        this.targetSiteCd = domain.getTargetSiteCd();
        this.targetSiteDescTxt = domain.getTargetSiteDescTxt();
        this.txt = domain.getTxt();
        this.userAffiliationTxt = domain.getUserAffiliationTxt();
        this.programJurisdictionOid = domain.getProgramJurisdictionOid();
        this.sharedInd = domain.getSharedInd();
        this.versionCtrlNbr = domain.getVersionCtrlNbr();
        this.materialCd = domain.getMaterialCd();
        this.ageAtVacc = domain.getAgeAtVacc();
        this.ageAtVaccUnitCd = domain.getAgeAtVaccUnitCd();
        this.vaccMfgrCd = domain.getVaccMfgrCd();
        this.materialLotNm = domain.getMaterialLotNm();
        this.materialExpirationTime = domain.getMaterialExpirationTime();
        this.vaccDoseNbr = domain.getVaccDoseNbr();
        this.vaccInfoSourceCd = domain.getVaccInfoSourceCd();
        this.electronicInd = domain.getElectronicInd();
    }


}
