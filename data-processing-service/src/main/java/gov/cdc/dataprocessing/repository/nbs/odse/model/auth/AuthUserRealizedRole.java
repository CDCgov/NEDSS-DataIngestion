package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import lombok.Data;

import java.sql.Timestamp;

@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
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
