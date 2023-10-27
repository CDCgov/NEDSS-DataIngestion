package gov.cdc.dataingestion.nbs.ecr.model.patient;

import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaPatientTelecom {
    private POCDMT000040ClinicalDocument1 clinicalDocument;
    private int phoneCounter;
    private String wpNumber;
}
