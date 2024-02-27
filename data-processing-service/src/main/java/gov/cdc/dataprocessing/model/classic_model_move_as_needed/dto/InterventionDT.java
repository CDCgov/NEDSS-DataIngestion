package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class InterventionDT extends AbstractVO {
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

}
