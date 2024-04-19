package gov.cdc.dataprocessing.service.model;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.msgoute.NbsInterfaceDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
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
