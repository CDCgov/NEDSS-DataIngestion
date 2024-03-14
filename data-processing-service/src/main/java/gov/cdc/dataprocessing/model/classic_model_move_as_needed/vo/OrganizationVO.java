package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationNameDT;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class OrganizationVO extends LdfBaseVO {
//    public OrganizationDT theOrganizationDT = new OrganizationDT();
//    public Collection<OrganizationNameDT> theOrganizationNameDTCollection;
//    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDTCollection = new ArrayList<>();
//    public Collection<EntityIdDto> theEntityIdDTCollection = new ArrayList<>();

    //collections for role and participation object association added by John Park
//    public Collection<ParticipationDT> theParticipationDTCollection=new ArrayList<>();
//    public Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
//    private String sendingFacility;
//    private String sendingSystem;
//    private String localIdentifier;
//    private String role;

    public OrganizationDT theOrganizationDT = new OrganizationDT();
    public Collection<OrganizationNameDT> theOrganizationNameDTCollection;
    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection = new ArrayList<>();
    public Collection<EntityIdDto> theEntityIdDtoCollection = new ArrayList<>();

    //collections for role and participation object association added by John Park
    public Collection<ParticipationDT> theParticipationDTCollection;
    public Collection<RoleDto> theRoleDTCollection;
    private String sendingFacility;
    private String sendingSystem;
    private String localIdentifier;
    private String role;
}