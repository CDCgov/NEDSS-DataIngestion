package gov.cdc.dataingestion.reportstatus.model;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxLogStatus {
  private EdxActivityLog edxActivityLog;
  private List<EdxActivityDetailLog> edxActivityDetailLogList = new ArrayList<>();
}
