package gov.cdc.dataingestion.nbs.ecr.model.interview;

import gov.cdc.nedss.phdc.cda.POCDMT000040Component3;
import gov.cdc.nedss.phdc.cda.POCDMT000040Observation;
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
public class InterviewEntryRelation {
    POCDMT000040Component3 out;
    int c;
    POCDMT000040Observation obs;
}
