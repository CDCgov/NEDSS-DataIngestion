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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
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
