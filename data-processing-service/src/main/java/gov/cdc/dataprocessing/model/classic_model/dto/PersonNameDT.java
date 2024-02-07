package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
public class PersonNameDT
        extends AbstractVO {
    private Long personUid;
    private Integer personNameSeq;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp asOfDate;
    private String defaultNmInd;
    private String durationAmt;
    private String durationUnitCd;
    private String firstNm;
    private String firstNmSndx;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String lastNm;
    private String lastNmSndx;
    private String lastNm2;
    private String lastNm2Sndx;
    private String middleNm;
    private String middleNm2;
    private String nmDegree;
    private String nmPrefix;
    private String nmSuffix;
    private String nmSuffixCd;
    private String nmUseCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private Timestamp toTime;
    private String userAffiliationTxt;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;
    private Integer versionCtrlNbr;
    private String localId;
}
