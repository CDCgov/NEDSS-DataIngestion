package gov.cdc.dataprocessing.service.model.lookup_data;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsUiMetaData;
import gov.cdc.dataprocessing.repository.nbs.odse.model.question.WAQuestion;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class MetaAndWaCommonAttribute {
    private Long id;
    private Long questionUid;
    private Long parentUid;
    private Timestamp addTime;
    private Long addUserId;
    private String defaultValue;
    private String displayInd;
    private String enableInd;
    private String fieldSize;
    private String investigationFormCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String ldfPageId;
    private Integer orderNbr;
    private String questionLabel;
    private String questionToolTip;
    private String requiredInd;
    private Integer tabOrderId;
    private Integer versionCtrlNbr;
    private Long tableUid;
    private Long codeSetGroupId;
    private String dataCd;
    private String dataType;
    private String dataUseCd;
    private String partTypeCd;
    private Integer questionGroupSeqNbr;
    private String questionIdentifier;
    private String questionOid;
    private String questionOidSystemTxt;
    private String questionUnitIdentifier;
    private String subGroupNm;
    private String mask;
    private String standardNndIndCd;
    private String unitTypeCd;
    private String unitValue;
    private String coinfectionIndCd;
    private String questionIdentifierNnd;
    private String questionRequiredNnd;
    private String codeSetNm;
    private String codeSetClassCd;
    private Long nbsUiComponentUid;
    private String hl7SegmentField;
    private String dataLocation;

    public MetaAndWaCommonAttribute() {
        // Default constructor
    }

    // Constructor to parse WAQuestion object into CommonAttributes
    public MetaAndWaCommonAttribute(WAQuestion waQuestion) {
        this.dataLocation = waQuestion.getDataLocation();
        this.questionUid = waQuestion.getWaQuestionUid();
        this.addUserId = waQuestion.getAddUserId();
        this.addTime = waQuestion.getAddTime();
        this.defaultValue = waQuestion.getDefaultValue();
        this.fieldSize = waQuestion.getFieldSize();
        this.investigationFormCd = waQuestion.getInvestigationFormCd();
        this.lastChgTime = waQuestion.getLastChgTime();
        this.lastChgUserId = waQuestion.getLastChgUserId();
        this.questionToolTip = waQuestion.getQuestionToolTip();
        this.versionCtrlNbr = waQuestion.getVersionCtrlNbr();
        this.codeSetGroupId = waQuestion.getCodeSetGroupId();
        this.dataCd = waQuestion.getDataCd();
        this.dataType = waQuestion.getDataType();
        this.dataUseCd = waQuestion.getDataUseCd();
        this.questionGroupSeqNbr = waQuestion.getQuestionGroupSeqNbr();
        this.questionIdentifier = waQuestion.getQuestionIdentifier();
        this.questionOid = waQuestion.getQuestionOid();
        this.questionOidSystemTxt = waQuestion.getQuestionOidSystemTxt();
        this.questionUnitIdentifier = waQuestion.getQuestionUnitIdentifier();
        this.mask = waQuestion.getMask();
        this.standardNndIndCd = waQuestion.getStandardNndIndCd();
        this.unitTypeCd = waQuestion.getUnitTypeCd();
        this.unitValue = waQuestion.getUnitValue();
        this.coinfectionIndCd = waQuestion.getCoinfectionIndCd();
        this.codeSetNm = waQuestion.getCodeSetNm();
        this.codeSetClassCd = waQuestion.getCodeSetClassCd();
        this.nbsUiComponentUid = waQuestion.getNbsUiComponentUid();
    }

    // Constructor to parse NbsUiMetaData object into CommonAttributes
    public MetaAndWaCommonAttribute(NbsUiMetaData nbsUiMetaData) {
        this.dataLocation = nbsUiMetaData.getDataLocation();
        this.id = nbsUiMetaData.getId();
        this.questionUid = nbsUiMetaData.getQuestionUid();
        this.parentUid = nbsUiMetaData.getParentUid();
        this.addTime = nbsUiMetaData.getAddTime();
        this.addUserId = nbsUiMetaData.getAddUserId();
        this.defaultValue = nbsUiMetaData.getDefaultValue();
        this.displayInd = nbsUiMetaData.getDisplayInd();
        this.enableInd = nbsUiMetaData.getEnableInd();
        this.fieldSize = nbsUiMetaData.getFieldSize();
        this.investigationFormCd = nbsUiMetaData.getInvestigationFormCd();
        this.lastChgTime = nbsUiMetaData.getLastChgTime();
        this.lastChgUserId = nbsUiMetaData.getLastChgUserId();
        this.ldfPageId = nbsUiMetaData.getLdfPageId();
        this.orderNbr = nbsUiMetaData.getOrderNbr();
        this.questionLabel = nbsUiMetaData.getQuestionLabel();
        this.questionToolTip = nbsUiMetaData.getQuestionToolTip();
        this.requiredInd = nbsUiMetaData.getRequiredInd();
        this.tabOrderId = nbsUiMetaData.getTabOrderId();
        this.versionCtrlNbr = nbsUiMetaData.getVersionCtrlNbr();
        this.codeSetGroupId = nbsUiMetaData.getCodeSetGroupId();
        this.dataCd = nbsUiMetaData.getDataCd();
        this.dataType = nbsUiMetaData.getDataType();
        this.dataUseCd = nbsUiMetaData.getDataUseCd();
        this.partTypeCd = nbsUiMetaData.getPartTypeCd();
        this.questionGroupSeqNbr = nbsUiMetaData.getQuestionGroupSeqNbr();
        this.questionIdentifier = nbsUiMetaData.getQuestionIdentifier();
        this.questionOid = nbsUiMetaData.getQuestionOid();
        this.questionOidSystemTxt = nbsUiMetaData.getQuestionOidSystemTxt();
        this.questionUnitIdentifier = nbsUiMetaData.getQuestionUnitIdentifier();
        this.subGroupNm = nbsUiMetaData.getSubGroupNm();
        this.mask = nbsUiMetaData.getMask();
        this.standardNndIndCd = nbsUiMetaData.getStandardNndIndCd();
        this.unitTypeCd = nbsUiMetaData.getUnitTypeCd();
        this.unitValue = nbsUiMetaData.getUnitValue();
        this.coinfectionIndCd = nbsUiMetaData.getCoinfectionIndCd();
        this.questionIdentifierNnd = nbsUiMetaData.getQuestionIdentifierNnd();
        this.questionRequiredNnd = nbsUiMetaData.getQuestionRequiredNnd();
        this.codeSetNm = nbsUiMetaData.getCodeSetNm();
        this.codeSetClassCd = nbsUiMetaData.getCodeSetClassCd();
        this.nbsUiComponentUid = nbsUiMetaData.getNbsUiComponentUid();
        this.hl7SegmentField = nbsUiMetaData.getHl7SegmentField();
    }
}
