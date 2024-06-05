package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.phc.InterventionDto;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
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
