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
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class PublicHealthCaseFlowContainer {
    LabResultProxyContainer labResultProxyContainer;
    EdxLabInformationDto edxLabInformationDto;
    ObservationDto observationDto;
    Integer nbsInterfaceId;
    private WdsTrackerView wdsTrackerView = new WdsTrackerView();

}
