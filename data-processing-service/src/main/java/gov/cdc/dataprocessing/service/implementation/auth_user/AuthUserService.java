package gov.cdc.dataprocessing.service.implementation.auth_user;

import gov.cdc.dataprocessing.cache.AuthUserProfile;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
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
public class AuthUserService implements IAuthUserService {
    AuthUserRepository authUserRepository;
    CustomAuthUserRepository customAuthUserRepository;

    private final JdbcTemplate jdbcTemplateOdse;
    private static final String SELECT_AUTH_USER_BY_USER_ID = """
        SELECT
            auth_user_uid, user_id, user_type, user_title, user_department, user_first_nm, user_last_nm,
            user_work_email, user_work_phone, user_mobile_phone, master_sec_admin_ind, prog_area_admin_ind,
            nedss_entry_id, external_org_uid, user_password, user_comments, add_time, add_user_id,
            last_chg_time, last_chg_user_id, record_status_cd, record_status_time, jurisdiction_derivation_ind,
            provider_uid
        FROM Auth_user
        WHERE user_id = ?
    """;
    public AuthUserService(AuthUserRepository authUserRepository,
                           CustomAuthUserRepository customAuthUserRepository,
                           @Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplateOdse) {
        this.authUserRepository = authUserRepository;
        this.customAuthUserRepository = customAuthUserRepository;
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }


    public AuthUserProfileInfo getAuthUserInfo(String authUserId) throws DataProcessingException {
        if (AuthUserProfile.authUserProfileInfo  == null) {
            var authUser = this.findAuthUserByUserId(authUserId);
            AuthUserProfileInfo authUserData;
            if (authUser != null && authUser.isPresent())  // NOSONAR
            {
                authUserData = new AuthUserProfileInfo();
                authUserData.setAuthUser(authUser.get());
                var authUserRoleRes = this.customAuthUserRepository.getAuthUserRealizedRole(authUserId);
                authUserData.setAuthUserRealizedRoleCollection(authUserRoleRes);
            }
            else {
                throw new DataProcessingException("Auth User Not Found");
            }
            AuthUserProfile.authUserProfileInfo = authUserData; //NOSONAR
        }

        return AuthUserProfile.authUserProfileInfo;
    }


    @SuppressWarnings("java:S1874")
    private Optional<AuthUser> findAuthUserByUserId(String userId) {
        try {
            AuthUser user = jdbcTemplateOdse.queryForObject(
                    SELECT_AUTH_USER_BY_USER_ID,
                    new Object[]{userId},
                    (rs, rowNum) -> mapRowToAuthUser(rs)
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // No result found
        }
    }

    private AuthUser mapRowToAuthUser(ResultSet rs) throws SQLException {
        AuthUser authUser = new AuthUser();
        authUser.setAuthUserUid(rs.getLong("auth_user_uid"));
        authUser.setUserId(rs.getString("user_id"));
        authUser.setUserType(rs.getString("user_type"));
        authUser.setUserTitle(rs.getString("user_title"));
        authUser.setUserDepartment(rs.getString("user_department"));
        authUser.setUserFirstNm(rs.getString("user_first_nm"));
        authUser.setUserLastNm(rs.getString("user_last_nm"));
        authUser.setUserWorkEmail(rs.getString("user_work_email"));
        authUser.setUserWorkPhone(rs.getString("user_work_phone"));
        authUser.setUserMobilePhone(rs.getString("user_mobile_phone"));
        authUser.setMasterSecAdminInd(rs.getString("master_sec_admin_ind"));
        authUser.setProgAreaAdminInd(rs.getString("prog_area_admin_ind"));
        authUser.setNedssEntryId(rs.getLong("nedss_entry_id"));
        authUser.setExternalOrgUid(rs.getLong("external_org_uid"));
        authUser.setUserPassword(rs.getString("user_password"));
        authUser.setUserComments(rs.getString("user_comments"));
        authUser.setAddTime(rs.getTimestamp("add_time"));
        authUser.setAddUserId(rs.getLong("add_user_id"));
        authUser.setLastChgTime(rs.getTimestamp("last_chg_time"));
        authUser.setLastChgUserId(rs.getLong("last_chg_user_id"));
        authUser.setRecordStatusCd(rs.getString("record_status_cd"));
        authUser.setRecordStatusTime(rs.getTimestamp("record_status_time"));
        authUser.setJurisdictionDerivationInd(rs.getString("jurisdiction_derivation_ind"));
        authUser.setProviderUid(rs.getLong("provider_uid"));
        return authUser;
    }

}