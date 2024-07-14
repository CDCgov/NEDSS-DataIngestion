package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MixTestCon {

    @Test
    void testDocumentSummaryContainer() {
        DocumentSummaryContainer entity = new DocumentSummaryContainer();
        entity.setProgAreaCdOverride("TEST");
        entity.setVersionCtrlNbr(null);
        assertNotNull(entity.getSuperclass());
    }

    @Test
    void testLabReportSum() {
        LabReportSummaryContainer entity = new LabReportSummaryContainer();
        entity.setLastChgReasonCd(null);

        assertNull(entity.getLastChgReasonCd());
    }

    @Test
    void testLdf() {
        LdfBaseContainer entity = new LdfBaseContainer();
        entity.setLdfUids(null);
        assertNull(entity.getLdfUids());
    }

    @Test
    void testNotiSum() {
        NotificationSummaryContainer entity = new NotificationSummaryContainer();
        entity.setLastChgUserId(null);
        entity.setLastChgReasonCd(null);
        entity.setStatusCd(null);
        entity.setStatusTime(null);
        entity.setNotificationUid(null);

        assertNotNull(entity.getSuperclass());
        assertNull(entity.getVersionCtrlNbr());

        assertNull(entity.getVersionCtrlNbr());
        assertNull(entity.getLastChgUserId());
        assertNull(entity.getLastChgReasonCd());
        assertNull(entity.getStatusCd());
        assertNull(entity.getStatusTime());
        assertNull(entity.getNotificationUid());

    }

    @Test
    void testOrg() {
        OrganizationContainer entity = new OrganizationContainer();

        entity.setSendingFacility("TEST");
        entity.setSendingSystem("TEST");

        assertNotNull(entity.getSendingFacility());
        assertNotNull(entity.getSendingSystem());

    }

    @Test
    void testTreat() {
        TreatmentContainer entity = new TreatmentContainer();
        assertNotNull(entity.getSuperclass());
    }

}
