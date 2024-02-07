package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.EntityIdDT;
import gov.cdc.dataprocessing.model.classic_model.dto.MaterialDT;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class MaterialVO extends AbstractVO
{
    /**
     * Variable to keep track of changes in the Value Object.
     */
    private boolean itDirty = false;

    /**
     * Variable to keep track whether Value Object is new or not.
     */
    private boolean itNew = true;

    /**
     * Data Table of Value Object
     */
    private MaterialDT theMaterialDT = new MaterialDT ();

    /**
     * Related Locators
     */
    private Collection<Object> theEntityLocatorParticipationDTCollection;

    /**
     * Other Related Entities
     */
    private List<EntityIdDT> theEntityIdDTCollection = new ArrayList<>();

    /**
     * collections for role and participation object association added by John Park
     */
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;
    private Collection<Object> theManufacturedMaterialDTCollection;
}
