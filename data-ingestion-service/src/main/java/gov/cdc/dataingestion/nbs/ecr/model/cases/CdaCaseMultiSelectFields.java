package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.nedss.phdc.cda.POCDMT000040Section;
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
public class CdaCaseMultiSelectFields {
    POCDMT000040Section out;
    int sectionCounter;
    int componentCounter;
    String questionIdentifier;
}
