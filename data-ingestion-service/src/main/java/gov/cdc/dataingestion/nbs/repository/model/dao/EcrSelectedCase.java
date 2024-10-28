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
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class EcrSelectedCase {
    private EcrMsgCaseDto msgCase;
    private List<EcrMsgCaseParticipantDto> msgCaseParticipants = new ArrayList<>();
    private List<EcrMsgCaseAnswerDto> msgCaseAnswers = new ArrayList<>();
    private List<EcrMsgCaseAnswerDto> msgCaseAnswerRepeats = new ArrayList<>();

}
