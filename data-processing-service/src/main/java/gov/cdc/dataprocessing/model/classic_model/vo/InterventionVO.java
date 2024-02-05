package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dt.InterventionDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class InterventionVO extends AbstractVO{
    private static final long serialVersionUID = 1L;
    private InterventionDT theInterventionDT = new InterventionDT();
    private Collection<Object> theProcedure1DTCollection;
    private Collection<Object> theSubstanceAdministrationDTCollection;
    private Collection<Object> theActIdDTCollection;
    private Collection<Object> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
}
