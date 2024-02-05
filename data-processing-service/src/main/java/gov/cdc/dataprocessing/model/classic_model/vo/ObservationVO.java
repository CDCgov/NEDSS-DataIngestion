package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dt.ObservationDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ObservationVO  extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private ObservationDT theObservationDT = new ObservationDT();
    private Collection<Object> theActIdDTCollection;
    private Collection<Object> theObservationReasonDTCollection;
    private Collection<Object> theObservationInterpDTCollection;
    private Collection<Object> theObsValueCodedDTCollection;
    private Collection<Object> theObsValueCodedModDTCollection;
    private Collection<Object> theObsValueTxtDTCollection;
    private Collection<Object> theObsValueDateDTCollection;
    private Collection<Object> theObsValueNumericDTCollection;
    private Collection<Object> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
    public Collection<Object> theMaterialDTCollection;

}
