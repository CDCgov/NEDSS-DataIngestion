package gov.cdc.nbsDedup.service.model.phc;


import gov.cdc.nbsDedup.model.container.model.LabResultProxyContainer;
import gov.cdc.nbsDedup.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.nbsDedup.model.dto.observation.ObservationDto;
import gov.cdc.nbsDedup.service.model.wds.WdsTrackerView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicHealthCaseFlowContainer {
    LabResultProxyContainer labResultProxyContainer;
    EdxLabInformationDto edxLabInformationDto;
    ObservationDto observationDto;
    Integer nbsInterfaceId;
    private WdsTrackerView wdsTrackerView = new WdsTrackerView();

}
