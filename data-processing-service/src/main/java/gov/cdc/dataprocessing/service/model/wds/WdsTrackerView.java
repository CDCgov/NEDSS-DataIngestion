package gov.cdc.dataprocessing.service.model.wds;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
