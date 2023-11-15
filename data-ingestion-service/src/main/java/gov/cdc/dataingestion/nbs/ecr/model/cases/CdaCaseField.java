package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.nedss.phdc.cda.POCDMT000040StructuredBody;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaCaseField {
    POCDMT000040StructuredBody output;
    int repeats;
    int componentCaseCounter;
    int counter;
}
