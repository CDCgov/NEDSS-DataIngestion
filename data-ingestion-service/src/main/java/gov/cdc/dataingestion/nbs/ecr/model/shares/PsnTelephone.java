package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
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
public class PsnTelephone {
    POCDMT000040Participant2 out;
    int teleCounter;
}
