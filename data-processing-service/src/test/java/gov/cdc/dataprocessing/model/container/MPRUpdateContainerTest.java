package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.MPRUpdateContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MPRUpdateContainerTest {

    @Test
    void testConstructorAndGetters() {
        // Create a PersonContainer for mpr
        PersonContainer mpr = new PersonContainer();

        // Create a collection of PersonContainer objects
        Collection<PersonContainer> personVOs = new ArrayList<>();
        PersonContainer person1 = new PersonContainer();
        PersonContainer person2 = new PersonContainer();
        personVOs.add(person1);
        personVOs.add(person2);

        // Instantiate MPRUpdateContainer with the above mpr and personVOs
        MPRUpdateContainer container = new MPRUpdateContainer(mpr, personVOs);

        // Test getters
        assertEquals(mpr, container.getMpr());
        assertEquals(personVOs, container.getPersonVOs());
    }

    @Test
    void testSetters() {
        // Create a PersonContainer for mpr
        PersonContainer mpr = new PersonContainer();

        // Create a collection of PersonContainer objects
        Collection<PersonContainer> personVOs = new ArrayList<>();
        PersonContainer person1 = new PersonContainer();
        PersonContainer person2 = new PersonContainer();
        personVOs.add(person1);
        personVOs.add(person2);

        // Instantiate MPRUpdateContainer with null values
        MPRUpdateContainer container = new MPRUpdateContainer(null, null);

        // Test setters
        container.setMpr(mpr);
        container.setPersonVOs(personVOs);

        // Test getters again to verify setters worked
        assertEquals(mpr, container.getMpr());
        assertEquals(personVOs, container.getPersonVOs());
    }

    @Test
    void testDefaultConstructorAndGettersSetters() {
        // Create a PersonContainer for mpr
        PersonContainer mpr = new PersonContainer();

        // Create a collection of PersonContainer objects
        Collection<PersonContainer> personVOs = new ArrayList<>();
        PersonContainer person1 = new PersonContainer();
        PersonContainer person2 = new PersonContainer();
        personVOs.add(person1);
        personVOs.add(person2);

        // Use default constructor and setters
        MPRUpdateContainer container = new MPRUpdateContainer(null, null);
        container.setMpr(mpr);
        container.setPersonVOs(personVOs);

        // Test getters
        assertEquals(mpr, container.getMpr());
        assertEquals(personVOs, container.getPersonVOs());
    }
}
