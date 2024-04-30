package gov.cdc.dataprocessing.service.model;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WdsTrackerView {
    private List<WdsReport> wdsReport;
    PublicHealthCaseDT publicHealthCase;
    Long patientUid;
    Long patientParentUid;
    String patientFirstName;
    String patientLastName;
}
