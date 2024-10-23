package gov.cdc.dataprocessing.repository.nbs.odse.model.question;

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
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class QuestionMetadata {
    private Long nbsQuestionUid;
    private Timestamp addTime;
    private Long addUserId;
    private String codeSetGroupId;
    private String dataType;
    private String mask;
    private String investigationFormCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String questionLabel;
    private String questionToolTip;
    private Integer questionVersionNbr;
    private String tabId;
    private boolean enableInd;
    private Integer orderNbr;
    private String defaultValue;
    private boolean requiredInd;
    private boolean displayInd;
    private String coinfectionIndCd;
    private String nndMetadataUid;
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
    private Integer fieldSize;
    private String parentUid;
    private String ldfPageId;
    private Long nbsUiMetadataUid;
    private Long nbsUiComponentUid;
    private String unitTypeCd;
    private String unitValue;
    private String nbsTableUid;
    private String partTypeCd;
    private String standardNndIndCd;
    private String subGroupNm;
    private String hl7SegmentField;
    private Integer questionGroupSeqNbr;
    private String questionUnitIdentifier;

    // Getters and setters

    public QuestionMetadata(Object[] data) {
        if (data.length >= 1) {
            this.nbsQuestionUid = ((Double) data[0]).longValue();
        }
        if (data.length >= 2) {
            this.addTime = Timestamp.valueOf((String) data[1]);
        }
        if (data.length >= 3) {
            this.addUserId = ((Double) data[2]).longValue();
        }
        if (data.length >= 4) {
            this.codeSetGroupId = (String) data[3];
        }
        if (data.length >= 5) {
            this.dataType = (String) data[4];
        }
        if (data.length >= 6) {
            this.mask = (String) data[5];
        }
        if (data.length >= 7) {
            this.investigationFormCd = (String) data[6];
        }
        if (data.length >= 8) {
            this.lastChgTime = Timestamp.valueOf((String) data[7]);
        }
        if (data.length >= 9) {
            this.lastChgUserId =  ((Double) data[8]).longValue();
        }
        if (data.length >= 10) {
            this.questionLabel = (String) data[9];
        }
        if (data.length >= 11) {
            this.questionToolTip = (String) data[10];
        }
        if (data.length >= 12) {
            this.questionVersionNbr = (Integer) data[11];
        }
        if (data.length >= 13) {
            this.tabId = (String) data[12];
        }
        if (data.length >= 14) {
            this.enableInd = (boolean) data[13];
        }
        if (data.length >= 15) {
            this.orderNbr = (Integer) data[14];
        }
        if (data.length >= 16) {
            this.defaultValue = (String) data[15];
        }
        if (data.length >= 17) {
            this.requiredInd = (boolean) data[16];
        }
        if (data.length >= 18) {
            this.displayInd = (boolean) data[17];
        }
        if (data.length >= 19) {
            this.coinfectionIndCd = (String) data[18];
        }
        if (data.length >= 20) {
            this.nndMetadataUid = (String) data[19];
        }
        if (data.length >= 21) {
            this.questionIdentifier = (String) data[20];
        }
        if (data.length >= 22) {
            this.questionIdentifierNnd = (String) data[21];
        }
        if (data.length >= 23) {
            this.questionRequiredNnd = (String) data[22];
        }
        if (data.length >= 24) {
            this.questionOid = (String) data[23];
        }
        if (data.length >= 25) {
            this.questionOidSystemTxt = (String) data[24];
        }
        if (data.length >= 26) {
            this.codeSetNm = (String) data[25];
        }
        if (data.length >= 27) {
            this.codeSetClassCd = (String) data[26];
        }
        if (data.length >= 28) {
            this.dataLocation = (String) data[27];
        }
        if (data.length >= 29) {
            this.dataCd = (String) data[28];
        }
        if (data.length >= 30) {
            this.dataUseCd = (String) data[29];
        }
        if (data.length >= 31) {
            this.fieldSize = (Integer) data[30];
        }
        if (data.length >= 32) {
            this.parentUid = (String) data[31];
        }
        if (data.length >= 33) {
            this.ldfPageId = (String) data[32];
        }
        if (data.length >= 34) {
            this.nbsUiMetadataUid = (Long) data[33];
        }
        if (data.length >= 35) {
            this.nbsUiComponentUid = (Long) data[34];
        }
        if (data.length >= 36) {
            this.unitTypeCd = (String) data[35];
        }
        if (data.length >= 37) {
            this.unitValue = (String) data[36];
        }
        if (data.length >= 38) {
            this.nbsTableUid = (String) data[37];
        }
        if (data.length >= 39) {
            this.partTypeCd = (String) data[38];
        }
        if (data.length >= 40) {
            this.standardNndIndCd = (String) data[39];
        }
        if (data.length >= 41) {
            this.subGroupNm = (String) data[40];
        }
        if (data.length >= 42) {
            this.hl7SegmentField = (String) data[41];
        }
        if (data.length >= 43) {
            this.questionGroupSeqNbr = (Integer) data[42];
        }
        if (data.length >= 44) {
            this.questionUnitIdentifier = (String) data[43];
        }
    }
    public QuestionMetadata() {

    }

}
