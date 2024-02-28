package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ParticipationDT extends AbstractVO {
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String awarenessCd;
    private String awarenessDescTxt;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private String typeCd;
    private Timestamp toTime;
    private String typeDescTxt;
    private String userAffiliationTxt;
    private String subjectEntityClassCd;


    private Long subjectEntityUid;

    private Integer roleSeq;
    private String cd;
    private String actClassCd;
    private String subjectClassCd;
    private Long actUid;
}
