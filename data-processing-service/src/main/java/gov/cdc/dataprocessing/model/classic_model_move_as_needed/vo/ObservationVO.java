package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ObservationVO  extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private ObservationDT theObservationDT = new ObservationDT();
    private Collection<ActIdDto> theActIdDtoCollection;
    private Collection<ObservationReasonDT> theObservationReasonDTCollection;
    private Collection<ObservationInterpDT> theObservationInterpDTCollection;
    private Collection<ObsValueCodedDT> theObsValueCodedDTCollection;
    private Collection<Object> theObsValueCodedModDTCollection;
    private Collection<ObsValueTxtDT> theObsValueTxtDTCollection;
    private Collection<ObsValueDateDT> theObsValueDateDTCollection;
    private Collection<ObsValueNumericDT> theObsValueNumericDTCollection;
    private Collection<ActivityLocatorParticipationDT> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<ParticipationDT> theParticipationDTCollection;
    public Collection<ActRelationshipDT> theActRelationshipDTCollection;
    public Collection<MaterialDT> theMaterialDTCollection;

}
