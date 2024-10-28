package gov.cdc.dataingestion.nbs.ecr.model.provider;

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
public class ProviderFieldCheck {
    POCDMT000040Section clinicalDocument;
    String inv168;
    int performerSectionCounter;
    int performerComponentCounter;
    int componentCounter;
}
