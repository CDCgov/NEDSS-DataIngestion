package gov.cdc.dataprocessing.model.dto;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NbsQuestionMetadata extends BaseContainer {

    private static final long serialVersionUID = 1L;
    private Long nbsQuestionUid;
    private Timestamp addTime;
    private Long addUserId;
    private Long codeSetGroupId;
    private String dataType;
    private String investigationFormCd;
    private String templateType;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Integer orderNbr;
    private String questionLabel;
    private String questionToolTip;
    private String statusCd;
    private Timestamp statusTime;
    private Integer tabId;
    private Integer questionVersionNbr;
    private Long nndMetadataUid;
    private String questionIdentifier;
    private String questionIdentifierNnd;
    private String questionRequiredNnd;
    private String questionOid;
    private String questionOidSystemTxt;
    private String codeSetNm;
    private String codeSetClassCd;
    private String dataLocation;
    private String dataCd;
    private String dataUseCd;
    private String enableInd;
    private String defaultValue;
    private String requiredInd;
    private Long  parentUid;
    private String ldfPageId;
    private Long nbsUiMetadataUid;
    private Long nbsUiComponentUid;
    private Long nbsTableUid;
    private String fieldSize;
    private String futureDateInd;
    private String displayInd;
    private String jspSnippetCreateEdit;
    private String jspSnippetView;
    private String unitTypeCd;
    private String unitValue;
    private String standardNndIndCd;
    private String hl7SegmentField;
    private Integer questionGroupSeqNbr;
    private String partTypeCd;
    private String questionUnitIdentifier;
    private String mask;
    private String subGroupNm;
    private String coinfectionIndCd;
}
