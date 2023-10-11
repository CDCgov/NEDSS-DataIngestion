package gov.cdc.dataingestion.nbs.repository.model.dao;


import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewProviderDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EcrSelectedInterview {
    private EcrMsgInterviewDto msgInterview;
    private List<EcrMsgInterviewProviderDto> msgInterviewProviders;
    private List<EcrMsgCaseAnswerDto> msgInterviewAnswers;
    private List<EcrMsgCaseAnswerDto> msgInterviewAnswerRepeats;
}
