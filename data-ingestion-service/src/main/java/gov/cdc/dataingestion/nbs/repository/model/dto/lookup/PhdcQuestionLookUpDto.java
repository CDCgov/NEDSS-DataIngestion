package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
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
