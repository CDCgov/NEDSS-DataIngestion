package gov.cdc.dataprocessing.utilities.component.sql;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.auth_user.RealizedRoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jurisdiction.ProgAreaJurisdictionUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PROGRAM_JUS_OID;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.QUERY_HELPER_1;

@Component

public class QueryHelper {

    private final ProgAreaJurisdictionUtil progAreaJurisdictionUtil;

    public QueryHelper(ProgAreaJurisdictionUtil progAreaJurisdictionUtil) {
        this.progAreaJurisdictionUtil = progAreaJurisdictionUtil;
    }

    /**
     * OBSERVATIONLABREPORT & OBSERVATIONMORBIDITYREPORT -> SECURE By both prog and jus
     * */
    @SuppressWarnings("java:S125")
    public String getDataAccessWhereClause(String businessObjLookupName, String operation, String alias) {

        String whereClause = null;
        String columnName;
        String ownerList;
        String guestList;

        columnName = PROGRAM_JUS_OID;
        ownerList =  OdseCache.OWNER_LIST_HASHED_PA_J ; //getHashedPAJList(false);
        guestList = OdseCache.GUEST_LIST_HASHED_PA_J ; //getHashedPAJList(true);
        whereClause = buildWhereClause(ownerList, guestList, columnName, alias,true, businessObjLookupName);


        return whereClause;
    }

    public String getHashedPAJList(boolean guest) throws DataProcessingException {
        Set<Long> allPAJList = new HashSet<>();

        for (AuthUserRealizedRole authUserRealizedRole : AuthUtil.authUserRealizedRoleCollection) {
            RealizedRoleDto rRole = new RealizedRoleDto(authUserRealizedRole);

            if (rRole.isGuest() == guest) {
                String paCd = rRole.getProgramAreaCode();
                String jCd = rRole.getJurisdictionCode();
                Collection<Object> pajCds = progAreaJurisdictionUtil.getPAJHashList(paCd, jCd);

                for (Object pajCd : pajCds) {
                    if (pajCd instanceof Long lg) {
                        allPAJList.add(lg);
                    }
                }
            }
        }

        if (allPAJList.isEmpty()) return "";

        StringJoiner joiner = new StringJoiner(", ");
        for (Long cd : allPAJList) {
            if (cd != null) {
                joiner.add(cd.toString());
            }
        }

        return joiner.toString();
    }


    public String buildWhereClause(String ownerList, String guestList,
                                   String columnName, String alias, boolean oIdFlag, String businessObjLookupName) {

        String whereClauseOwner = buildOwnerWhereClause(ownerList, columnName, alias, oIdFlag, businessObjLookupName);
        String whereClauseGuest = buildGuestWhereClause(guestList, columnName, alias, oIdFlag, businessObjLookupName);

        boolean isOwnerClauseValid = whereClauseOwner != null && !whereClauseOwner.trim().isEmpty();
        boolean isGuestClauseValid = whereClauseGuest != null && !whereClauseGuest.trim().isEmpty();

        if (isOwnerClauseValid && isGuestClauseValid) {
            return "(" + whereClauseOwner + " or " + whereClauseGuest + ")";
        }
        else if (isOwnerClauseValid) {
            return "(" + whereClauseOwner + ")";
        }
        else if (isGuestClauseValid) {
            return "(" + whereClauseGuest + ")";
        }
        else {
            return "(0=1)";
        }
    }

    @SuppressWarnings("java:S1172")
   protected String buildOwnerWhereClause(String ownerList, String columnName,
                                 String alias, boolean oIdFlag, String businessObjLookupName) {
        String whereClauseOwner = "";


        if (ownerList != null && !ownerList.trim().isEmpty()) {

            if (alias == null || alias.trim().isEmpty()) {
                whereClauseOwner = "(" + columnName + QUERY_HELPER_1 + ownerList +
                        "))";
            }
            else {
                whereClauseOwner = "(" + alias + "." + columnName + QUERY_HELPER_1 +
                        ownerList + "))";
            }
        }
        else {
            whereClauseOwner = null;
        }

        return whereClauseOwner;
    }


    @SuppressWarnings("java:S1172")
    protected String buildGuestWhereClause(String guestList, String columnName,
                                         String alias, boolean oIdFlag, String businessObjLookupName) {

        String whereClauseGuest = "";

        if (guestList != null && !guestList.trim().isEmpty()) {
            if (alias == null || alias.trim().isEmpty()) {
                whereClauseGuest = "(("+ columnName + QUERY_HELPER_1 + guestList +
                        ")) and  shared_ind = '" +
                        "T" +
                        "')";
            }
            else {
                whereClauseGuest = "((" + alias + "." + columnName +
                        QUERY_HELPER_1 + guestList +
                        ")) and " + alias + ".shared_ind = '" +
                        "T" +
                        "')";
            }
        }
        else {
            whereClauseGuest = null;
        }




        return whereClauseGuest;
    }

}
