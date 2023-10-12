package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import gov.cdc.nedss.phdc.cda.POCDMT000040SubstanceAdministration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MapParticipantRole {
    POCDMT000040SubstanceAdministration output;
    POCDMT000040Participant2 participant2;
    int c;
}
