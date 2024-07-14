package gov.cdc.dataprocessing.model.dto.other;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NbsPageDT {
    private Long nbsPageUid;
    private Long waTemplateUid;
    private String formCd;
    private String descTxt;
    private byte[] jspPayload;
    private String datamartNm;
    private String localId;
    private String busObjType;
    private Long lastChgUserId;
    private Timestamp lastChgTime;
    private String recordStatusCd;
    private Timestamp recordStatusTime;

}
