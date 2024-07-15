package gov.cdc.dataprocessing.model.dto.other;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalFieldsDT {
    private Long nbsUiMetadataUid;
    private String questionLabel;
    private String typeCdDesc;
    private Integer orderNbr;
    private Long nbsQuestionUid;
    private Long parentUid;
    private String tab;
    private String section;
    private String subSection;
    private String viewLink;
    private String editLink;
    private String deleteLink;

}
