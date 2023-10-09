package gov.cdc.dataingestion.nbs.ecr.model.cases;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerRepeatDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaCaseMultiGroupSeqNumber {
    POCDMT000040Section out;

    int sectionCounter;
    int answerGroupSeqNbr;
    int answerGroupCounter;
    int questionGroupSeqNbr;
    int questionGroupCounter;
    int componentCounter;
}
