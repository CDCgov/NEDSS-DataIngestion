package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationProxyContainerTest {

    @Test
    void testGettersAndSetters() {
        NotificationProxyContainer container = new NotificationProxyContainer();

        Collection<Object> actRelationshipCollection = new ArrayList<>();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        NotificationContainer notificationContainer = new NotificationContainer();

        container.setTheActRelationshipDTCollection(actRelationshipCollection);
        container.setThePublicHealthCaseContainer(publicHealthCaseContainer);
        container.setTheNotificationContainer(notificationContainer);

        assertEquals(actRelationshipCollection, container.getTheActRelationshipDTCollection());
        assertEquals(publicHealthCaseContainer, container.getThePublicHealthCaseContainer());
        assertEquals(notificationContainer, container.getTheNotificationContainer());
    }
}
