package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040Section;
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
public class MultiSelect {
    private Integer answerGroupCounter;
    private Integer questionGroupCounter;
    private Integer sectionCounter;
    private POCDMT000040Section component;

}
