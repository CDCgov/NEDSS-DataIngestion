package gov.cdc.dataprocessing.model.dto.other;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NbsRdbMetadataDT {
    private Long nbsRdbMetadataUid;
    private Long nbsPageUid;
    private Long nbsUiMetadataUid;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Long lastChgUserId;
    private Timestamp lastChgTime;
    private String localId;
    private String rptAdminColumnNm;
    private String rdbTableNm;
    private String userDefinedColumnNm;
    private String rdbColumnNm;
    private Integer dataMartRepeatNbr;


}
