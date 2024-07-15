package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ProviderDataForPrintContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProviderDataForPrintContainerTest {

    @Test
    void testGettersAndSetters() {
        ProviderDataForPrintContainer providerData = new ProviderDataForPrintContainer();

        providerData.setProviderStreetAddress1("123 Main St");
        providerData.setProviderCity("Atlanta");
        providerData.setProviderState("GA");
        providerData.setProviderZip("30301");
        providerData.setProviderPhone("123-456-7890");
        providerData.setProviderPhoneExtension("1234");
        providerData.setFacilityName("Main Facility");
        providerData.setFacilityCity("Atlanta");
        providerData.setFacilityState("GA");
        providerData.setFacilityAddress1("123 Main St");
        providerData.setFacilityAddress2("Suite 100");
        providerData.setFacility("Main Facility");
        providerData.setFacilityZip("30301");
        providerData.setFacilityPhoneExtension("5678");
        providerData.setFacilityPhone("098-765-4321");

        assertEquals("123 Main St", providerData.getProviderStreetAddress1());
        assertEquals("Atlanta", providerData.getProviderCity());
        assertEquals("GA", providerData.getProviderState());
        assertEquals("30301", providerData.getProviderZip());
        assertEquals("123-456-7890", providerData.getProviderPhone());
        assertEquals("1234", providerData.getProviderPhoneExtension());
        assertEquals("Main Facility", providerData.getFacilityName());
        assertEquals("Atlanta", providerData.getFacilityCity());
        assertEquals("GA", providerData.getFacilityState());
        assertEquals("123 Main St", providerData.getFacilityAddress1());
        assertEquals("Suite 100", providerData.getFacilityAddress2());
        assertEquals("Main Facility", providerData.getFacility());
        assertEquals("30301", providerData.getFacilityZip());
        assertEquals("5678", providerData.getFacilityPhoneExtension());
        assertEquals("098-765-4321", providerData.getFacilityPhone());
    }
}
