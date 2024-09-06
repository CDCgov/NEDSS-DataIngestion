package gov.cdc.nbsDedup.model;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.dto.phc.InterventionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class InterventionContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private InterventionDto theInterventionDto = new InterventionDto();
    private Collection<Object> theProcedure1DTCollection;
    private Collection<Object> theSubstanceAdministrationDTCollection;
    private Collection<Object> theActIdDTCollection;
    private Collection<Object> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
}
