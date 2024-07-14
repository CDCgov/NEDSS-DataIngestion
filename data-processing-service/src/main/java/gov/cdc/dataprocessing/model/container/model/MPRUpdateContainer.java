package gov.cdc.dataprocessing.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@SuppressWarnings("all")
public class MPRUpdateContainer {
    private PersonContainer mpr = null;
    private Collection<PersonContainer> personVOs = null;

    /**
     This is the constructor for the class.
     */
    public MPRUpdateContainer(PersonContainer mpr, Collection<PersonContainer>  personVOs)
    {
        this.mpr = mpr;
        this.personVOs = personVOs;
    }
}
