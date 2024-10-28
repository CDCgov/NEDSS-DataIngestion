package gov.cdc.dataingestion.nbs.ecr.model.treatment;

import gov.cdc.nedss.phdc.cda.POCDMT000040SubstanceAdministration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class TreatmentField {
    String treatmentUid;
    String trtTreatmentDt ;
    String trtFrequencyAmtCd;
    String trtDosageUnitCd;
    String trtDurationAmt;
    String trtDurationUnitCd;
    String treatmentName ;
    String treatmentNameQuestion ;
    String customTreatment ;
    POCDMT000040SubstanceAdministration output;
}
