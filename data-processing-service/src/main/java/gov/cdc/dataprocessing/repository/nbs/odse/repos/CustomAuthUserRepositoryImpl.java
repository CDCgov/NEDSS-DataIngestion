package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
public class CustomAuthUserRepositoryImpl implements CustomAuthUserRepository {
    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplateOdse;


    private static final String SELECT_REALIZED_ROLES_FOR_USER_ID = "" +
            "SELECT PS.perm_set_nm \"permSetNm\" "
            +",SUR.auth_user_role_uid \"authUserRoleUid\" "
            +",SUR.auth_role_nm \"authRoleNm\" "
            +",SUR.prog_area_cd \"progAreaCd\" "
            +",SUR.jurisdiction_cd \"jurisdictionCd\" "
            +",SUR.auth_user_uid \"authUserUid\" "
            +",SUR.auth_perm_set_uid \"authPermSetUid\" "
            +",SUR.role_guest_ind \"roleGuestInd\" "
            +",SUR.read_only_ind \"readOnlyInd\" "
            +",SUR.disp_seq_nbr \"dispSeqNbr\" "
            +",SUR.add_time \"addTime\" "
            +",SUR.add_user_id \"addUserId\" "
            +",SUR.last_chg_time \"lastChgTime\" "
            +",SUR.last_chg_user_id \"lastChgUserId\" "
            +",SUR.record_status_cd \"recordStatusCd\" "
            +",SUR.record_status_time \"recordStatusTime\"  FROM "+
            "AUTH_USER_ROLE" + " SUR, " +  "Auth_perm_set" + " PS, " + "Auth_user" + " SU "+
            " where SUR.auth_user_uid = SU.auth_user_uid "+
            " and PS.auth_perm_set_uid = SUR.auth_perm_set_uid "+
            " and UPPER(SU.user_id) = UPPER(?)";

    public CustomAuthUserRepositoryImpl(@Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplateOdse) {
        // For Unit Test
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public Collection<AuthUserRealizedRole> getAuthUserRealizedRole(String userId) {
        return jdbcTemplateOdse.query(
                SELECT_REALIZED_ROLES_FOR_USER_ID,
                new Object[]{userId},
                (ResultSet rs) -> {
                    Collection<AuthUserRealizedRole> authUserRealizedRoles = new ArrayList<>();
                    while (rs.next()) {
                        authUserRealizedRoles.add(mapRowToAuthUserRealizedRole(rs));
                    }
                    return authUserRealizedRoles;
                }
        );


    }


    private AuthUserRealizedRole mapRowToAuthUserRealizedRole(ResultSet rs) throws SQLException {
        AuthUserRealizedRole container = new AuthUserRealizedRole();
        container.setPermSetNm(rs.getString(1));
        container.setAuthUserRoleUid(rs.getLong(2));
        container.setAuthRoleNm(rs.getString(3));
        container.setProgAreaCd(rs.getString(4));
        container.setJurisdictionCd(rs.getString(5));
        container.setAuthUserUid(rs.getLong(6));
        container.setAuthPermSetUid(rs.getLong(7));
        container.setRoleGuestInd(rs.getString(8));
        container.setReadOnlyInd(rs.getString(9));
        container.setDispSeqNbr(rs.getInt(10));
        container.setAddTime(rs.getTimestamp(11));
        container.setAddUserId(rs.getLong(12));
        container.setLastChgTime(rs.getTimestamp(13));
        container.setLastChgUserId(rs.getLong(14));
        container.setRecordStatusCd(rs.getString(15));
        container.setRecordStatusTime(rs.getTimestamp(16));
        return container;
    }

//    public Collection<AuthUserRealizedRole> getAuthUserRealizedRole(String userId) {
//        Query query = entityManager.createNativeQuery(SELECT_REALIZED_ROLES_FOR_USER_ID);
//        Collection<AuthUserRealizedRole> authUserRealizedRoles = new ArrayList<>();
//        query.setParameter("userId", userId);
//        List<Object[]> results = query.getResultList();
//        if (results != null && !results.isEmpty()) {
//            for(var result : results) {
//                AuthUserRealizedRole container = new AuthUserRealizedRole();
//                container.setPermSetNm((String) result[0]);
//                container.setAuthUserRoleUid((Long) result[1]);
//                container.setAuthRoleNm((String) result[2]);
//                container.setProgAreaCd((String) result[3]);
//                container.setJurisdictionCd((String) result[4]);
//                container.setAuthUserUid((Long) result[5]);
//                container.setAuthPermSetUid((Long) result[6]);
//                container.setRoleGuestInd( ((Character) result[7]).toString());
//                container.setReadOnlyInd(((Character) result[8]).toString());
//                container.setDispSeqNbr((Integer) result[9]);
//                container.setAddTime((Timestamp) result[10]);
//                container.setAddUserId((Long) result[11]);
//                container.setLastChgTime((Timestamp) result[12]);
//                container.setLastChgUserId((Long) result[13]);
//                container.setRecordStatusCd((String) result[14]);
//                container.setRecordStatusTime((Timestamp) result[15]);
//                authUserRealizedRoles.add(container);
//            }
//        }
//        return authUserRealizedRoles;
//    }
}
