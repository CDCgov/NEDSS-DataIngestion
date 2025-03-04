package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.nedss.phdc.cda.POCDMT000040StructuredBody;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class CdaCaseField {
    POCDMT000040StructuredBody output;
    int repeats;
    int componentCaseCounter;
    int counter;
}
