package gov.cdc.dataingestion.nbs.ecr.model.interview;

import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class InterviewTopField {
    POCDMT000040ClinicalDocument1 clinicalDocument;
    int c;
}
