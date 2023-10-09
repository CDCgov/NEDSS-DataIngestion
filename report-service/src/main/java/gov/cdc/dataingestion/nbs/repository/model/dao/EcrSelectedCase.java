package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerRepeatDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseParticipantDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EcrSelectedCase {
    private EcrMsgCaseDto MsgCase;
    private List<EcrMsgCaseParticipantDto> MsgCaseParticipants = new ArrayList<>();
    private List<EcrMsgCaseAnswerDto> MsgCaseAnswers = new ArrayList<>();
    private List<EcrMsgCaseAnswerRepeatDto> MsgCaseAnswerRepeats = new ArrayList<>();

}
