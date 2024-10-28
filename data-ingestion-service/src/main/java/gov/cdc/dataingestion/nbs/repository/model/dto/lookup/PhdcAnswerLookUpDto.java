package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

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
public class PhdcAnswerLookUpDto {
    private String ansFromCode;
    private String ansFromCodeSystemCd;
    private String ansFromCodeSystemDescTxt;
    private String ansFromDisplayNm;
    private String ansToCode;
    private String ansToCodeSystemCd;
    private String ansToCodeSystemDescTxt;
    private String ansToDisplayNm;
    private String codeTranslationRequired;
    private String docTypeCd;
    private String docTypeVersionTxt;
    private String quesCodeSystemCd;
    private String questionIdentifier;
    private String sendingSystemCd;
}
