package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
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
public class CdaTreatmentMapper {
    private POCDMT000040ClinicalDocument1 clinicalDocument;
    private int treatmentCounter;
    private int componentCounter;
    private int treatmentSectionCounter;
}
