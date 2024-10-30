package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.model.lookup_data.MetaAndWaCommonAttribute;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
    List<CodeValueGeneral> aList = new ArrayList<>();

    public NbsQuestionMetadata() {

    }

    public NbsQuestionMetadata(MetaAndWaCommonAttribute commonAttributes) {
        this.dataLocation = commonAttributes.getDataLocation();
        this.nbsQuestionUid = commonAttributes.getQuestionUid();
        this.addTime = commonAttributes.getAddTime();
        this.addUserId = commonAttributes.getAddUserId();
        this.codeSetGroupId = commonAttributes.getCodeSetGroupId();
        this.dataType = commonAttributes.getDataType();
        this.investigationFormCd = commonAttributes.getInvestigationFormCd();
        this.lastChgTime = commonAttributes.getLastChgTime();
        this.lastChgUserId = commonAttributes.getLastChgUserId();
        this.orderNbr = commonAttributes.getOrderNbr();
        this.questionLabel = commonAttributes.getQuestionLabel();
        this.questionToolTip = commonAttributes.getQuestionToolTip();
        this.questionIdentifier = commonAttributes.getQuestionIdentifier();
        this.questionIdentifierNnd = commonAttributes.getQuestionIdentifierNnd();
        this.questionRequiredNnd = commonAttributes.getQuestionRequiredNnd();
        this.questionOid = commonAttributes.getQuestionOid();
        this.questionOidSystemTxt = commonAttributes.getQuestionOidSystemTxt();
        this.codeSetNm = commonAttributes.getCodeSetNm();
        this.codeSetClassCd = commonAttributes.getCodeSetClassCd();
        this.dataCd = commonAttributes.getDataCd();
        this.dataUseCd = commonAttributes.getDataUseCd();
        this.enableInd = commonAttributes.getEnableInd();
        this.defaultValue = commonAttributes.getDefaultValue();
        this.requiredInd = commonAttributes.getRequiredInd();
        this.parentUid = commonAttributes.getParentUid();
        this.ldfPageId = commonAttributes.getLdfPageId();
        this.nbsUiComponentUid = commonAttributes.getNbsUiComponentUid();
        this.fieldSize = commonAttributes.getFieldSize();
        this.displayInd = commonAttributes.getDisplayInd();
        this.unitTypeCd = commonAttributes.getUnitTypeCd();
        this.unitValue = commonAttributes.getUnitValue();
        this.standardNndIndCd = commonAttributes.getStandardNndIndCd();
        this.hl7SegmentField = commonAttributes.getHl7SegmentField();
        this.questionGroupSeqNbr = commonAttributes.getQuestionGroupSeqNbr();
        this.partTypeCd = commonAttributes.getPartTypeCd();
        this.questionUnitIdentifier = commonAttributes.getQuestionUnitIdentifier();
        this.mask = commonAttributes.getMask();
        this.subGroupNm = commonAttributes.getSubGroupNm();
        this.coinfectionIndCd = commonAttributes.getCoinfectionIndCd();
    }


    public NbsQuestionMetadata(QuestionRequiredNnd data) {
        this.nbsQuestionUid = data.getNbsQuestionUid();
        this.questionIdentifier = data.getQuestionIdentifier();
        this.questionLabel = data.getQuestionLabel();
        this.dataLocation = data.getDataLocation();
    }
}
