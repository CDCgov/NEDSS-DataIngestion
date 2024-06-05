package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.material.ManufacturedMaterialDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class MaterialContainer extends BaseContainer
{


    /**
     * Data Table of Value Object
     */
    private MaterialDto theMaterialDto = new MaterialDto();

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
    public Collection<ParticipationDto> theParticipationDtoCollection;
    public Collection<RoleDto> theRoleDTCollection;
    private Collection<ManufacturedMaterialDto> theManufacturedMaterialDtoCollection;

    public MaterialContainer() {
         itDirty = false;
            itNew = true;
    }
}
