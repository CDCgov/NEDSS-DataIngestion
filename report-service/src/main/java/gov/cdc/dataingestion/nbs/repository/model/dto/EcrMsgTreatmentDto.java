package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgTreatmentDto {
    private String trtLocalId;
    private String trtAuthorId;
    private String trtCompositeCd;
    private String trtCommentTxt;
    private String trtCustomTreatmentTxt;
    private Integer trtDosageAmt;
    private String trtDosageUnitCd;
    private String trtDrugCd;
    private Integer trtDurationAmt;
    private String trtDurationUnitCd;
    private Timestamp trtEffectiveTime;
    private String trtFrequencyAmtCd;
    private String trtRouteCd;
    private Timestamp trtTreatmentDt;

}
