package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ObservationContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private ObservationDto theObservationDto = new ObservationDto();
    private Collection<ActIdDto> theActIdDtoCollection;
    private Collection<ObservationReasonDto> theObservationReasonDtoCollection;
    private Collection<ObservationInterpDto> theObservationInterpDtoCollection;
    private Collection<ObsValueCodedDto> theObsValueCodedDtoCollection;
    private Collection<Object> theObsValueCodedModDTCollection;
    private Collection<ObsValueTxtDto> theObsValueTxtDtoCollection;
    private Collection<ObsValueDateDto> theObsValueDateDtoCollection;
    private Collection<ObsValueNumericDto> theObsValueNumericDtoCollection;
    private Collection<ActivityLocatorParticipationDto> theActivityLocatorParticipationDtoCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<ParticipationDto> theParticipationDtoCollection;
    public Collection<ActRelationshipDto> theActRelationshipDtoCollection;
    public Collection<MaterialDto> theMaterialDtoCollection;

}
