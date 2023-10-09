package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.nedss.phdc.cda.POCDMT000040Component3;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaCaseAnsQuesIdentifier {
    POCDMT000040Component3 component;
    int counter;
    int sequenceNbr;
    String questionSeq;
}
