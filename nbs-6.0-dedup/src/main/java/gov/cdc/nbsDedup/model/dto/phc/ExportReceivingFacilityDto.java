package gov.cdc.nbsDedup.model.dto.phc;

import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ExportReceivingFacilityDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Timestamp addTime;
    private String reportType;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Long exportReceivingFacilityUid;
    private String recordStatusCd;
    private String receivingSystemNm;
    private String  receivingSystemOid;
    private String receivingSystemShortName;
    private String receivingSystemOwner;
    private String receivingSystemOwnerOid;
    private String receivingSystemDescTxt;
    private String sendingIndCd;
    private String receivingIndCd;
    private String allowTransferIndCd;
    private String adminComment;

    private String sendingIndDescTxt;
    private String receivingIndDescTxt;
    private String allowTransferIndDescTxt;
    private String reportTypeDescTxt;

    private String recordStatusCdDescTxt;

    private String jurDeriveIndCd;
}
