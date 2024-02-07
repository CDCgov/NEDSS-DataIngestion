package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EntityIdDT extends AbstractVO {
    private Long entityUid;

    private Integer entityIdSeq;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private Timestamp asOfDate;

    private String assigningAuthorityCd;

    private String assigningAuthorityDescTxt;

    private String durationAmt;

    private String durationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private String rootExtensionTxt;

    private String statusCd;

    private Timestamp statusTime;

    private String typeCd;

    private String typeDescTxt;

    private String userAffiliationTxt;

    private Timestamp validFromTime;

    private Timestamp validToTime;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;
    //   private String universalIdType;

    private String assigningAuthorityIdType;
}
