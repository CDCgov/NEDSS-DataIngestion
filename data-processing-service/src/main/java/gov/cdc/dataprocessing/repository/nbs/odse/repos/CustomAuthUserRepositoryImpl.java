package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class CustomAuthUserRepositoryImpl implements CustomAuthUserRepository {
    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;

    private String SELECT_REALIZED_ROLES_FOR_USER_ID = "" +
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
            " and UPPER(SU.user_id) = UPPER(:userId)";

    public CustomAuthUserRepositoryImpl() {
        // For Unit Test
    }

    public Collection<AuthUserRealizedRole> getAuthUserRealizedRole(String userId) {
        Query query = entityManager.createNativeQuery(SELECT_REALIZED_ROLES_FOR_USER_ID);
        Collection<AuthUserRealizedRole> authUserRealizedRoles = new ArrayList<>();
        query.setParameter("userId", userId);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var result : results) {
                AuthUserRealizedRole container = new AuthUserRealizedRole();
                container.setPermSetNm((String) result[0]);
                container.setAuthUserRoleUid((Long) result[1]);
                container.setAuthRoleNm((String) result[2]);
                container.setProgAreaCd((String) result[3]);
                container.setJurisdictionCd((String) result[4]);
                container.setAuthUserUid((Long) result[5]);
                container.setAuthPermSetUid((Long) result[6]);
                container.setRoleGuestInd( ((Character) result[7]).toString());
                container.setReadOnlyInd(((Character) result[8]).toString());
                container.setDispSeqNbr((Integer) result[9]);
                container.setAddTime((Timestamp) result[10]);
                container.setAddUserId((Long) result[11]);
                container.setLastChgTime((Timestamp) result[12]);
                container.setLastChgUserId((Long) result[13]);
                container.setRecordStatusCd((String) result[14]);
                container.setRecordStatusTime((Timestamp) result[15]);
                authUserRealizedRoles.add(container);
            }
        }
        return authUserRealizedRoles;
    }
}
