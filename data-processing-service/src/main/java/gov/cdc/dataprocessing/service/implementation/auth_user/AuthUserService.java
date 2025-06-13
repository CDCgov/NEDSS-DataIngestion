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

import static gov.cdc.dataprocessing.constant.data_field.*;

@Service

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
        authUser.setAddTime(rs.getTimestamp(ADD_TIME_DB));
        authUser.setAddUserId(rs.getLong(ADD_USER_ID_DB));
        authUser.setLastChgTime(rs.getTimestamp(LAST_CHG_TIME_DB));
        authUser.setLastChgUserId(rs.getLong(LAST_CHG_USER_ID_DB));
        authUser.setRecordStatusCd(rs.getString(RECORD_STATUS_CD_DB));
        authUser.setRecordStatusTime(rs.getTimestamp(RECORD_STATUS_TIME_DB));
        authUser.setJurisdictionDerivationInd(rs.getString("jurisdiction_derivation_ind"));
        authUser.setProviderUid(rs.getLong("provider_uid"));
        return authUser;
    }

}