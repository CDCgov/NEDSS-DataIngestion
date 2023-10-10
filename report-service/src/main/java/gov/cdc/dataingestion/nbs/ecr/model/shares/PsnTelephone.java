package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PsnTelephone {
    POCDMT000040Participant2 out;
    int teleCounter;
}
