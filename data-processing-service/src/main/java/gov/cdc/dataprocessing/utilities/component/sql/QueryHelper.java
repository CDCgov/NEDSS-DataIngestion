package gov.cdc.dataprocessing.utilities.component.sql;

import gov.cdc.dataprocessing.model.dto.auth_user.RealizedRoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jurisdiction.ProgAreaJurisdictionUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PROGRAM_JUS_OID;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.QUERY_HELPER_1;

@Component
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

//        boolean paSecured = NBSBOLookup.isSecuredByProgramArea(
//                businessObjLookupName);
//
//        boolean jSecured = NBSBOLookup.isSecuredByJurisdiction(
//                businessObjLookupName);

//        boolean paSecured = true;
//        boolean jSecured = true;

//        if (paSecured && jSecured)
//        {
            columnName = PROGRAM_JUS_OID;
            ownerList = getHashedPAJList(businessObjLookupName, operation, false);
            guestList = getHashedPAJList(businessObjLookupName, operation, true);
            whereClause = buildWhereClause(ownerList, guestList, columnName, alias,true, businessObjLookupName);
//          }
//        else if (paSecured || jSecured) {
//            //If the record is secured by program area only, do the following
//            if (paSecured) {
//                columnName = "prog_area_cd";
//                ownerList = getPAList(businessObjLookupName, operation, false);
//
//                guestList = getPAList(businessObjLookupName, operation, true);
//                whereClause = buildWhereClause(ownerList, guestList,
//                        columnName, alias, false, businessObjLookupName);
//            }
//
//            //if the record is secured by jurisdiction only, do the following
//            else if (jSecured) {
//                columnName = "jurisdiction_cd";
//                ownerList = getJList(businessObjLookupName, operation, false);
//                guestList = getJList(businessObjLookupName, operation, true);
//                whereClause = buildWhereClause(ownerList, guestList,
//                        columnName, alias, false, businessObjLookupName);
//            }
//        }

        //If the record is not secured by program area or jurisdiction, do this
//        else if (!paSecured && !jSecured) {
//            whereClause = null;
//        }

        return whereClause;
    }

    @SuppressWarnings("java:S1172")
    private String getHashedPAJList(String businessObjLookupName, String operation, boolean guest) {
        Collection<Object> allPAJList = new HashSet<>();
        StringBuilder hashedPAJList = new StringBuilder();


        for (AuthUserRealizedRole authUserRealizedRole : AuthUtil.authUserRealizedRoleCollection) {
            RealizedRoleDto rRole = new RealizedRoleDto(authUserRealizedRole);

            if (rRole.isGuest() == guest) { //only consider roles that match the requested guest status
                boolean isOpAvailable = true;
                //logger.debug("rRole.getRoleName() = " + rRole.getRoleName());


                if (isOpAvailable) {
                    String paCd = rRole.getProgramAreaCode();
                    String jCd = rRole.getJurisdictionCode();
                    Collection<Object> pajCds = progAreaJurisdictionUtil.getPAJHashList(paCd, jCd);
                    allPAJList.addAll(pajCds);
                }
            }
        }

        for (Object o : allPAJList) {
            Long cd = (Long) o;
            if (cd != null && cd.toString().trim().length() != 0) {
                hashedPAJList = hashedPAJList.append(cd).append(", ");
            }
        }


        if (hashedPAJList.toString().trim().length() > 0) {
            return hashedPAJList.toString().trim().substring(0, (hashedPAJList.toString().trim().length() - 1));
        }
        else {
            return hashedPAJList.toString();
        }
    }

    public String buildWhereClause(String ownerList, String guestList,
                                   String columnName, String alias, boolean OIDFlag, String businessObjLookupName) {

        String whereClauseOwner = buildOwnerWhereClause(ownerList, columnName, alias, OIDFlag, businessObjLookupName);
        String whereClauseGuest = buildGuestWhereClause(guestList, columnName, alias, OIDFlag, businessObjLookupName);

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
                                 String alias, boolean OIDFlag, String businessObjLookupName) {
        String whereClauseOwner = "";


        if (ownerList != null && ownerList.trim().length() != 0) {

            if (alias == null || alias.trim().length() == 0) {
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
                                         String alias, boolean OIDFlag, String businessObjLookupName) {

        //logger.debug("alias = " + alias);
        String whereClauseGuest = "";

        if (guestList != null && guestList.trim().length() != 0) {
            if (alias == null || alias.trim().length() == 0) {
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
