package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class ObservationVO  extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private ObservationDT theObservationDT = new ObservationDT();
    private Collection<ActIdDT> theActIdDTCollection;
    private Collection<ObservationReasonDT> theObservationReasonDTCollection;
    private Collection<Object> theObservationInterpDTCollection;
    private Collection<ObsValueCodedDT> theObsValueCodedDTCollection;
    private Collection<Object> theObsValueCodedModDTCollection;
    private Collection<Object> theObsValueTxtDTCollection;
    private Collection<Object> theObsValueDateDTCollection;
    private List<ObsValueNumericDT> theObsValueNumericDTCollection;
    private Collection<Object> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
    public Collection<Object> theMaterialDTCollection;

}
