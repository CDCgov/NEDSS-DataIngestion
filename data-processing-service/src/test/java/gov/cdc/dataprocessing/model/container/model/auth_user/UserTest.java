package gov.cdc.dataprocessing.model.container.model.auth_user;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void testGettersAndSetters() {
        User user = new User();

        // Test inherited boolean fields from BaseContainer
        user.setItNew(true);
        user.setItOld(true);
        user.setItDirty(true);
        user.setItDelete(true);

        assertTrue(user.isItNew());
        assertTrue(user.isItOld());
        assertTrue(user.isItDirty());
        assertTrue(user.isItDelete());

        // Test inherited String field from BaseContainer
        String superClassType = "TestSuperClass";
        user.setSuperClassType(superClassType);
        assertEquals(superClassType, user.getSuperClassType());

        // Test inherited Collection field from BaseContainer
        Collection<Object> ldfs = new ArrayList<>();
        ldfs.add("TestObject");
        user.setLdfs(ldfs);
        assertEquals(ldfs, user.getLdfs());

        // Test User specific fields
        String userID = "userID";
        String firstName = "firstName";
        String lastName = "lastName";
        String comments = "comments";
        String status = "status";
        String entryID = "entryID";
        String password = "password";
        Long reportingFacilityUid = 123L;
        String userType = "userType";
        String facilityDetails = "facilityDetails";
        String readOnly = "readOnly";
        String facilityID = "facilityID";
        Long providerUid = 456L;
        String msa = "msa";
        String paa = "paa";
        String adminUserTypes = "adminUserTypes";
        String paaProgramArea = "paaProgramArea";
        String jurisdictionDerivationInd = "jurisdictionDerivationInd";

        user.setUserID(userID);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setComments(comments);
        user.setStatus(status);
        user.setEntryID(entryID);
        user.setPassword(password);
        user.setReportingFacilityUid(reportingFacilityUid);
        user.setUserType(userType);
        user.setFacilityDetails(facilityDetails);
        user.setReadOnly(readOnly);
        user.setFacilityID(facilityID);
        user.setProviderUid(providerUid);
        user.setMsa(msa);
        user.setPaa(paa);
        user.setAdminUserTypes(adminUserTypes);
        user.setPaaProgramArea(paaProgramArea);
        user.setJurisdictionDerivationInd(jurisdictionDerivationInd);

        assertEquals(userID, user.getUserID());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(comments, user.getComments());
        assertEquals(status, user.getStatus());
        assertEquals(entryID, user.getEntryID());
        assertEquals(password, user.getPassword());
        assertEquals(reportingFacilityUid, user.getReportingFacilityUid());
        assertEquals(userType, user.getUserType());
        assertEquals(facilityDetails, user.getFacilityDetails());
        assertEquals(readOnly, user.getReadOnly());
        assertEquals(facilityID, user.getFacilityID());
        assertEquals(providerUid, user.getProviderUid());
        assertEquals(msa, user.getMsa());
        assertEquals(paa, user.getPaa());
        assertEquals(adminUserTypes, user.getAdminUserTypes());
        assertEquals(paaProgramArea, user.getPaaProgramArea());
        assertEquals(jurisdictionDerivationInd, user.getJurisdictionDerivationInd());
    }

}
