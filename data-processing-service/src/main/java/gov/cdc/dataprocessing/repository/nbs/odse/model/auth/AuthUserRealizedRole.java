package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AuthUserRealizedRole {
    private String permSetNm;
    private Long authUserRoleUid;
    private String authRoleNm;
    private String progAreaCd;
    private String jurisdictionCd;
    private Long authUserUid;
    private Long authPermSetUid;
    private String roleGuestInd;
    private String readOnlyInd;
    private Integer dispSeqNbr;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;

    public AuthUserRealizedRole() {

    }
}
