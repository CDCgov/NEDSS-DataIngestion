package gov.cdc.dataingestion.nbs.ecr.model;

import gov.cdc.nedss.phdc.cda.POCDMT000040Encounter;
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
public class InterviewAnswerMultiMapper {
    private Integer answerGroupCounter;
    private Integer questionGroupCounter;
    private Integer sectionCounter;
    private String questionId;
    private POCDMT000040Encounter component;

}
