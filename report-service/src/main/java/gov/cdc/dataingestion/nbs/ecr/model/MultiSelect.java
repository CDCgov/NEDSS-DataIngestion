package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MultiSelect {
    private Integer answerGroupCounter;
    private Integer questionGroupCounter;
    private Integer sectionCounter;
    private POCDMT000040Section component;

}
