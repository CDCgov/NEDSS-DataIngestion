package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ExportReceivingFacilityDT extends AbstractVO {
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
