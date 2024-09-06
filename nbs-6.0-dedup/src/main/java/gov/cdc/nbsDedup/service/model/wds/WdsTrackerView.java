package gov.cdc.nbsDedup.service.model.wds;

import gov.cdc.nbsDedup.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WdsTrackerView {
    private List<WdsReport> wdsReport;
    PublicHealthCaseDto publicHealthCase;
    Long patientUid;
    Long patientParentUid;
    String patientFirstName;
    String patientLastName;
}
