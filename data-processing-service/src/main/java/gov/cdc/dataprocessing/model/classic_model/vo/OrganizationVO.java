package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.EntityIdDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EntityLocatorParticipationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.OrganizationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.OrganizationNameDT;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class OrganizationVO extends LdfBaseVO {
    public OrganizationDT theOrganizationDT = new OrganizationDT();
    public Collection<OrganizationNameDT> theOrganizationNameDTCollection;
    public Collection<EntityLocatorParticipationDT> theEntityLocatorParticipationDTCollection = new ArrayList<>();
    public Collection<EntityIdDT> theEntityIdDTCollection = new ArrayList<>();

    //collections for role and participation object association added by John Park
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;
    private String sendingFacility;
    private String sendingSystem;
    private String localIdentifier;
    private String role;
}
