package gov.cdc.dataprocessing.model.dto;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.model.MetaAndWaCommonAttribute;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

}
