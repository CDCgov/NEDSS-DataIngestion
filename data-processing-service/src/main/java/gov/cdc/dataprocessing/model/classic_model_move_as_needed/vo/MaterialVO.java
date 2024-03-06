package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ManufacturedMaterialDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MaterialDT;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
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
    private Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDTCollection;

    /**
     * Other Related Entities
     */
    private Collection<EntityIdDto> theEntityIdDtoCollection = new ArrayList<>();

    /**
     * collections for role and participation object association added by John Park
     */
    public Collection<ParticipationDT> theParticipationDTCollection;
    public Collection<RoleDto> theRoleDTCollection;
    private Collection<ManufacturedMaterialDT> theManufacturedMaterialDTCollection;
}
