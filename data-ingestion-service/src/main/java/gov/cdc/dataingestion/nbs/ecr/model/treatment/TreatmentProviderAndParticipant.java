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
public class TreatmentProviderAndParticipant {
    POCDMT000040SubstanceAdministration output;
    int org;
    int provider;
}
