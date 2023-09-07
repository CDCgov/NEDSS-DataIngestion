package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewAnswerRepeatDto;
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
    private EcrMsgInterviewDto MsgInterview;
    private List<EcrMsgInterviewProviderDto> MsgInterviewProviders;
    private List<EcrMsgInterviewAnswerDto> MsgInterviewAnswers;
    private List<EcrMsgInterviewAnswerRepeatDto> MsgInterviewAnswerRepeats;
}
