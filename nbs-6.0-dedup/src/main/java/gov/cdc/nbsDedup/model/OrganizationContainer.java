package gov.cdc.nbsDedup.model;


import gov.cdc.nbsDedup.model.container.model.LdfBaseContainer;
import gov.cdc.nbsDedup.model.dto.entity.EntityIdDto;
import gov.cdc.nbsDedup.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.nbsDedup.model.dto.entity.RoleDto;
import gov.cdc.nbsDedup.model.dto.organization.OrganizationDto;
import gov.cdc.nbsDedup.model.dto.organization.OrganizationNameDto;
import gov.cdc.nbsDedup.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class OrganizationContainer extends LdfBaseContainer {
    public OrganizationDto theOrganizationDto = new OrganizationDto();
    public Collection<OrganizationNameDto> theOrganizationNameDtoCollection;
    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection = new ArrayList<>();
    public Collection<EntityIdDto> theEntityIdDtoCollection = new ArrayList<>();
    //collections for role and participation object association added by John Park
    public Collection<ParticipationDto> theParticipationDtoCollection;
    public Collection<RoleDto> theRoleDTCollection;
    private String sendingFacility;
    private String sendingSystem;
    private String localIdentifier;
    private String role;
}
