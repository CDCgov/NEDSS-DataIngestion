package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import lombok.Data;

import java.sql.Timestamp;

@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
