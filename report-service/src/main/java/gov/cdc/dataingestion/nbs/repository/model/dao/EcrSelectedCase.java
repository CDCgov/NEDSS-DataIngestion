package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerDto;
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
    private EcrMsgCaseDto msgCase;
    private List<EcrMsgCaseParticipantDto> msgCaseParticipants = new ArrayList<>();
    private List<EcrMsgCaseAnswerDto> msgCaseAnswers = new ArrayList<>();
    private List<EcrMsgCaseAnswerDto> msgCaseAnswerRepeats = new ArrayList<>();

}
