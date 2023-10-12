package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.nedss.phdc.cda.POCDMT000040Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaCaseMultiSelectFields {
    POCDMT000040Section out;
    int sectionCounter;
    int componentCounter;
    String questionIdentifier;
}
