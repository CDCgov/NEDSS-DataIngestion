package gov.cdc.dataprocessing.model.dto.other;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NbsQuestionDT {
    private Long nbsQuestionUid;
    private Timestamp addTime;
    private Long addUserId;
    private Long codeSetGroupId;
    private String dataCd;
    private String dataLocation;
    private String questionIdentifier;
    private String questionOid;
    private String questionOidSystemTxt;
    private String questionUnitIdentifier;
    private String dataType;
    private String dataUseCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String questionLabel;
    private String questionToolTip;
    private String datamartColumnNm;
    private String partTypeCd;
    private String defaultValue;
    private Integer versionCtrlNbr;
    private String unitParentIdentifier;

}
