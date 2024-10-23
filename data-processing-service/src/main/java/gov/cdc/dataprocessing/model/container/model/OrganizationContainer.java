package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class OrganizationContainer extends LdfBaseContainer {
//    public OrganizationDto theOrganizationDto = new OrganizationDto();
//    public Collection<OrganizationNameDto> theOrganizationNameDtoCollection;
//    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDTCollection = new ArrayList<>();
//    public Collection<EntityIdDto> theEntityIdDTCollection = new ArrayList<>();

    //collections for role and participation object association added by John Park
//    public Collection<ParticipationDto> theParticipationDtoCollection=new ArrayList<>();
//    public Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
//    private String sendingFacility;
//    private String sendingSystem;
//    private String localIdentifier;
//    private String role;

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