package gov.cdc.dataprocessing.service.model.phc;

import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.service.model.wds.WdsTrackerView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class PublicHealthCaseFlowContainer {
    LabResultProxyContainer labResultProxyContainer;
    EdxLabInformationDto edxLabInformationDto;
    ObservationDto observationDto;
    Integer nbsInterfaceId;
    private WdsTrackerView wdsTrackerView = new WdsTrackerView();

}
