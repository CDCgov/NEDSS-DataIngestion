package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040SubstanceAdministration;
import gov.cdc.nedss.phdc.cda.StrucDocText;
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
public class CdaTreatmentAdministrationMapper {
    POCDMT000040SubstanceAdministration administration;
    StrucDocText text;
}
