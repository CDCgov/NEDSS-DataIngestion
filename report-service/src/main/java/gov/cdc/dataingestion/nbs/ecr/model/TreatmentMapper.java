package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040SubstanceAdministration;
import gov.cdc.nedss.phdc.cda.StrucDocText;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TreatmentMapper {
    private POCDMT000040SubstanceAdministration component;
    private StrucDocText list;
}
