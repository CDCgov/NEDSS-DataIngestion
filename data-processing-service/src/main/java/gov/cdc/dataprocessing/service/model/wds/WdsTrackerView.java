package gov.cdc.dataprocessing.service.model.wds;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("all")
public class WdsTrackerView {
    PublicHealthCaseDto publicHealthCase;
    Long patientUid;
    Long patientParentUid;
    String patientFirstName;
    String patientLastName;
    private List<WdsReport> wdsReport;
}
