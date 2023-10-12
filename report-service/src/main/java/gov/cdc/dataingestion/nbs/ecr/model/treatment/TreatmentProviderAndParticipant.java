package gov.cdc.dataingestion.nbs.ecr.model.treatment;

import gov.cdc.nedss.phdc.cda.POCDMT000040SubstanceAdministration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TreatmentProviderAndParticipant {
    POCDMT000040SubstanceAdministration output;
    int org;
    int provider;
}
