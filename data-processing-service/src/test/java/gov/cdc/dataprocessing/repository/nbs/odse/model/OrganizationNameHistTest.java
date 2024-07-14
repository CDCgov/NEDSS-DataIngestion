package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationNameHist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationNameHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        OrganizationNameHist organizationNameHist = new OrganizationNameHist();

        // Assert
        assertNull(organizationNameHist.getOrganizationUid());
        assertEquals(0, organizationNameHist.getOrganizationNameSeq());
        assertEquals(0, organizationNameHist.getVersionCtrlNbr());
        assertNull(organizationNameHist.getNmTxt());
        assertNull(organizationNameHist.getNmUseCd());
        assertNull(organizationNameHist.getRecordStatusCd());
        assertNull(organizationNameHist.getDefaultNmInd());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        OrganizationNameHist organizationNameHist = new OrganizationNameHist();

        Long organizationUid = 1L;
        int organizationNameSeq = 1;
        int versionCtrlNbr = 1;
        String nmTxt = "Test Name";
        String nmUseCd = "USE_CODE";
        String recordStatusCd = "ACTIVE";
        String defaultNmInd = "Y";

        // Act
        organizationNameHist.setOrganizationUid(organizationUid);
        organizationNameHist.setOrganizationNameSeq(organizationNameSeq);
        organizationNameHist.setVersionCtrlNbr(versionCtrlNbr);
        organizationNameHist.setNmTxt(nmTxt);
        organizationNameHist.setNmUseCd(nmUseCd);
        organizationNameHist.setRecordStatusCd(recordStatusCd);
        organizationNameHist.setDefaultNmInd(defaultNmInd);

        // Assert
        assertEquals(organizationUid, organizationNameHist.getOrganizationUid());
        assertEquals(organizationNameSeq, organizationNameHist.getOrganizationNameSeq());
        assertEquals(versionCtrlNbr, organizationNameHist.getVersionCtrlNbr());
        assertEquals(nmTxt, organizationNameHist.getNmTxt());
        assertEquals(nmUseCd, organizationNameHist.getNmUseCd());
        assertEquals(recordStatusCd, organizationNameHist.getRecordStatusCd());
        assertEquals(defaultNmInd, organizationNameHist.getDefaultNmInd());
    }
}
