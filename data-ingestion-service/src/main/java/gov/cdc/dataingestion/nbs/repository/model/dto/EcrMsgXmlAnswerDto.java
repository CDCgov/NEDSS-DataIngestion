package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgXmlAnswerDto {
    private String dataType;
    private String answerXmlTxt;
}
