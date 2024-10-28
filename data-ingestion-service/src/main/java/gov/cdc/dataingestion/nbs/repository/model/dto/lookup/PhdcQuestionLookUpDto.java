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
public class PhdcQuestionLookUpDto {
    private String docTypeCd;
    private String docTypeVersionTxt;
    private String quesCodeSystemCd;
    private String quesCodeSystemDescTxt;
    private String dataType;
    private String questionIdentifier;
    private String quesDisplayName;
    private String sectionNm;
    private String sendingSystemCd;
}
